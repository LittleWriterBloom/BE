package com.pkg.littlewriter.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class BookCoverDTO {
    private String firstPageImageUrl;
    private CharacterDTO character;
    private String title;
    private String author;
    private String userId;
    private Date createDate;
    private Long bookColor;
}
