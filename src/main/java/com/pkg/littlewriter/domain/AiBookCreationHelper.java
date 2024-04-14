package com.pkg.littlewriter.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.pkg.littlewriter.domain.generativeAi.*;
import com.pkg.littlewriter.domain.generativeAi.openAiModels.ContextQuestionGenerator;
import com.pkg.littlewriter.dto.BookInsightDTO;
import com.pkg.littlewriter.dto.WordQuestionDTO;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class AiBookCreationHelper {
    @Autowired
    ContextQuestionGenerator contextQuestionGenerator;
    @Autowired
    GenerativeAi keywordExtractor;
    @Autowired
    GenerativeAi keywordToImageGenerator;
    @Autowired
    GenerativeAi wordQuestionGenerator;
    @Autowired
    GenerativeAi contextRefiner;

    public BookInsightDTO generateBookInsightFrom(BookInProgress bookInProgress) {
        BookInProgressJsonable bookInProgressJsonable = new BookInProgressJsonable(bookInProgress);
        try {
            List<String> questions = contextQuestionGenerator.get3Responses(bookInProgressJsonable)
                    .stream()
                    .map(GenerativeAiResponse::getMessage)
                    .toList();
            GenerativeAiResponse refinedContext = getRefinedContext(bookInProgress);
            GenerativeAiResponse imageUrlResponse = getImageUrlResponse(bookInProgressJsonable);
            return BookInsightDTO.builder()
                    .generatedQuestions(questions)
                    .temporaryGeneratedImageUrl(imageUrlResponse.getMessage())
                    .refinedContext(refinedContext.getMessage())
                    .build();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private GenerativeAiResponse getImageUrlResponse(BookInProgressJsonable bookInProgressJsonable) throws JsonProcessingException {
        GenerativeAiResponse extractedKeywords = keywordExtractor.getResponse(bookInProgressJsonable);
        ImageKeywordJsonable keywordJsonable = new ImageKeywordJsonable(extractedKeywords.getMessage());
        return keywordToImageGenerator.getResponse(keywordJsonable);
    }

    private GenerativeAiResponse getRefinedContext(BookInProgress bookInProgress) throws JsonProcessingException {
        return contextRefiner.getResponse(new Jsonable() {
            @Override
            public String toJsonString() throws JsonProcessingException {
                return bookInProgress.getCurrentContext();
            }
        });
    }

    private GenerativeAiResponse getQuestions(BookInProgressJsonable bookInProgressJsonable) throws JsonProcessingException {
        return contextQuestionGenerator.getResponse(bookInProgressJsonable);
    }

    public BookInsightDTO generateBookInsightFrom(BookInit bookInit) {
        BookInitJsonable bookInProgressJsonable = new BookInitJsonable(bookInit);
        try {
            List<String> questions = contextQuestionGenerator.get3Responses(bookInProgressJsonable)
                    .stream()
                    .map(GenerativeAiResponse::getMessage)
                    .toList();
            GenerativeAiResponse extractedKeywords = keywordExtractor.getResponse(bookInProgressJsonable);
            ImageKeywordJsonable keywordJsonable = new ImageKeywordJsonable(extractedKeywords.getMessage());
            GenerativeAiResponse imageUrlResponse = keywordToImageGenerator.getResponse(keywordJsonable);
            GenerativeAiResponse refinedContext = contextRefiner.getResponse(new Jsonable() {
                @Override
                public String toJsonString() throws JsonProcessingException {
                    return bookInit.getCurrentContext();
                }
            });
            return BookInsightDTO.builder()
                    .generatedQuestions(questions)
                    .temporaryGeneratedImageUrl(imageUrlResponse.getMessage())
                    .refinedContext(refinedContext.getMessage())
                    .build();
        } catch (JsonProcessingException e) {
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
