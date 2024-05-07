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

    public BookInsightDTO generateBookInsightFrom(BookInProgress bookInProgress) throws JsonProcessingException {
        try {
            BookInProgressJsonable bookInProgressJsonable = new BookInProgressJsonable(bookInProgress);
            CharacterJsonable characterJsonable = CharacterJsonable.builder()
                    .name(bookInProgress.getCharacterDTO().getName())
                    .personality(bookInProgress.getCharacterDTO().getPersonality())
                    .build();
            bookInProgressJsonable.setMainCharacter(characterJsonable);
            CompletableFuture<ContextAndQuestion> contextAndQuestionCompletableFuture = asyncGenerativeAiService.asyncGenerateContextWithQuestion(bookInProgressJsonable);
            CompletableFuture<GeneratedImage> generatedImageCompletableFuture = asyncGenerativeAiService.asyncGenerateDalleImage(bookInProgressJsonable);
            ContextAndQuestion contextAndQuestion = contextAndQuestionCompletableFuture.get();
            GeneratedImage generatedImage = generatedImageCompletableFuture.get();
            return BookInsightDTO.builder()
                    .sketchImageUrl(generatedImage.getImageUrl())
                    .generatedQuestions(contextAndQuestion.getQuestions())
                    .refinedContext(contextAndQuestion.getRefinedText())
                    .build();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public BookInsightDTO generateBookInsightDalle(BookInProgress bookInProgress) throws OpenAiException {
        try {
            CompletableFuture<RawResponse> dalleImageResponse = asyncGenerativeAiService.asyncGenerateDalleImage(bookInProgress);
            CompletableFuture<RefineContextAndQuestionDTO> refineContextAndQuestionDTOCompletableFuture = asyncGenerativeAiService.asyncEnrichContextWithQuestion(bookInProgress);
            return dalleImageResponse.thenCombine(refineContextAndQuestionDTOCompletableFuture, (dalleResult, contextResult) ->
                    BookInsightDTO.builder()
                            .generatedQuestions(contextResult.getResponse().getQuestions())
                            .refinedContext(contextResult.getResponse().getRefinedText())
                            .sketchImageUrl(dalleResult.getMessage())
                            .build())
                    .get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new OpenAiException(e.getMessage());
        }
    }

    public BookInsightDTO generateBookInsightDalle(BookInit bookInit) throws OpenAiException {
        try {
            CompletableFuture<RawResponse> dalleImageResponse = asyncGenerativeAiService.asyncGenerateDalleImage(bookInit);
            CompletableFuture<RefineContextAndQuestionDTO> refineContextAndQuestionDTOCompletableFuture = asyncGenerativeAiService.asyncEnrichContextWithQuestion(bookInit);
            return dalleImageResponse.thenCombine(refineContextAndQuestionDTOCompletableFuture, (dalleResult, contextResult) ->
                            BookInsightDTO.builder()
                                    .generatedQuestions(contextResult.getResponse().getQuestions())
                                    .refinedContext(contextResult.getResponse().getRefinedText())
                                    .sketchImageUrl(dalleResult.getMessage())
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

//    public String generateDictionaryResponse(ContextDictionaryInputDTO contextDictionaryInputDTO) throws OpenAiException {
//        ContextDictionaryInputJsonable contextDictionaryInputJsonable = new ContextDictionaryInputJsonable(contextDictionaryInputDTO);
//        RawResponse response = dictionary.getResponse(contextDictionaryInputJsonable);
//        return response.getMessage();
//    }
//
//    //////
//    public BookInsightDTO generateBookInsightFrom2(BookInProgress bookInProgress) throws IOException, InterruptedException, ExecutionException, StableDiffusionException {
//        BookInProgressJsonable bookInProgressJsonable = new BookInProgressJsonable(bookInProgress);
//        CompletableFuture<ContextAndQuestion> contextAndQuestionCompletableFuture = asyncGenerativeAiService.asyncGenerateEnrichContextThenQuestion(bookInProgressJsonable);
//        DepictInfoJsonable depictInfoJsonable = new DepictInfoJsonable(bookInProgress);
//        GenerativeAiResponse extractedKeywords = keywordExtractorStableDiffusion.getResponse(depictInfoJsonable);
//        String prompt = extractedKeywords.getMessage();
//        System.out.println("background prompt for stable diffusion: " + prompt);
//        TextToImageRequest request = TextToImageRequest.builder()
//                .prompt(prompt)
//                .build();
//        ImageResponse imageResponse = stableDiffusion.generateFromPrompt(request);
//        ContextAndQuestion contextAndQuestion = contextAndQuestionCompletableFuture.get();
//        List<String> questions = contextAndQuestion.getQuestions();
//        return BookInsightDTO.builder()
//                .generatedQuestions(questions)
//                .temporaryGeneratedImageUrl(imageResponse.getMessage())
//                .refinedContext(contextAndQuestion.getRefinedText())
//                .build();
//    }
//
//    public BookInsightDTO generateBookInsightFrom(BookInit bookInit) throws JsonProcessingException, ExecutionException, InterruptedException {
//        BookInitJsonable bookInitJsonable = new BookInitJsonable(bookInit);
//        CompletableFuture<ContextAndQuestion> contextAndQuestionCompletableFuture = asyncGenerativeAiService.asyncGenerateContextWithQuestion(bookInitJsonable);
//        CompletableFuture<GeneratedImage> generatedImageCompletableFuture = asyncGenerativeAiService.asyncGenerateDalleImage(bookInitJsonable);
//        GeneratedImage generatedImage = generatedImageCompletableFuture.get();
//        ContextAndQuestion contextAndQuestion = contextAndQuestionCompletableFuture.get();
//        return BookInsightDTO.builder()
//                .generatedQuestions(contextAndQuestion.getQuestions())
//                .refinedContext(contextAndQuestion.getRefinedText())
//                .temporaryGeneratedImageUrl(generatedImage.getImageUrl())
//                .build();
//    }
//
//    public BookInsightDTO generateBookInsightFrom2(BookInit bookInit) throws IOException, InterruptedException, ExecutionException, StableDiffusionException {
//        BookInitJsonable bookInitJsonable = new BookInitJsonable(bookInit);
//        CompletableFuture<ContextAndQuestion> contextAndQuestionCompletableFuture = asyncGenerativeAiService.asyncGenerateEnrichContextThenQuestion(bookInitJsonable);
//        DepictInfoJsonable depictInfoJsonable = new DepictInfoJsonable(bookInit);
//        GenerativeAiResponse extractedKeywords = keywordExtractorStableDiffusion.getResponse(depictInfoJsonable);
//        String prompt = extractedKeywords.getMessage();
//        System.out.println("background prompt for stable diffusion: " + prompt);
//        TextToImageRequest request = TextToImageRequest.builder()
//                .prompt(prompt)
//                .build();
//        ImageResponse imageResponse = stableDiffusion.generateFromPrompt(request);
//        ContextAndQuestion contextAndQuestion = contextAndQuestionCompletableFuture.get();
//        List<String> questions = contextAndQuestion.getQuestions();
//        return BookInsightDTO.builder()
//                .generatedQuestions(questions)
//                .temporaryGeneratedImageUrl(imageResponse.getMessage())
//                .refinedContext(contextAndQuestion.getRefinedText())
//                .build();
//    }

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
