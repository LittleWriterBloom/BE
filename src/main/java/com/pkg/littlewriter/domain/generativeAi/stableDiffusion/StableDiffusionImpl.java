package com.pkg.littlewriter.domain.generativeAi.stableDiffusion;

import com.pkg.littlewriter.domain.generativeAi.stableDiffusion.api.StableDiffusionApi;
import com.pkg.littlewriter.domain.generativeAi.stableDiffusion.api.request.FetchRequestBody;
import com.pkg.littlewriter.domain.generativeAi.stableDiffusion.api.request.ImageToImageRequestBody;
import com.pkg.littlewriter.domain.generativeAi.stableDiffusion.api.request.TextToImageRequestBody;
import com.pkg.littlewriter.domain.generativeAi.stableDiffusion.api.response.*;
import com.pkg.littlewriter.domain.generativeAi.stableDiffusion.api.response.fetch.FetchSuccessResponse;
import com.pkg.littlewriter.domain.generativeAi.stableDiffusion.api.response.imageToimage.ImageToImageProcessingResponse;
import com.pkg.littlewriter.domain.generativeAi.stableDiffusion.api.response.textToimage.TextToImageProcessingResponse;
import com.pkg.littlewriter.domain.generativeAi.stableDiffusion.api.response.textToimage.TextToImageSuccessResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class StableDiffusionImpl implements StableDiffusion {
    @Autowired
    private StableDiffusionApi api;

    @Override
    public ImageResponse generateFromPrompt(TextToImageRequest textToImageRequest) throws IOException, InterruptedException {
        TextToImageRequestBody text2image = TextToImageRequestBody.builder()
                .key(api.getKey())
                .prompt(textToImageRequest.getPrompt())
                .modelId("v1-5-pruned-v6")
                .loraModel("kids-illustration")
                .height("512")
                .width("512")
                .samples("1")
                .loraStrength(1f)
                .build();
        Response<?> response = api.getTextToImageResponse(text2image);
        if (response.isDone()) {
            TextToImageSuccessResponse successStatus = (TextToImageSuccessResponse) response.getInstance();
            return ImageResponse.builder()
                    .isDone(true)
                    .imageUrl(successStatus.getOutput().get(0))
                    .id(successStatus.getId())
                    .build();
        }
        TextToImageProcessingResponse processingStatus = (TextToImageProcessingResponse) response.getInstance();
        return ImageResponse.builder()
                .isDone(false)
                .imageUrl(processingStatus.getFutureLinks().get(0))
                .id(processingStatus.getId())
                .build();
    }

    @Override
    public ImageResponse generateFromImage(ImageToImageRequest imageToImageRequest) throws IOException, InterruptedException {
        ImageToImageRequestBody requestBody = ImageToImageRequestBody.builder()
                .key(api.getKey())
                .prompt(imageToImageRequest.getPrompt())
                .height("512")
                .width("512")
                .upscale("2")
                .initImage(imageToImageRequest.getImageUrl())
                .modelId("v1-5-pruned-v6")
                .controlnetModel("scribble")
                .controlnetType("scribble")
                .loraModel("kids-illustration")
                .strength(1.0f)
                .scheduler("EulerDiscreteScheduler")
                .samples("1")
                .tomesd("yes")
                .build();
        Response<?> response = api.getImageToImageResponse(requestBody);
        if (response.isDone()) {
            TextToImageSuccessResponse successStatus = (TextToImageSuccessResponse) response.getInstance();
            return ImageResponse.builder()
                    .isDone(true)
                    .imageUrl(successStatus.getOutput().get(0))
                    .id(successStatus.getId())
                    .build();
        }
        ImageToImageProcessingResponse processingStatus = (ImageToImageProcessingResponse) response.getInstance();
        return ImageResponse.builder()
                .isDone(false)
                .imageUrl(processingStatus.getFutureLinks().get(0))
                .id(processingStatus.getId())
                .build();
    }

    @Override
    public ImageResponse fetchImageResponse(ImageResponse imageResponse) throws IOException, InterruptedException {
        FetchRequestBody fetchRequestBody = FetchRequestBody.builder()
                .key(api.getKey())
                .requestId(imageResponse.getId())
                .build();
        Response<?> response = api.getFetchResponse(fetchRequestBody);
        if (response.isDone()) {
            FetchSuccessResponse fetchSuccess = (FetchSuccessResponse) response.getInstance();
            return ImageResponse.builder()
                    .isDone(true)
                    .imageUrl(fetchSuccess.getOutput().get(0))
                    .id(fetchSuccess.getId())
                    .build();
        }
        return imageResponse;
    }
}
