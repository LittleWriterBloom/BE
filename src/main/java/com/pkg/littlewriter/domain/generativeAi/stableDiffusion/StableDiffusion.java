package com.pkg.littlewriter.domain.generativeAi.stableDiffusion;

import java.io.IOException;

public interface StableDiffusion {
    ImageResponse generateFromPrompt(TextToImageRequest textToImageRequest) throws IOException, InterruptedException;
    ImageResponse generateFromImage(ImageToImageRequest imageToImageRequest) throws IOException, InterruptedException;
    ImageResponse fetchImageResponse(ImageResponse imageResponse) throws IOException, InterruptedException;
}
