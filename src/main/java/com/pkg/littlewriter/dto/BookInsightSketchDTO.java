package com.pkg.littlewriter.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class BookInsightSketchDTO {
    private String refinedContext;
    private String sketchImageUrl;
    private List<String> generatedQuestions;

    public BookInsightSketchDTO(BookInsightDTO bookInsightDTO) {
        this.refinedContext = bookInsightDTO.getRefinedContext();
        this.sketchImageUrl = bookInsightDTO.getSketchImageUrl();
        this.generatedQuestions = bookInsightDTO.getGeneratedQuestions();
    }
}
