package com.pkg.littlewriter.domain.generativeAi.stableDiffusion;

import java.io.IOException;

public interface StableDiffusion {
    ImageResponse generateFromPrompt(TextToImageRequest textToImageRequest) throws IOException, InterruptedException, StableDiffusionException;
    ImageResponse generateFromImage(ImageToImageRequest imageToImageRequest) throws IOException, InterruptedException, StableDiffusionException;
    ImageResponse fetchImageResponse(ImageResponse imageResponse) throws IOException, InterruptedException;
    ImageResponse generateFromImageCanny(ImageToImageRequest imageToImageRequest) throws IOException, InterruptedException, StableDiffusionException;
}
