package com.pkg.littlewriter.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class BookInsightDTO {
    private List<String> generatedQuestions;
    private String temporaryGeneratedImageUrl;
    private String refinedContext;
}
