package com.pkg.littlewriter.domain.generativeAi.stableDiffusion;

import com.pkg.littlewriter.domain.generativeAi.openAi.OpenAiException;
import com.pkg.littlewriter.domain.generativeAi.stableDiffusion.api.StableDiffusionApi;
import com.pkg.littlewriter.domain.generativeAi.stableDiffusion.api.request.FetchRequestBody;
import com.pkg.littlewriter.domain.generativeAi.stableDiffusion.api.request.ImageToImageRequestBody;
import com.pkg.littlewriter.domain.generativeAi.stableDiffusion.api.request.TextToImageRequestBody;
import com.pkg.littlewriter.domain.generativeAi.stableDiffusion.api.response.*;
import com.pkg.littlewriter.domain.generativeAi.stableDiffusion.api.response.fetch.FetchSuccessResponse;
import com.pkg.littlewriter.domain.generativeAi.stableDiffusion.api.response.imageToimage.ImageToImageProcessingResponse;
import com.pkg.littlewriter.domain.generativeAi.stableDiffusion.api.response.imageToimage.ImageToImageSuccessResponse;
import com.pkg.littlewriter.domain.generativeAi.stableDiffusion.api.response.textToimage.TextToImageProcessingResponse;
import com.pkg.littlewriter.domain.generativeAi.stableDiffusion.api.response.textToimage.TextToImageSuccessResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class StableDiffusionImpl implements StableDiffusion {
    @Autowired
    private StableDiffusionApi api;

    @Override
    public ImageResponse generateFromPrompt(TextToImageRequest textToImageRequest) throws StableDiffusionException {
        try {

            TextToImageRequestBody text2image = TextToImageRequestBody.builder()
                    .key(api.getKey())
                    .prompt(textToImageRequest.getPrompt())
                    .modelId("v1-5-pruned-v6")
                    .loraModel("kids-illustration")
                    .height("512")
                    .width("512")
                    .samples("1")
                    .upscale("2")
                    .negativePrompt("duplicated characters, Bad anatomy, deformed, extra arms, extra limbs, extra hands, fused fingers, gross proportions, low quality")
                    .selfAttention("yes")
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
        } catch (InterruptedException | IOException ie) {
            log.error("InterruptedException: ", ie);
            Thread.currentThread().interrupt();
            throw new StableDiffusionException(ie.getMessage());
        }
    }

    @Override
    public ImageResponse generateFromImage(ImageToImageRequest imageToImageRequest) throws StableDiffusionException {
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
                .controlnetConditioningScale(0.5f)
                .loraModel("kids-illustration")
                .autoHint("yes")
                .strength(1.0f)
                .guessMode("no")
                .useKarrasSigmas("yes")
                .scheduler("EulerDiscreteScheduler")
                .multiLingual("yes")
                .samples("1")
                .tomesd("yes")
                .guidanceScale(7.5f)
                .build();
        return getImageResponse(requestBody);
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

    @Override
    public ImageResponse generateFromImageCanny(ImageToImageRequest imageToImageRequest) throws StableDiffusionException {
        ImageToImageRequestBody requestBody = ImageToImageRequestBody.builder()
                .key(api.getKey())
                .prompt(imageToImageRequest.getPrompt())
                .height("512")
                .width("512")
                .upscale("2")
                .initImage(imageToImageRequest.getImageUrl())
                .modelId("v1-5-pruned-v6")
                .controlnetModel("canny")
                .controlnetType("canny")
                .controlnetConditioningScale(0.5f)
                .loraModel("kids-illustration")
                .autoHint("yes")
                .strength(1.0f)
                .guessMode("no")
                .useKarrasSigmas("yes")
                .scheduler("EulerDiscreteScheduler")
                .multiLingual("yes")
                .samples("1")
                .tomesd("yes")
                .guidanceScale(12f)
                .clipSkip(3)
                .instantResponse("yes")
                .build();
        return getImageResponse(requestBody);
    }

    private ImageResponse getImageResponse(ImageToImageRequestBody requestBody) throws StableDiffusionException {
        try {
            Response<?> response = api.getImageToImageResponse(requestBody);
            if (response.isDone()) {
                ImageToImageSuccessResponse successStatus = (ImageToImageSuccessResponse) response.getInstance();
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
        } catch (InterruptedException | IOException ie) {
            log.error("InterruptedException: ", ie);
            Thread.currentThread().interrupt();
            throw new StableDiffusionException(ie.getMessage());
        }
    }
}
