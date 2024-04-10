package com.pkg.littlewriter.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class PageProgressResponseDTO {
    private String generatedQuestions;
    private String generatedBackgroundImageUrl;
    private String refinedSentence;
    private int currentPageNumber;
}
