package com.pkg.littlewriter.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.joda.time.DateTime;

import java.util.Date;
import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BookDTO {
    private String id;
    private String title;
    private Long userId;
    private Long characterId;
    private List<PageDTO> pages;
    private Date createDate;
    private Long bookColor;
    private String author;
    private int storyLength;
}
