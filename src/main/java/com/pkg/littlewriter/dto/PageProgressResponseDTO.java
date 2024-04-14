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
public class PageProgressResponseDTO {
    private List<String> generatedQuestions;
    private String generatedBackgroundImageUrl;
    private String refinedSentence;
    private int currentPageNumber;
}
