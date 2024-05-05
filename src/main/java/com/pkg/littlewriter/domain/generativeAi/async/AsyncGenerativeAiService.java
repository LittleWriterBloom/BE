package com.pkg.littlewriter.domain.generativeAi.async;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pkg.littlewriter.domain.UserInputCharacterDTO;
import com.pkg.littlewriter.domain.generativeAi.openAi.OpenAiException;
import com.pkg.littlewriter.domain.generativeAi.openAi.chat.common.*;
import com.pkg.littlewriter.domain.generativeAi.*;
import com.pkg.littlewriter.domain.generativeAi.openAi.chat.contextAndQuestion.RefineContextAndQuestionDTO;
import com.pkg.littlewriter.domain.generativeAi.openAi.chat.contextAndQuestion.RefineContextAndQuestionGenerator;
import com.pkg.littlewriter.domain.generativeAi.openAi.chat.contextEricher.EnrichContextAndQuestionGenerator;
import com.pkg.littlewriter.domain.generativeAi.openAi.chat.keyword.ContextKeyWordExtractor;
import com.pkg.littlewriter.domain.generativeAi.openAi.chat.keyword.ContextKeyWordExtractorStableDiffusion;
import com.pkg.littlewriter.domain.generativeAi.openAi.chat.keyword.KeyWordExtractorInputJsonable;
import com.pkg.littlewriter.domain.generativeAi.openAi.image.ImageGenerator;
import com.pkg.littlewriter.domain.generativeAi.openAiModels.*;
import com.pkg.littlewriter.domain.generativeAi.stableDiffusion.ImageResponse;
import com.pkg.littlewriter.domain.generativeAi.stableDiffusion.StableDiffusionException;
import com.pkg.littlewriter.domain.generativeAi.stableDiffusion.StableDiffusionImpl;
import com.pkg.littlewriter.domain.generativeAi.stableDiffusion.TextToImageRequest;
import com.pkg.littlewriter.domain.generativeAi.stableDiffusion.api.StableDiffusionApi;
import com.pkg.littlewriter.dto.BookInsightDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
public class AsyncGenerativeAiService {
    @Autowired
    private ContextQuestionGenerator questionGenerator;
    @Autowired
    private ContextRefiner contextRefiner;
    @Autowired
    private KeywordExtractor keywordExtractor;
    @Autowired
    private KeywordToImageGenerator keywordToImageGenerator;
    @Autowired
    private ContextEnricher contextEnricher;
    @Autowired
    private ContextAndQuestionGenerator contextAndQuestionGenerator;

    //////
    @Autowired
    private RefineContextAndQuestionGenerator refineContextAndQuestionGenerator;
    @Autowired
    private EnrichContextAndQuestionGenerator enrichContextAndQuestionGenerator;
    @Autowired
    private ContextKeyWordExtractor contextKeyWordExtractor;
    @Autowired
    private ImageGenerator imageGenerator;
    @Autowired
    private ContextKeyWordExtractorStableDiffusion contextKeyWordExtractorStableDiffusion;
    @Autowired
    private StableDiffusionImpl stableDiffusion;

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Async
    public CompletableFuture<RefineContextAndQuestionDTO> asyncRefineContextWithQuestion(BookInProgress bookInProgress) throws OpenAiException {
        BookBuildJsonable bookBuildJsonable = new BookBuildJsonable(bookInProgress);
        RefineContextAndQuestionDTO response = refineContextAndQuestionGenerator.getResponse(bookBuildJsonable);
        return CompletableFuture.completedFuture(response);
    }

