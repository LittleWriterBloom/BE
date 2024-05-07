package com.pkg.littlewriter.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CharacterCreationRequestDTO {
    private String name;
    private String personality;
    private String base64Image;
    private String description;
    private ImageType imageType;
    private String imageUrl;
    private String originImageUrl;
    private String base64OriginImage;
    public enum ImageType {
        BASE_64, URL
    }
}
