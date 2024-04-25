package com.pkg.littlewriter.domain.generativeAi;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.pkg.littlewriter.domain.generativeAi.openAiModels.ContextQuestionGenerator;
import com.pkg.littlewriter.domain.generativeAi.stableDiffusion.ImageResponse;
import com.pkg.littlewriter.domain.generativeAi.stableDiffusion.StableDiffusion;
import com.pkg.littlewriter.domain.generativeAi.stableDiffusion.TextToImageRequest;
import com.pkg.littlewriter.domain.generativeAi.async.AsyncGenerativeAiService;
import com.pkg.littlewriter.domain.generativeAi.async.ContextAndQuestion;
import com.pkg.littlewriter.domain.generativeAi.async.GeneratedImage;
import com.pkg.littlewriter.dto.BookInsightDTO;
import com.pkg.littlewriter.dto.WordQuestionDTO;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
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

    public BookInsightDTO generateBookInsightFrom(BookInProgress bookInProgress) throws JsonProcessingException, ExecutionException, InterruptedException {
        BookInProgressJsonable bookInProgressJsonable = new BookInProgressJsonable(bookInProgress);
        CharacterJsonable characterJsonable = CharacterJsonable.builder()
                .name(bookInProgress.getCharacterDTO().getName())
                .personality(bookInProgress.getCharacterDTO().getPersonality())
                .build();
        bookInProgressJsonable.setMainCharacter(characterJsonable);
        CompletableFuture<ContextAndQuestion> contextAndQuestionCompletableFuture = asyncGenerativeAiService.asyncGenerateContextAndQuestion(bookInProgressJsonable);
        CompletableFuture<GeneratedImage> generatedImageCompletableFuture = asyncGenerativeAiService.asyncGenerateImage(bookInProgressJsonable);
        ContextAndQuestion contextAndQuestion = contextAndQuestionCompletableFuture.get();
        GeneratedImage generatedImage = generatedImageCompletableFuture.get();
        return BookInsightDTO.builder()
                .temporaryGeneratedImageUrl(generatedImage.getImageUrl())
                .generatedQuestions(contextAndQuestion.getGeneratedQuestions())
                .refinedContext(contextAndQuestion.getGeneratedContext())
                .build();
    }

    public BookInsightDTO generateBookInsightFrom2(BookInProgress bookInProgress) {
        BookInProgressJsonable bookInProgressJsonable = new BookInProgressJsonable(bookInProgress);
        try {
            List<String> questions = contextQuestionGenerator.get3Responses(bookInProgressJsonable)
                    .stream()
                    .map(GenerativeAiResponse::getMessage)
                    .toList();
            GenerativeAiResponse refinedContext = getRefinedContext(bookInProgress);
            DepictInfoJsonable depictInfoJsonable = new DepictInfoJsonable(bookInProgress);
            String prompt = keywordExtractorStableDiffusion.getResponse(depictInfoJsonable).getMessage();
            System.out.println("creating img using ... " + prompt);
            TextToImageRequest textToImageRequest = TextToImageRequest.builder()
                    .prompt(prompt)
                    .build();
            ImageResponse response = stableDiffusion.generateFromPrompt(textToImageRequest);
            return BookInsightDTO.builder()
                    .generatedQuestions(questions)
                    .temporaryGeneratedImageUrl(response.getImageUrl())
                    .refinedContext(refinedContext.getMessage())
                    .build();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private GenerativeAiResponse getRefinedContext(BookInProgress bookInProgress) throws JsonProcessingException {
        return contextRefiner.getResponse(new Jsonable() {
            @Override
            public String toJsonString() throws JsonProcessingException {
                return bookInProgress.getCurrentContext();
            }
        });
    }

    public BookInsightDTO generateBookInsightFrom(BookInit bookInit) throws JsonProcessingException, ExecutionException, InterruptedException {
        BookInitJsonable bookInitJsonable = new BookInitJsonable(bookInit);
        CompletableFuture<ContextAndQuestion> contextAndQuestionCompletableFuture = asyncGenerativeAiService.asyncGenerateContextAndQuestion(bookInitJsonable);
        CompletableFuture<GeneratedImage> generatedImageCompletableFuture = asyncGenerativeAiService.asyncGenerateImage(bookInitJsonable);
        ContextAndQuestion contextAndQuestion = contextAndQuestionCompletableFuture.get();
        GeneratedImage generatedImage = generatedImageCompletableFuture.get();
        return BookInsightDTO.builder()
                .generatedQuestions(contextAndQuestion.getGeneratedQuestions())
                .refinedContext(contextAndQuestion.getGeneratedContext())
                .temporaryGeneratedImageUrl(generatedImage.getImageUrl())
                .build();
    }

    public BookInsightDTO generateBookInsightFrom2(BookInit bookInit) {
        BookInitJsonable bookInitJsonable = new BookInitJsonable(bookInit);
        try {
            List<String> questions = Arrays.stream(contextQuestionGenerator.getResponse(bookInitJsonable)
                            .getMessage()
                            .split("\n"))
                    .toList();
            DepictInfoJsonable depictInfoJsonable = new DepictInfoJsonable(bookInit);
            GenerativeAiResponse extractedKeywords = keywordExtractorStableDiffusion.getResponse(depictInfoJsonable);
            ImageKeywordJsonable keywordJsonable = new ImageKeywordJsonable(extractedKeywords.getMessage());
            String prompt = bookInit.getCharacterDTO().getDescription() + bookInit.getCharacterDTO().getAppearanceKeywords() + "with";
//            GenerativeAiResponse imageUrlResponse = keywordToImageGenerator.getResponse(keywordJsonable);
            System.out.println("background prompt for stable diffusion: " + extractedKeywords.getMessage());
            TextToImageRequest request = TextToImageRequest.builder()
                    .prompt(extractedKeywords.getMessage())
                    .build();
            ImageResponse imageResponse = stableDiffusion.generateFromPrompt(request);
            GenerativeAiResponse refinedContext = contextRefiner.getResponse(new Jsonable() {
                @Override
                public String toJsonString() throws JsonProcessingException {
                    return bookInit.getCurrentContext();
                }
            });
            return BookInsightDTO.builder()
                    .generatedQuestions(questions)
                    .temporaryGeneratedImageUrl(imageResponse.getMessage())
                    .refinedContext(refinedContext.getMessage())
                    .build();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public String generateImageUrlFrom(String keyword) {
        try {
            ImageKeywordJsonable keywordJsonable = new ImageKeywordJsonable(keyword);
            GenerativeAiResponse extractedKeywords = keywordExtractor.getResponse(keywordJsonable);
            ImageKeywordJsonable extractedKeywordJsonable = new ImageKeywordJsonable(extractedKeywords.getMessage());
            return keywordToImageGenerator.getResponse(extractedKeywordJsonable).getMessage();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
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
