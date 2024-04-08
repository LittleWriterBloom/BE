package com.pkg.littlewriter.controller;

import com.pkg.littlewriter.dto.CharacterCreationRequestDTO;
import com.pkg.littlewriter.dto.CharacterDTO;
import com.pkg.littlewriter.dto.ResponseDTO;
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
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/character")
public class CharacterController {
    @Autowired
    UserService userService;
    @Autowired
    CharacterService characterService;
    @Autowired
    S3BucketService s3BucketService;

    @PostMapping()
    public ResponseEntity<?> createCharacter(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody CharacterCreationRequestDTO characterCreationRequestDTO) {
        MemberEntity memberEntity = userService.getById(userDetails.getId());
        S3File characterImageFile = s3BucketService.uploadFromBase64(characterCreationRequestDTO.getBase64Image(), S3DirectoryEnum.CHARACTER);
        CharacterEntity newCharacter = CharacterEntity.builder()
                .memberId(memberEntity.getId())
                .name(characterCreationRequestDTO.getName())
                .imageUrl(characterImageFile.getUrl())
                .personality(characterCreationRequestDTO.getPersonality())
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
