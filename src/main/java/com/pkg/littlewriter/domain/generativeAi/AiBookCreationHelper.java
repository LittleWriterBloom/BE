package com.pkg.littlewriter.domain.generativeAi;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.pkg.littlewriter.domain.generativeAi.async.SketchAndProcessingImage;
import com.pkg.littlewriter.domain.generativeAi.openAi.OpenAiException;
import com.pkg.littlewriter.domain.generativeAi.openAi.chat.common.RawResponse;
import com.pkg.littlewriter.domain.generativeAi.openAi.chat.contextAndQuestion.RefineContextAndQuestionDTO;
import com.pkg.littlewriter.domain.generativeAi.openAi.chat.dictionary.ContextDictionary;
import com.pkg.littlewriter.domain.generativeAi.openAiModels.ContextQuestionGenerator;
import com.pkg.littlewriter.domain.generativeAi.stableDiffusion.StableDiffusion;
import com.pkg.littlewriter.domain.generativeAi.stableDiffusion.StableDiffusionException;
import com.pkg.littlewriter.domain.generativeAi.async.AsyncGenerativeAiService;
import com.pkg.littlewriter.domain.generativeAi.async.ContextAndQuestion;
import com.pkg.littlewriter.domain.generativeAi.async.GeneratedImage;
import com.pkg.littlewriter.dto.BookInsightDTO;
import com.pkg.littlewriter.dto.BookInsightDalleDTO;
import com.pkg.littlewriter.dto.WordQuestionDTO;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Component
@AllArgsConstructor
public class AiBookCreationHelper {
    @Autowired
    ContextQuestionGenerator contextQuestionGenerator;
    @Autowired
    GenerativeAi keywordExtractor;
    @Autowired
    GenerativeAi keywordExtractorStableDiffusion;
    @Autowired
    GenerativeAi keywordToImageGenerator;
    @Autowired
    GenerativeAi wordQuestionGenerator;
    @Autowired
    GenerativeAi contextRefiner;
    @Autowired
    GenerativeAi contextEnricher;
    @Autowired
    StableDiffusion stableDiffusion;
    @Autowired
    AsyncGenerativeAiService asyncGenerativeAiService;

    @Autowired
    private ContextDictionary dictionary;

    public BookInsightDalleDTO generateBookInsightDalleFrom(BookInProgress bookInProgress) throws OpenAiException {
        CompletableFuture<RefineContextAndQuestionDTO> enrichContextAndQuestionDTOCompletableFuture = asyncGenerativeAiService.asyncEnrichContextWithQuestion(bookInProgress);
        CompletableFuture<RawResponse> imageResponseCompletableFuture = asyncGenerativeAiService.asyncGenerateDalleImage(bookInProgress);
        try {
            return enrichContextAndQuestionDTOCompletableFuture.thenCombine(imageResponseCompletableFuture, (contextResult, imageResult) ->
                    BookInsightDalleDTO.builder()
                            .generatedQuestions(contextResult.getResponse().getQuestions())
                            .refinedContext(contextResult.getResponse().getRefinedText())
                            .imageUrl(imageResult.getMessage())
                            .build())
                    .get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new OpenAiException(e.getMessage());
        }
    }

    public BookInsightDalleDTO generateBookInsightDalleFrom(BookInit bookInit) throws OpenAiException {
        CompletableFuture<RefineContextAndQuestionDTO> enrichContextAndQuestionDTOCompletableFuture = asyncGenerativeAiService.asyncEnrichContextWithQuestion(bookInit);
        CompletableFuture<RawResponse> imageResponseCompletableFuture = asyncGenerativeAiService.asyncGenerateDalleImage(bookInit);
        try {
            return enrichContextAndQuestionDTOCompletableFuture.thenCombine(imageResponseCompletableFuture, (contextResult, imageResult) ->
                            BookInsightDalleDTO.builder()
                                    .generatedQuestions(contextResult.getResponse().getQuestions())
                                    .refinedContext(contextResult.getResponse().getRefinedText())
                                    .imageUrl(imageResult.getMessage())
                                    .build())
                    .get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new OpenAiException(e.getMessage());
        }
    }

    public BookInsightDTO generateBookInsightStableDiffusion(BookInProgress bookInProgress) throws OpenAiException, StableDiffusionException {
        try {
            CompletableFuture<RefineContextAndQuestionDTO> enrichContextAndQuestionDTOCompletableFuture = asyncGenerativeAiService.asyncEnrichContextWithQuestion(bookInProgress);
//            CompletableFuture<ImageResponse> imageResponseCompletableFuture = asyncGenerativeAiService.asyncGenerateStableDiffusionImage(bookInProgress);
            CompletableFuture<SketchAndProcessingImage> sketchAndProcessingImageCompletableFuture = asyncGenerativeAiService.asyncGenerateSketchAndProcessingImage(bookInProgress);
            return enrichContextAndQuestionDTOCompletableFuture.thenCombine(sketchAndProcessingImageCompletableFuture, (contextResult, imageResult) ->
                    BookInsightDTO.builder()
                            .generatedQuestions(contextResult.getResponse().getQuestions())
                            .refinedContext(contextResult.getResponse().getRefinedText())
                            .sketchImageUrl(imageResult.getSketchImageUrl())
                            .processingImage(imageResult.getProcessingImage())
                            .build())
                    .get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            if (e.getCause().equals(StableDiffusionException.class)) {
                throw new StableDiffusionException(e.getMessage());
            }
            throw new OpenAiException(e.getMessage());
        }
    }

    public BookInsightDTO generateBookInsightStableDiffusion(BookInit bookInit) throws OpenAiException, StableDiffusionException {
        try {
            CompletableFuture<RefineContextAndQuestionDTO> enrichContextAndQuestionDTOCompletableFuture = asyncGenerativeAiService.asyncEnrichContextWithQuestion(bookInit);
//            CompletableFuture<ImageResponse> imageResponseCompletableFuture = asyncGenerativeAiService.asyncGenerateStableDiffusionImage(bookInit);
            CompletableFuture<SketchAndProcessingImage> sketchAndProcessingImageCompletableFuture = asyncGenerativeAiService.asyncGenerateSketchAndProcessingImage(bookInit);
            return enrichContextAndQuestionDTOCompletableFuture.thenCombine(sketchAndProcessingImageCompletableFuture, (contextResult, imageResult) ->
                            BookInsightDTO.builder()
                                    .generatedQuestions(contextResult.getResponse().getQuestions())
                                    .refinedContext(contextResult.getResponse().getRefinedText())
                                    .sketchImageUrl(imageResult.getSketchImageUrl())
                                    .processingImage(imageResult.getProcessingImage())
                                    .build())
                    .get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            if (e.getCause().equals(StableDiffusionException.class)) {
                throw new StableDiffusionException(e.getMessage());
            }
            throw new OpenAiException(e.getMessage());
        }
    }

    public String generateWordQuestionAnswer(WordQuestionDTO wordQuestionDTO) {
        try {
            WordQuestionJsonable wordQuestionJsonable = new WordQuestionJsonable(wordQuestionDTO);
            GenerativeAiResponse answer = wordQuestionGenerator.getResponse(wordQuestionJsonable);
            return answer.getMessage();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
