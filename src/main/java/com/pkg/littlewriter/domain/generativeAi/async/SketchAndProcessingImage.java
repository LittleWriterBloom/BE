package com.pkg.littlewriter.domain.generativeAi.async;

import com.pkg.littlewriter.domain.generativeAi.stableDiffusion.ImageResponse;
import lombok.Data;

@Data
public class SketchAndProcessingImage {
    private String sketchImageUrl;
    private ImageResponse processingImage;

    public SketchAndProcessingImage (String sketchImageUrl, ImageResponse processingImage) {
        this.sketchImageUrl = sketchImageUrl;
        this.processingImage = processingImage;
    }
}
