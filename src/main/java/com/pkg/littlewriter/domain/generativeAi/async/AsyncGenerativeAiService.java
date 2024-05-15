package com.pkg.littlewriter.domain.generativeAi.async;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pkg.littlewriter.domain.UserInputCharacterDTO;
import com.pkg.littlewriter.domain.generativeAi.openAi.OpenAiException;
import com.pkg.littlewriter.domain.generativeAi.openAi.chat.common.*;
import com.pkg.littlewriter.domain.generativeAi.*;
import com.pkg.littlewriter.domain.generativeAi.openAi.chat.contextAndQuestion.RefineContextAndQuestionDTO;
import com.pkg.littlewriter.domain.generativeAi.openAi.chat.contextEricher.EnrichContextAndQuestionGenerator;
import com.pkg.littlewriter.domain.generativeAi.openAi.chat.keyword.*;
import com.pkg.littlewriter.domain.generativeAi.openAi.image.DalleImageGenerator;
import com.pkg.littlewriter.domain.generativeAi.openAi.image.DalleSketchGenerator;
import com.pkg.littlewriter.domain.generativeAi.openAiModels.*;
import com.pkg.littlewriter.domain.generativeAi.stableDiffusion.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
public class AsyncGenerativeAiService {
    @Autowired
    private KeywordExtractor keywordExtractor;
    @Autowired
    private KeywordToImageGenerator keywordToImageGenerator;
    @Autowired
    private ContextAndQuestionGenerator contextAndQuestionGenerator;
    @Autowired
    private EnrichContextAndQuestionGenerator enrichContextAndQuestionGenerator;
    @Autowired
    private ContextKeyWordExtractor contextKeyWordExtractor;
    @Autowired
    private DalleSketchGenerator sketchGenerator;
    @Autowired
    private StableDiffusionImpl stableDiffusion;
    @Autowired
    private SketchKeywordExtractor sketchKeywordExtractor;

    @Autowired
    private DalleImageGenerator dalleImageGenerator;

    private static final ObjectMapper MAPPER = new ObjectMapper();


    @Async
    public CompletableFuture<RefineContextAndQuestionDTO> asyncEnrichContextWithQuestion(BookInProgress bookInProgress) throws OpenAiException {
        BookBuildJsonable bookBuildJsonable = new BookBuildJsonable(bookInProgress);
        RefineContextAndQuestionDTO response = enrichContextAndQuestionGenerator.getResponse(bookBuildJsonable);
        return CompletableFuture.completedFuture(response);
    }

    @Async
    public CompletableFuture<RefineContextAndQuestionDTO> asyncEnrichContextWithQuestion(BookInit bookInit) throws OpenAiException {
        BookBuildJsonable bookBuildJsonable = new BookBuildJsonable(bookInit);
        RefineContextAndQuestionDTO response = enrichContextAndQuestionGenerator.getResponse(bookBuildJsonable);
        return CompletableFuture.completedFuture(response);
    }

    @Async
    public CompletableFuture<RawResponse> asyncGenerateDalleImage(BookInProgress bookInProgress) throws OpenAiException {
        KeyWordExtractorInputJsonable keyWordExtractorInputJsonable = new KeyWordExtractorInputJsonable(bookInProgress);
        RawResponse response = contextKeyWordExtractor.getResponse(keyWordExtractorInputJsonable);
        OneAttributeJsonable imagePromptJsonable = new OneAttributeJsonable("context", response.getMessage());
        RawResponse imageResponse = dalleImageGenerator.getResponse(imagePromptJsonable);
        return CompletableFuture.completedFuture(imageResponse);
    }

    @Async
    public CompletableFuture<RawResponse> asyncGenerateDalleImage(BookInit bookInit) throws OpenAiException {
        KeyWordExtractorInputJsonable keyWordExtractorInputJsonable = new KeyWordExtractorInputJsonable(bookInit);
        RawResponse response = contextKeyWordExtractor.getResponse(keyWordExtractorInputJsonable);
        OneAttributeJsonable imagePromptJsonable = new OneAttributeJsonable("context", response.getMessage());
        RawResponse imageResponse = dalleImageGenerator.getResponse(imagePromptJsonable);
        return CompletableFuture.completedFuture(imageResponse);
    }

