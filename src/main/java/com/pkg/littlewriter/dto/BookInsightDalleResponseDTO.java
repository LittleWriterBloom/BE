package com.pkg.littlewriter.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class BookInsightDalleResponseDTO {
    private BookInsightDalleDTO bookInsight;
    private int storyLength;
}
