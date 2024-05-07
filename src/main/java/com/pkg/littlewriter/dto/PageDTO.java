package com.pkg.littlewriter.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class PageDTO {
    private String context;
    private String sketchImageUrl;
    private String coloredImageUrl;
    private String characterActionInfo;
    private int pageNumber;
}
