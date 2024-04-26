package com.pkg.littlewriter.domain.model.redis;

import com.pkg.littlewriter.dto.PageDTO;
import lombok.*;

import java.util.List;

@Getter
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookInProgressRedis {
    private String backgroundInfo;
    private String bookId;
    private Long characterId;
    private Long userId;
    private List<PageDTO> previousPages;
    private int storyLength;
}
