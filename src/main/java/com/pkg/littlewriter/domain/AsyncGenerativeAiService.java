package com.pkg.littlewriter.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.pkg.littlewriter.domain.generativeAi.BookInProgressJsonable;
import com.pkg.littlewriter.domain.generativeAi.BookInitJsonable;
import com.pkg.littlewriter.domain.generativeAi.GenerativeAi;
import com.pkg.littlewriter.domain.generativeAi.GenerativeAiResponse;
import com.pkg.littlewriter.domain.generativeAi.openAiModels.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

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
    private WordQuestionGenerator wordQuestionGenerator;

    @Async
    public CompletableFuture<GenerativeAiResponse> asyncGenerateQuestions(BookInProgressJsonable bookInProgressJsonable) throws JsonProcessingException {
        return CompletableFuture.completedFuture(questionGenerator.getResponse(bookInProgressJsonable));
    }
}