    @Async
    public CompletableFuture<RefineContextAndQuestionDTO> asyncRefineContextWithQuestion(BookInit bookInit) throws OpenAiException {
        BookBuildJsonable bookBuildJsonable = new BookBuildJsonable(bookInit);
        RefineContextAndQuestionDTO response = refineContextAndQuestionGenerator.getResponse(bookBuildJsonable);
        return CompletableFuture.completedFuture(response);
    }

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
        RawResponse imageResponse = imageGenerator.getResponse(imagePromptJsonable);
        return CompletableFuture.completedFuture(imageResponse);
    }

    @Async
    public CompletableFuture<RawResponse> asyncGenerateDalleImage(BookInit bookInit) throws OpenAiException {
        KeyWordExtractorInputJsonable keyWordExtractorInputJsonable = new KeyWordExtractorInputJsonable(bookInit);
        RawResponse response = contextKeyWordExtractor.getResponse(keyWordExtractorInputJsonable);
        OneAttributeJsonable imagePromptJsonable = new OneAttributeJsonable("context", response.getMessage());
        RawResponse imageResponse = imageGenerator.getResponse(imagePromptJsonable);
        return CompletableFuture.completedFuture(imageResponse);
    }

    @Async
    public CompletableFuture<ImageResponse> asyncGenerateStableDiffusionImage(BookInProgress bookInProgress) throws OpenAiException, StableDiffusionException {
        KeyWordExtractorInputJsonable keyWordExtractorInputJsonable = new KeyWordExtractorInputJsonable(bookInProgress);
        RawResponse response = contextKeyWordExtractorStableDiffusion.getResponse(keyWordExtractorInputJsonable);
        TextToImageRequest textToImageRequest = TextToImageRequest.builder()
                .prompt(response.getMessage())
                .build();
        ImageResponse stableDiffusionResponse = stableDiffusion.generateFromPrompt(textToImageRequest);
        return CompletableFuture.completedFuture(stableDiffusionResponse);
    }

    @Async
    public CompletableFuture<ImageResponse> asyncGenerateStableDiffusionImage(BookInit bookInit) throws OpenAiException, StableDiffusionException {
        KeyWordExtractorInputJsonable keyWordExtractorInputJsonable = new KeyWordExtractorInputJsonable(bookInit);
        RawResponse response = contextKeyWordExtractorStableDiffusion.getResponse(keyWordExtractorInputJsonable);
        TextToImageRequest textToImageRequest = TextToImageRequest.builder()
                .prompt(response.getMessage())
                .build();
        ImageResponse stableDiffusionResponse = stableDiffusion.generateFromPrompt(textToImageRequest);
        return CompletableFuture.completedFuture(stableDiffusionResponse);
    }


    /////////////////////////////////////////////
    @Async
    public CompletableFuture<ContextAndQuestion> asyncGenerateRefinedContextThenQuestion(BookInProgressJsonable bookInProgressJsonable) throws JsonProcessingException {
        String refinedContext = contextRefiner.getResponse(bookInProgressJsonable).getMessage();
        String rawResponse = questionGenerator.getResponse(bookInProgressJsonable).getMessage();
        List<String> questions = Arrays.stream(rawResponse.split("\n")).toList();
        return CompletableFuture.completedFuture(ContextAndQuestion.builder()
                .refinedText(refinedContext)
                .questions(questions)
                .build());
    }

    @Async
    public CompletableFuture<ContextAndQuestion> asyncGenerateRefinedContextThenQuestion(BookInitJsonable bookInitJsonable) throws JsonProcessingException {
        String refinedContext = contextRefiner.getResponse(bookInitJsonable).getMessage();
        String rawResponse = questionGenerator.getResponse(bookInitJsonable).getMessage();
        List<String> questions = Arrays.stream(rawResponse.split("\n")).toList();
        return CompletableFuture.completedFuture(ContextAndQuestion.builder()
                .refinedText(refinedContext)
                .questions(questions)
                .build());
    }

    @Async
    public CompletableFuture<ContextAndQuestion> asyncGenerateContextWithQuestion(BookInitJsonable bookInitJsonable) throws JsonProcessingException {
        UserInputCharacterDTO characterDTO = UserInputCharacterDTO.builder()
                .name(bookInitJsonable.getCharacterJsonable().getName())
                .personality(bookInitJsonable.getCharacterJsonable().getPersonality())
                .build();
        UserInputJsonable userInputJsonable = UserInputJsonable.builder()
                .previousContext("")
                .currentContext(bookInitJsonable.getCurrentContext())
                .character(characterDTO)
                .build();
        String response = contextAndQuestionGenerator.getResponse(userInputJsonable).getMessage();
        System.out.println(response);
        ContextAndQuestion contextAndQuestion = MAPPER.readValue(response, ContextAndQuestion.class);
        return CompletableFuture.completedFuture(contextAndQuestion);
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
    public CompletableFuture<ContextAndQuestion> asyncGenerateEnrichContextThenQuestion(BookInitJsonable bookInitJsonable) throws JsonProcessingException {
        String enrichedContext = contextEnricher.getResponse(bookInitJsonable).getMessage();
        String rawResponse = questionGenerator.getResponse(bookInitJsonable).getMessage();
        List<String> questions = Arrays.stream(rawResponse.split("\n")).toList();
        return CompletableFuture.completedFuture(ContextAndQuestion.builder()
                .refinedText(enrichedContext)
                .questions(questions)
                .build());
    }

    @Async
    public CompletableFuture<ContextAndQuestion> asyncGenerateEnrichContextThenQuestion(BookInProgressJsonable bookInProgressJsonable) throws JsonProcessingException {
        String enrichedContext = contextEnricher.getResponse(bookInProgressJsonable).getMessage();
        String rawResponse = questionGenerator.getResponse(bookInProgressJsonable).getMessage();
        List<String> questions = Arrays.stream(rawResponse.split("\n")).toList();
        return CompletableFuture.completedFuture(ContextAndQuestion.builder()
                .refinedText(enrichedContext)
                .questions(questions)
                .build());
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

    @Async
    public CompletableFuture<GeneratedImage> asyncGenerateDalleImage(BookInitJsonable bookInitJsonable) throws JsonProcessingException {
        DepictInfoJsonable depictInfoJsonable = DepictInfoJsonable
                .builder()
                .backgroundInfo(bookInitJsonable.getBackgroundInfo())
                .currentContext(bookInitJsonable.getCurrentContext())
                .build();
        String keywords = keywordExtractor.getResponse(depictInfoJsonable).getMessage();
        ImageKeywordJsonable imageKeywordJsonable = new ImageKeywordJsonable(keywords);
        String imagUrl = keywordToImageGenerator.getResponse(imageKeywordJsonable).getMessage();
        return CompletableFuture.completedFuture(GeneratedImage.builder().imageUrl(imagUrl).build());
    }
}
