package com.pkg.littlewriter.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Pagination<T> {
    private Integer totalPages;
    private Long totalElements;
    private Integer currentPage;
    private Integer currentElements;

    public Pagination (Page<T> page) {
        this.totalPages = page.getTotalPages();
        this.totalElements = page.getTotalElements();
        this.currentPage = page.getNumber();
        this.currentElements = page.getNumberOfElements();
    }
}