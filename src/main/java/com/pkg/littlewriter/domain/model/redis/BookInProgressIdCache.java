package com.pkg.littlewriter.domain.model.redis;

import com.pkg.littlewriter.dto.PageDTO;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookInProgressIdCache {
    private String bookId;
    private String userId;
}
