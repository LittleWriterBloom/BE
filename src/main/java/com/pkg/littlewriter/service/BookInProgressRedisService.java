package com.pkg.littlewriter.service;

import com.pkg.littlewriter.domain.model.redis.BookInProgressIdCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class BookInProgressRedisService {
    private static final String KEY = "book_in_progress";
    private static final Long EXPIRATION_SEC = 3600L;
    private final HashOperations<String, String, BookInProgressIdCache> hashOperations;

    @Autowired
    public BookInProgressRedisService(RedisTemplate<String, BookInProgressIdCache> redisTemplate) {
        this.hashOperations = redisTemplate.opsForHash();
        redisTemplate.expire(KEY, EXPIRATION_SEC, TimeUnit.SECONDS);
    }

    public void save(BookInProgressIdCache bookInProgressIdCache) {
        validateBookInProgress(bookInProgressIdCache);
        hashOperations.put(KEY, bookInProgressIdCache.getUserId(), bookInProgressIdCache);
    }

    public BookInProgressIdCache getByUserId(Long userId) {
        String userIdString = String.valueOf(userId);
        return hashOperations.get(KEY, userIdString);
    }

    public boolean existsByUserId(Long userId) {
        String userIdString = String.valueOf(userId);
        return hashOperations.hasKey(KEY, userIdString);
    }

    private void validateBookInProgress(BookInProgressIdCache bookInProgressIdCache) {
        if(bookInProgressIdCache.getBookId() == null || bookInProgressIdCache.getUserId() == null) {
            throw new IllegalArgumentException("bookInProgress field cannot be null");
        }
    }
}
