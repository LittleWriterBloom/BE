package com.pkg.littlewriter.dto;

import com.pkg.littlewriter.domain.model.BookEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RetrieveBookCoverResponseDTO {
    private List<BookCoverDTO> books;
    private Pagination pageInfo;
}
