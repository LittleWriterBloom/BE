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
public class PageProgressRequestDTO {
    private String backgroundInfo;
    private Long characterId;
    private List<PageDTO> previousPages;
    private String userContext;
    private String characterActionInfo;
}
