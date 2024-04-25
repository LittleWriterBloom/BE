package com.pkg.littlewriter.domain.generativeAi.async;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.pkg.littlewriter.domain.generativeAi.*;
import com.pkg.littlewriter.domain.generativeAi.openAiModels.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
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

    @Async
    public CompletableFuture<ContextAndQuestion> asyncGenerateContextAndQuestion(BookInProgressJsonable bookInProgressJsonable) throws JsonProcessingException {
        String refinedContext = contextRefiner.getResponse(bookInProgressJsonable).getMessage();
        String rawResponse = questionGenerator.getResponse(bookInProgressJsonable).getMessage();
        List<String> questions = Arrays.stream(rawResponse.split("\n")).toList();
        return CompletableFuture.completedFuture(ContextAndQuestion.builder()
                .generatedContext(refinedContext)
                .generatedQuestions(questions)
                .build());
    }

    @Async
    public CompletableFuture<ContextAndQuestion> asyncGenerateContextAndQuestion(BookInitJsonable bookInitJsonable) throws JsonProcessingException {
        String refinedContext = contextRefiner.getResponse(bookInitJsonable).getMessage();
        String rawResponse = questionGenerator.getResponse(bookInitJsonable).getMessage();
        List<String> questions = Arrays.stream(rawResponse.split("\n")).toList();
        return CompletableFuture.completedFuture(ContextAndQuestion.builder()
                .generatedContext(refinedContext)
                .generatedQuestions(questions)
                .build());
    }

    @Async
    public CompletableFuture<GeneratedImage> asyncGenerateImage(BookInProgressJsonable bookInProgressJsonable) throws JsonProcessingException {
        String context = bookInProgressJsonable.getCurrentContext();
        String keywords = keywordExtractor.getResponse(new Jsonable() {
            @Override
            public String toJsonString() {
                return context;
            }
        }).getMessage();
        ImageKeywordJsonable imageKeywordJsonable = new ImageKeywordJsonable(keywords);
        String imagUrl = keywordToImageGenerator.getResponse(imageKeywordJsonable).getMessage();
        return CompletableFuture.completedFuture(GeneratedImage.builder().imageUrl(imagUrl).build());
    }

    @Async
    public CompletableFuture<GeneratedImage> asyncGenerateImage(BookInitJsonable bookInitJsonable) throws JsonProcessingException {
        String context = bookInitJsonable.getCurrentContext();
        String keywords = keywordExtractor.getResponse(new Jsonable() {
            @Override
            public String toJsonString() {
                return context;
            }
        }).getMessage();
        ImageKeywordJsonable imageKeywordJsonable = new ImageKeywordJsonable(keywords);
        String imagUrl = keywordToImageGenerator.getResponse(imageKeywordJsonable).getMessage();
        return CompletableFuture.completedFuture(GeneratedImage.builder().imageUrl(imagUrl).build());
    }
}
