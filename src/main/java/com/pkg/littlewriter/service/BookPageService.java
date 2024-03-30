package com.pkg.littlewriter.service;

import com.pkg.littlewriter.domain.model.PageEntity;
import com.pkg.littlewriter.persistence.PageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookPageService {
    @Autowired
    private PageRepository repository;

    public List<PageEntity> createPage(PageEntity page) {
        String bookId = page.getBookId();
        repository.save(page);
        return repository.getAllByBookId(bookId);
    }

    public List<PageEntity> getAllById(String bookId) {
        return repository.getAllByBookId(bookId);
    }
}
