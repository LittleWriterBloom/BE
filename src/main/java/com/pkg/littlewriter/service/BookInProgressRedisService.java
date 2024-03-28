package com.pkg.littlewriter.service;

import com.pkg.littlewriter.domain.model.redis.BookInProgress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class BookInProgressRedisService {
    private static final String KEY = "book_in_progress";
    private static final Long EXPIRATION_SEC = 3600L;
    private final HashOperations<String, String, BookInProgress> hashOperations;

    @Autowired
    public BookInProgressRedisService(RedisTemplate<String, BookInProgress> redisTemplate) {
        this.hashOperations = redisTemplate.opsForHash();
        redisTemplate.expire(KEY, EXPIRATION_SEC, TimeUnit.SECONDS);
    }

    public void save(BookInProgress bookInProgressRedisEntity) {
        validateBookInProgress(bookInProgressRedisEntity);
        hashOperations.put(KEY, bookInProgressRedisEntity.getUserId(), bookInProgressRedisEntity);
    }

    public BookInProgress getByUserId(Long userId) {
        String userIdString = String.valueOf(userId);
        return hashOperations.get(KEY, userIdString);
    }

    public boolean existsByUserId(Long userId) {
        String userIdString = String.valueOf(userId);
        return hashOperations.hasKey(KEY, userIdString);
    }

    private void validateBookInProgress(BookInProgress bookInProgress) {
        if(bookInProgress.getBookId() == null || bookInProgress.getUserId() == null) {
            throw new IllegalArgumentException("bookInProgress field cannot be null");
        }
    }
}
