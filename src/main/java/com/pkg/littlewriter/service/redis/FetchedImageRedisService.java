package com.pkg.littlewriter.service.redis;

import com.pkg.littlewriter.domain.generativeAi.stableDiffusion.ImageResponse;
import com.pkg.littlewriter.domain.model.redis.FetchImageQueueRedis;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Queue;
import java.util.concurrent.TimeUnit;

@Service
public class FetchedImageRedisService {
    private static final String KEY = "fetch_image";
    private static final Long EXPIRATION_SEC = 3600L;
    private final HashOperations<String, String, FetchImageQueueRedis> hashOperations;

    @Autowired
    public FetchedImageRedisService(RedisTemplate<String, FetchImageQueueRedis> fetchImageQueueRedisRedisTemplate) {
        this.hashOperations = fetchImageQueueRedisRedisTemplate.opsForHash();
        fetchImageQueueRedisRedisTemplate.expire(KEY, EXPIRATION_SEC, TimeUnit.SECONDS);
    }

    public void put(FetchImageQueueRedis fetchImageQueueRedis) {
        hashOperations.put(KEY, fetchImageQueueRedis.getBookInProgressBookId(), fetchImageQueueRedis);
    }

    public FetchImageQueueRedis getByBookId(String bookId) {
        return hashOperations.get(KEY, bookId);
    }

    public FetchImageQueueRedis appendTo(String bookId, ImageResponse imageResponse) {
        FetchImageQueueRedis fetchedImages = hashOperations.get(KEY, bookId);
        Queue<ImageResponse> images = fetchedImages.getFetchedImages();
        images.add(imageResponse);
        fetchedImages.setFetchedImages(images);
        put(fetchedImages);
        return getByBookId(fetchedImages.getBookInProgressBookId());
    }
}
