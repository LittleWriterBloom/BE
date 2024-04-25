package com.pkg.littlewriter.domain.generativeAi.stableDiffusion;

import com.pkg.littlewriter.domain.generativeAi.GenerativeAiResponse;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ImageResponse implements GenerativeAiResponse {
    private boolean isDone;
    private String imageUrl;
    private Long id;

    @Override
    public String getMessage() {
        return imageUrl;
    }
}
