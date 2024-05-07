package com.pkg.littlewriter.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.pkg.littlewriter.domain.generativeAi.GenerativeAiResponse;
import com.pkg.littlewriter.domain.generativeAi.Jsonable;
import com.pkg.littlewriter.domain.generativeAi.UrlJsonable;
import com.pkg.littlewriter.domain.generativeAi.openAi.OpenAiException;
import com.pkg.littlewriter.domain.generativeAi.openAi.image.CharacterImageKeywordExtractor;
import com.pkg.littlewriter.domain.generativeAi.openAi.image.CharacterImageToTextInputJsonable;
import com.pkg.littlewriter.domain.generativeAi.openAiModels.ImageToTextGenerator;
import com.pkg.littlewriter.domain.generativeAi.openAiModels.SimpleWordTranslator;
import com.pkg.littlewriter.domain.generativeAi.others.BackgroundRemoveApi;
import com.pkg.littlewriter.domain.generativeAi.others.BackgroundRemoveResponse;
import com.pkg.littlewriter.domain.generativeAi.stableDiffusion.ImageResponse;
import com.pkg.littlewriter.domain.generativeAi.stableDiffusion.ImageToImageRequest;
import com.pkg.littlewriter.domain.generativeAi.stableDiffusion.StableDiffusion;
import com.pkg.littlewriter.domain.generativeAi.stableDiffusion.StableDiffusionException;
import com.pkg.littlewriter.dto.*;
import com.pkg.littlewriter.domain.model.CharacterEntity;
import com.pkg.littlewriter.domain.model.MemberEntity;
import com.pkg.littlewriter.security.CustomUserDetails;
import com.pkg.littlewriter.service.CharacterService;
import com.pkg.littlewriter.service.S3BucketService;
import com.pkg.littlewriter.service.UserService;
import com.pkg.littlewriter.utils.S3DirectoryEnum;
import com.pkg.littlewriter.utils.S3File;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/character")
public class CharacterController {
    @Autowired
    private UserService userService;
    @Autowired
    private CharacterService characterService;
    @Autowired
    private S3BucketService s3BucketService;
    @Autowired
    private StableDiffusion stableDiffusion;
    @Autowired
    private CharacterImageKeywordExtractor characterImageKeywordExtractor;
    @Autowired
    private SimpleWordTranslator wordTranslator;
    @Autowired
    private BackgroundRemoveApi backgroundRemover;

    @PostMapping()
    public ResponseEntity<?> createCharacter(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody CharacterCreationRequestDTO characterCreationRequestDTO) throws IOException {
        MemberEntity memberEntity = userService.getById(userDetails.getId());
        S3File characterImageFile;
        S3File originCharacterImageFile;
        if (characterCreationRequestDTO.getImageType().equals(CharacterCreationRequestDTO.ImageType.BASE_64)) {
            characterImageFile = s3BucketService.uploadFromBase64(characterCreationRequestDTO.getBase64Image(), S3DirectoryEnum.CHARACTER);
            originCharacterImageFile = s3BucketService.uploadFromBase64(characterCreationRequestDTO.getBase64OriginImage(), S3DirectoryEnum.CHARACTER);
        } else {
            characterImageFile = s3BucketService.uploadTemporaryFromUrl(characterCreationRequestDTO.getImageUrl(), S3DirectoryEnum.CHARACTER);
            originCharacterImageFile = s3BucketService.uploadTemporaryFromUrl(characterCreationRequestDTO.getOriginImageUrl(), S3DirectoryEnum.CHARACTER);
        }
//        CharacterImageToTextInputJsonable inputJsonable = CharacterImageToTextInputJsonable.builder()
//                .imageUrl(characterImageFile.getUrl())
//                .description(characterCreationRequestDTO.getDescription())
//                .build();
//            String appearanceKeyword = characterImageKeywordExtractor.getResponse(inputJsonable).getMessage();
        CharacterEntity newCharacter = CharacterEntity.builder()
                .memberId(memberEntity.getId())
                .name(characterCreationRequestDTO.getName())
                .imageUrl(characterImageFile.getUrl())
                .originImageUrl(originCharacterImageFile.getUrl())
                .personality(characterCreationRequestDTO.getPersonality())
                .userDescription(characterCreationRequestDTO.getDescription())
//                    .appearanceKeywords(appearanceKeyword)
                .build();
        CharacterDTO characters = new CharacterDTO(characterService.create(newCharacter));
        ResponseDTO<CharacterDTO> responseDTO = ResponseDTO.<CharacterDTO>builder()
                .data(List.of(characters))
                .build();
        return ResponseEntity.ok().body(responseDTO);

    }

    @GetMapping
    public ResponseEntity<?> retrieveCharacters(@AuthenticationPrincipal CustomUserDetails userDetails) {
        MemberEntity memberEntity = userService.getById(userDetails.getId());
        List<CharacterEntity> characterEntities = characterService.retrieveByUserId(memberEntity.getId());
        List<CharacterDTO> characterDTOs = characterEntities.stream()
                .map(CharacterDTO::new)
                .toList();
        ResponseDTO<CharacterDTO> responseDTO = ResponseDTO.<CharacterDTO>builder()
                .data(characterDTOs)
                .build();
        return ResponseEntity.ok().body(responseDTO);
    }

