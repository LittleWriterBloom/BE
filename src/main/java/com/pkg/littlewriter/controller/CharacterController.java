package com.pkg.littlewriter.controller;

import com.pkg.littlewriter.dto.CharacterDTO;
import com.pkg.littlewriter.dto.ResponseDTO;
import com.pkg.littlewriter.model.CharacterEntity;
import com.pkg.littlewriter.model.MemberEntity;
import com.pkg.littlewriter.security.CustomUserDetails;
import com.pkg.littlewriter.service.CharacterService;
import com.pkg.littlewriter.service.UserService;
import com.pkg.littlewriter.utils.S3BucketUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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
    S3BucketUtils s3BucketUtils;

    @PostMapping()
    public ResponseEntity<?> createCharacter(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestParam(value="image") MultipartFile image, CharacterDTO characterDTO) throws IOException {
        MemberEntity memberEntity = userService.getById(userDetails.getId());
        String uploadName = userDetails.getUsername() + "/character/" + UUID.randomUUID() + ".png";
        s3BucketUtils.uploadToS3Bucket(image, uploadName);
        CharacterEntity newCharacter = CharacterEntity.builder()
                .memberId(memberEntity.getId())
                .name(characterDTO.getName())
                .imageUrl(s3BucketUtils.getBucketEndpoint() + uploadName)
                .personality(characterDTO.getPersonality())
                .build();
        List<CharacterDTO> characters = characterService.create(newCharacter)
                .stream()
                .map(CharacterDTO::new)
                .toList();
        ResponseDTO<CharacterDTO> responseDTO = ResponseDTO.<CharacterDTO>builder()
                .data(characters)
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
    public ResponseEntity<?> updateCharacterImage(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestParam(value="image") MultipartFile image, @RequestParam(value="id") Long characterId) throws IOException {
        String uploadName = userDetails.getUsername() + "/character/" + UUID.randomUUID() + ".png";
        s3BucketUtils.uploadToS3Bucket(image, uploadName);
        CharacterEntity characterEntity = characterService.getById(characterId);
        characterEntity.setImageUrl(s3BucketUtils.getBucketEndpoint() + uploadName);
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
    public ResponseEntity<?> updateCharacterImage(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody CharacterDTO characterDTO){
        CharacterEntity targetCharacterEntity = characterService.getById(characterDTO.getId());
        targetCharacterEntity.setPersonality(characterDTO.getPersonality());
        targetCharacterEntity.setName(characterDTO.getName());
        List<CharacterDTO> characters = characterService.update(targetCharacterEntity)
                .stream()
                .map(CharacterDTO::new)
                .collect(Collectors.toList());
        ResponseDTO<CharacterDTO> responseDTO = ResponseDTO.<CharacterDTO>builder()
                .data(characters)
                .build();
        return ResponseEntity.ok().body(responseDTO);
    }

    @DeleteMapping
    public ResponseEntity<?> deleteCharacter(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody CharacterDTO characterDTO) {
        try{
            MemberEntity memberEntity = userService.getById(userDetails.getId());
            log.info(characterDTO.getId().toString());
            CharacterEntity targetEntity = CharacterEntity.builder()
                    .id(characterDTO.getId())
                    .name(characterDTO.getName())
                    .personality(characterDTO.getPersonality())
                    .imageUrl(characterDTO.getImageUrl())
                    .build();
            targetEntity.setMemberId(memberEntity.getId());
            log.info(targetEntity.getId().toString());
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
