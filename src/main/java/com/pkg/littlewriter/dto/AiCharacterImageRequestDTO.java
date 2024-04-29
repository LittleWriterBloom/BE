package com.pkg.littlewriter.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class AiCharacterImageRequestDTO {
    private String base64Image;
    private String imageUrl;
    private String prompt;
    private CharacterCreationRequestDTO.ImageType imageType;
    public enum ImageType {
        BASE_64, URL
    }
}
