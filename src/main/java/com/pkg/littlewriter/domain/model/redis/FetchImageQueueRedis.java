package com.pkg.littlewriter.domain.model.redis;

import com.pkg.littlewriter.domain.generativeAi.stableDiffusion.ImageResponse;
import lombok.*;

import java.util.Queue;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FetchImageQueueRedis {
    private String bookInProgressBookId;
    private Queue<ImageResponse> fetchedImages;
}
