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
public class BookInsightResponseDTO {
    private BookInsightDTO bookInsight;
    private List<PageDTO> createdPages;
    private int storyLength;
}
