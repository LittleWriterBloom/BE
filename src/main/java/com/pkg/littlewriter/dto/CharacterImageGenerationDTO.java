package com.pkg.littlewriter.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CharacterImageGenerationDTO {
    private String originUrl;
    private String aiGeneratedImageUrl;
    private String prompt;
}
