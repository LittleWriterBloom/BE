package com.pkg.littlewriter.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.joda.time.DateTime;

import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class BookCreationRequestDTO {
    private String title;
    private Long characterId;
    private List<PageDTO> pages;
}