    @Async
    public CompletableFuture<SketchAndProcessingImage> asyncGenerateSketchAndProcessingImage(BookInProgress bookInProgress) throws OpenAiException, StableDiffusionException {
        SketchKeywordExtractorInputJsonable sketchKeywordExtractorInputJsonable = new SketchKeywordExtractorInputJsonable(bookInProgress);
        RawResponse promptResponse = sketchKeywordExtractor.getResponse(sketchKeywordExtractorInputJsonable);
        OneAttributeJsonable imagePromptJsonable = new OneAttributeJsonable("context", promptResponse.getMessage());
        RawResponse sketchImageResponse = sketchGenerator.getResponse(imagePromptJsonable);
        ImageToImageRequest imageToImageRequest = ImageToImageRequest.builder()
                .imageUrl(sketchImageResponse.getMessage())
                .prompt(promptResponse.getMessage())
                .build();
        ImageResponse processingImageResponse = stableDiffusion.generateFromImageCanny(imageToImageRequest);
        return CompletableFuture.completedFuture(new SketchAndProcessingImage(sketchImageResponse.getMessage(), processingImageResponse));
    }

    @Async
    public CompletableFuture<SketchAndProcessingImage> asyncGenerateSketchAndProcessingImage(BookInit bookInit) throws OpenAiException, StableDiffusionException {
        SketchKeywordExtractorInputJsonable sketchKeywordExtractorInputJsonable = new SketchKeywordExtractorInputJsonable(bookInit);
        RawResponse promptResponse = sketchKeywordExtractor.getResponse(sketchKeywordExtractorInputJsonable);
        OneAttributeJsonable imagePromptJsonable = new OneAttributeJsonable("context", promptResponse.getMessage());
        RawResponse sketchImageResponse = sketchGenerator.getResponse(imagePromptJsonable);
        ImageToImageRequest imageToImageRequest = ImageToImageRequest.builder()
                .imageUrl(sketchImageResponse.getMessage())
                .prompt(promptResponse.getMessage())
                .build();
        ImageResponse processingImageResponse = stableDiffusion.generateFromImageCanny(imageToImageRequest);
        return CompletableFuture.completedFuture(new SketchAndProcessingImage(sketchImageResponse.getMessage(), processingImageResponse));
    }

    @Async
    public CompletableFuture<ContextAndQuestion> asyncGenerateContextWithQuestion(BookInProgressJsonable bookInProgressJsonable) throws JsonProcessingException {
        UserInputCharacterDTO characterDTO = UserInputCharacterDTO.builder()
                .name(bookInProgressJsonable.getMainCharacter().getName())
                .personality(bookInProgressJsonable.getMainCharacter().getPersonality())
                .build();
        UserInputJsonable userInputJsonable = UserInputJsonable.builder()
                .previousContext("")
                .currentContext(bookInProgressJsonable.getCurrentContext())
                .character(characterDTO)
                .build();
        String response = contextAndQuestionGenerator.getResponse(userInputJsonable).getMessage();
        System.out.println(response);
        ContextAndQuestion contextAndQuestion = MAPPER.readValue(response, ContextAndQuestion.class);
        return CompletableFuture.completedFuture(contextAndQuestion);
    }

    @Async
    public CompletableFuture<GeneratedImage> asyncGenerateDalleImage(BookInProgressJsonable bookInProgressJsonable) throws JsonProcessingException {
        DepictInfoJsonable depictInfoJsonable = DepictInfoJsonable
                .builder()
                .backgroundInfo(bookInProgressJsonable.getBackgroundInfo())
                .currentContext(bookInProgressJsonable.getCurrentContext())
                .build();
        String keywords = keywordExtractor.getResponse(depictInfoJsonable).getMessage();
        ImageKeywordJsonable imageKeywordJsonable = new ImageKeywordJsonable(keywords);
        String imagUrl = keywordToImageGenerator.getResponse(imageKeywordJsonable).getMessage();
        return CompletableFuture.completedFuture(GeneratedImage.builder().imageUrl(imagUrl).build());
    }
}
