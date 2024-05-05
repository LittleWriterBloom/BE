package com.pkg.littlewriter.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.pkg.littlewriter.domain.generativeAi.AiBookCreationHelper;
import com.pkg.littlewriter.domain.generativeAi.BookInProgress;
import com.pkg.littlewriter.domain.generativeAi.BookInit;
import com.pkg.littlewriter.domain.generativeAi.stableDiffusion.StableDiffusionException;
import com.pkg.littlewriter.domain.model.BookEntity;
import com.pkg.littlewriter.dto.BookInsightDTO;
import com.pkg.littlewriter.dto.WordQuestionDTO;
import com.pkg.littlewriter.persistence.BookPageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class BookService {
    @Autowired
    private AiBookCreationHelper aiBookCreationHelper;
    @Autowired
    private BookPageRepository bookRepository;

    public BookInsightDTO generateHelperContents(BookInProgress bookInProgress) throws ExecutionException, JsonProcessingException, InterruptedException {
        return  aiBookCreationHelper.generateBookInsightFrom(bookInProgress);
    }

    public BookInsightDTO generateHelperContents(BookInit bookInit) throws ExecutionException, JsonProcessingException, InterruptedException {
        return  aiBookCreationHelper.generateBookInsightFrom(bookInit);
    }

    public BookInsightDTO generateHelperContents2(BookInit bookInit) throws IOException, ExecutionException, InterruptedException, StableDiffusionException {
        return  aiBookCreationHelper.generateBookInsightFrom2(bookInit);
    }

    public BookInsightDTO generateHelperContents2(BookInProgress bookInProgress) throws IOException, ExecutionException, InterruptedException, StableDiffusionException {
        return  aiBookCreationHelper.generateBookInsightFrom2(bookInProgress);
    }

    public String generateWordQuestionAnswer(WordQuestionDTO wordQuestionDTO) {
        return aiBookCreationHelper.generateWordQuestionAnswer(wordQuestionDTO);
    }

    public BookEntity createEmptyBook(BookEntity bookEntity) {
        bookRepository.save(bookEntity);
        return bookRepository.findById(bookEntity.getId()).orElseThrow();
    }

    public List<BookEntity> getAllByUserId(Long userId) {
        return bookRepository.findAllByUserId(userId);
    }

    public BookEntity getById(String bookId) {
        return bookRepository.findById(bookId).orElseThrow();
    }
}