    @PostMapping("/ai")
    public ResponseEntity<?> convert(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody AiCharacterImageRequestDTO characterCreationRequestDTO) throws IOException, InterruptedException, StableDiffusionException {
        S3File originCharacterImage;
        if (characterCreationRequestDTO.getImageType().equals(CharacterCreationRequestDTO.ImageType.BASE_64)) {
            originCharacterImage = s3BucketService.uploadFromBase64(characterCreationRequestDTO.getBase64Image(), S3DirectoryEnum.TEMPORARY);
        } else {
            originCharacterImage = s3BucketService.uploadTemporaryFromUrl(characterCreationRequestDTO.getImageUrl(), S3DirectoryEnum.TEMPORARY);
        }
        String englishDescription = wordTranslator.getResponse(new Jsonable() {
            @Override
            public String toJsonString() {
                return characterCreationRequestDTO.getPrompt();
            }
        }).getMessage();
        String prompt = englishDescription + ", whole body";
        ImageToImageRequest request = ImageToImageRequest.builder()
                .prompt(prompt)
                .imageUrl(originCharacterImage.getUrl())
                .build();
        ImageResponse response = stableDiffusion.generateFromImage(request);
        while (!response.isDone()) {
            response = stableDiffusion.fetchImageResponse(response);
        }
        BackgroundRemoveResponse backgroundRemoveResponse = backgroundRemover.removeBackground(response.getImageUrl());
        S3File stableDiffusionImage = s3BucketService.uploadTemporaryFromUrl(backgroundRemoveResponse.getHighResolution(), S3DirectoryEnum.TEMPORARY);
        CharacterImageGenerationDTO characterImageGenerationDTO = CharacterImageGenerationDTO.builder()
                .originUrl(originCharacterImage.getUrl())
                .aiGeneratedImageUrl(stableDiffusionImage.getUrl())
                .prompt(prompt)
                .build();
        ResponseDTO<CharacterImageGenerationDTO> responseDTO = ResponseDTO.<CharacterImageGenerationDTO>builder()
                .data(List.of(characterImageGenerationDTO))
                .build();
        return ResponseEntity.ok().body(responseDTO);
    }

    @PutMapping("/image")
    public ResponseEntity<?> updateCharacterImage(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody CharacterCreationRequestDTO characterCreationRequestDTO, @RequestParam(value = "id") Long characterId) {
        S3File characterImageFile = s3BucketService.uploadFromBase64(characterCreationRequestDTO.getBase64Image(), S3DirectoryEnum.CHARACTER);
        CharacterEntity characterEntity = characterService.getById(characterId);
        s3BucketService.deleteFileFromS3(new S3File(characterEntity.getImageUrl()));
        characterEntity.setImageUrl(characterImageFile.getUrl());
        List<CharacterDTO> characters = characterService.update(characterEntity)
                .stream()
                .map(CharacterDTO::new)
                .toList();
        ResponseDTO<CharacterDTO> responseDTO = ResponseDTO.<CharacterDTO>builder()
                .data(characters)
                .build();
        return ResponseEntity.ok().body(responseDTO);
    }

    @PutMapping("/details")
    public ResponseEntity<?> updateCharacterImage(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody CharacterDTO characterDTO) {
        CharacterEntity targetCharacterEntity = characterService.getById(characterDTO.getId());
        targetCharacterEntity.setPersonality(characterDTO.getPersonality());
        targetCharacterEntity.setName(characterDTO.getName());
        List<CharacterDTO> characters = characterService.update(targetCharacterEntity)
                .stream()
                .map(CharacterDTO::new)
                .toList();
        ResponseDTO<CharacterDTO> responseDTO = ResponseDTO.<CharacterDTO>builder()
                .data(characters)
                .build();
        return ResponseEntity.ok().body(responseDTO);
    }

    @DeleteMapping
    public ResponseEntity<?> deleteCharacter(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody CharacterDTO characterDTO) {
        try {
            CharacterEntity targetEntity = characterService.getById(characterDTO.getId());
            targetEntity.setMemberId(userDetails.getId());
            s3BucketService.deleteFileFromS3(new S3File(targetEntity.getImageUrl()));
            List<CharacterEntity> characterEntities = characterService.delete(targetEntity);
            List<CharacterDTO> characterDTOS = characterEntities.stream()
                    .map(CharacterDTO::new)
                    .toList();
            ResponseDTO<CharacterDTO> responseDTO = ResponseDTO.<CharacterDTO>builder()
                    .data(characterDTOS)
                    .build();
            return ResponseEntity.ok().body(responseDTO);
        } catch (Exception e) {
            log.warn(e.getMessage());
            ResponseDTO<CharacterDTO> responseDTO = ResponseDTO.<CharacterDTO>builder()
                    .error(e.getMessage())
                    .build();
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }
}
