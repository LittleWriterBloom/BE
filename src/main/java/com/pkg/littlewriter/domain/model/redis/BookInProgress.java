package com.pkg.littlewriter.domain.model.redis;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookInProgress {
    private String bookId;
    private String userId;
}
