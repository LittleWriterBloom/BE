package com.pkg.littlewriter.service;

import com.pkg.littlewriter.domain.AiBookCreationHelper;
import com.pkg.littlewriter.domain.generativeAi.BookInProgress;
import com.pkg.littlewriter.domain.generativeAi.BookInit;
import com.pkg.littlewriter.domain.model.BookEntity;
import com.pkg.littlewriter.dto.BookInsightDTO;
import com.pkg.littlewriter.dto.WordQuestionDTO;
import com.pkg.littlewriter.persistence.BookPageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookService {
    @Autowired
    private AiBookCreationHelper aiBookCreationHelper;
    @Autowired
    private BookPageRepository bookRepository;

    public BookInsightDTO generateHelperContents(BookInProgress bookInProgress) {
        return  aiBookCreationHelper.generateBookInsightFrom(bookInProgress);
    }

    public BookInsightDTO generateHelperContents(BookInit bookInit) {
        return  aiBookCreationHelper.generateBookInsightFrom(bookInit);
    }

    public String generateImageUrl(String keyword) {
        return aiBookCreationHelper.generateImageUrlFrom(keyword);
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
