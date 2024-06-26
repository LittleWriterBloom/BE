package com.pkg.littlewriter.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class BookInitRequestDTO {
    private String firstContext;
    private String backgroundInfo;
    private Long characterId;
    private int storyLength;
}
