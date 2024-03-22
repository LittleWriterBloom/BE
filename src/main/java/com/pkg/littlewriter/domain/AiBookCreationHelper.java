package com.pkg.littlewriter.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.pkg.littlewriter.domain.generativeAi.*;
import com.pkg.littlewriter.dto.QuestionAndImageDTO;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class AiBookCreationHelper {
    @Autowired
    GenerativeAi contextQuestionGenerator;
    @Autowired
    GenerativeAi keywordExtractor;
    @Autowired
    GenerativeAi keywordToImageGenerator;

    public QuestionAndImageDTO generateQuestionAndImageFrom(BookInProgress bookInProgress) {
        BookInProgressJsonable bookInProgressJsonable = new BookInProgressJsonable(bookInProgress);
        try {
            GenerativeAiResponse contextQuestionResponse = contextQuestionGenerator.getResponse(bookInProgressJsonable);
            GenerativeAiResponse extractedKeywords = keywordExtractor.getResponse(bookInProgressJsonable);
            KeywordJsonable keywordJsonable = new KeywordJsonable(extractedKeywords.getMessage());
            GenerativeAiResponse imageUrlResponse = keywordToImageGenerator.getResponse(keywordJsonable);
            return QuestionAndImageDTO.builder()
                    .generatedQuestions(contextQuestionResponse.getMessage())
                    .temporaryGeneratedImageUrl(imageUrlResponse.getMessage())
                    .build();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
