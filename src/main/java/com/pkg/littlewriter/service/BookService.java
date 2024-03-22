package com.pkg.littlewriter.service;

import com.pkg.littlewriter.domain.AiBookCreationHelper;
import com.pkg.littlewriter.domain.generativeAi.BookInProgress;
import com.pkg.littlewriter.dto.QuestionAndImageDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BookService {
    @Autowired
    private AiBookCreationHelper aiBookCreationHelper;

    public QuestionAndImageDTO generateHelperContents(BookInProgress bookInProgress) {
        return  aiBookCreationHelper.generateQuestionAndImageFrom(bookInProgress);
    }
}
