package com.pkg.littlewriter.domain.generativeAi.stableDiffusion;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ImageToImageRequest {
    private String imageUrl;
    private String prompt;
}
