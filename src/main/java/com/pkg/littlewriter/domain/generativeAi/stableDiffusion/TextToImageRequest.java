package com.pkg.littlewriter.domain.generativeAi.stableDiffusion;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TextToImageRequest {
    private String prompt;
}
