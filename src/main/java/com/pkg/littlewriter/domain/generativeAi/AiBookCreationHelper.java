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
        CompletableFuture<ContextAndQuestion> contextAndQuestionCompletableFuture = asyncGenerativeAiService.asyncGenerateContextWithQuestion(bookInProgressJsonable);
        CompletableFuture<GeneratedImage> generatedImageCompletableFuture = asyncGenerativeAiService.asyncGenerateImage(bookInProgressJsonable);
        ContextAndQuestion contextAndQuestion = contextAndQuestionCompletableFuture.get();
        GeneratedImage generatedImage = generatedImageCompletableFuture.get();
        return BookInsightDTO.builder()
                .temporaryGeneratedImageUrl(generatedImage.getImageUrl())
                .generatedQuestions(contextAndQuestion.getQuestions())
                .refinedContext(contextAndQuestion.getRefinedText())
                .build();
    }

    public BookInsightDTO generateBookInsightFrom2(BookInProgress bookInProgress) throws IOException, InterruptedException, ExecutionException {
        BookInProgressJsonable bookInProgressJsonable = new BookInProgressJsonable(bookInProgress);
        CompletableFuture<ContextAndQuestion> contextAndQuestionCompletableFuture = asyncGenerativeAiService.asyncGenerateEnrichContextThenQuestion(bookInProgressJsonable);
        DepictInfoJsonable depictInfoJsonable = new DepictInfoJsonable(bookInProgress);
        GenerativeAiResponse extractedKeywords = keywordExtractorStableDiffusion.getResponse(depictInfoJsonable);
        String prompt = extractedKeywords.getMessage();
        System.out.println("background prompt for stable diffusion: " + prompt);
        TextToImageRequest request = TextToImageRequest.builder()
                .prompt(prompt)
                .build();
        ImageResponse imageResponse = stableDiffusion.generateFromPrompt(request);
        ContextAndQuestion contextAndQuestion = contextAndQuestionCompletableFuture.get();
        List<String> questions =contextAndQuestion.getQuestions();
        return BookInsightDTO.builder()
                .generatedQuestions(questions)
                .temporaryGeneratedImageUrl(imageResponse.getMessage())
                .refinedContext(contextAndQuestion.getRefinedText())
                .build();
    }

    public BookInsightDTO generateBookInsightFrom(BookInit bookInit) throws JsonProcessingException, ExecutionException, InterruptedException {
        BookInitJsonable bookInitJsonable = new BookInitJsonable(bookInit);
        CompletableFuture<ContextAndQuestion> contextAndQuestionCompletableFuture = asyncGenerativeAiService.asyncGenerateContextWithQuestion(bookInitJsonable);
        CompletableFuture<GeneratedImage> generatedImageCompletableFuture = asyncGenerativeAiService.asyncGenerateImage(bookInitJsonable);
        GeneratedImage generatedImage = generatedImageCompletableFuture.get();
        ContextAndQuestion contextAndQuestion = contextAndQuestionCompletableFuture.get();
        return BookInsightDTO.builder()
                .generatedQuestions(contextAndQuestion.getQuestions())
                .refinedContext(contextAndQuestion.getRefinedText())
                .temporaryGeneratedImageUrl(generatedImage.getImageUrl())
                .build();
    }

    public BookInsightDTO generateBookInsightFrom2(BookInit bookInit) throws IOException, InterruptedException, ExecutionException {
        BookInitJsonable bookInitJsonable = new BookInitJsonable(bookInit);
        CompletableFuture<ContextAndQuestion> contextAndQuestionCompletableFuture = asyncGenerativeAiService.asyncGenerateEnrichContextThenQuestion(bookInitJsonable);
        DepictInfoJsonable depictInfoJsonable = new DepictInfoJsonable(bookInit);
        GenerativeAiResponse extractedKeywords = keywordExtractorStableDiffusion.getResponse(depictInfoJsonable);
        String prompt = extractedKeywords.getMessage();
        System.out.println("background prompt for stable diffusion: " + prompt);
        TextToImageRequest request = TextToImageRequest.builder()
                .prompt(prompt)
                .build();
        ImageResponse imageResponse = stableDiffusion.generateFromPrompt(request);
        ContextAndQuestion contextAndQuestion = contextAndQuestionCompletableFuture.get();
        List<String> questions =contextAndQuestion.getQuestions();
        return BookInsightDTO.builder()
                .generatedQuestions(questions)
                .temporaryGeneratedImageUrl(imageResponse.getMessage())
                .refinedContext(contextAndQuestion.getRefinedText())
                .build();
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
