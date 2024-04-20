package com.pkg.littlewriter.domain.generativeAi.stableDiffusion;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ImageResponse {
    private boolean isDone;
    private String imageUrl;
    private Long id;
}
