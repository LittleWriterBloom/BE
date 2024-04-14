package com.pkg.littlewriter.service;


import com.pkg.littlewriter.domain.model.redis.BookInProgressRedis;
import com.pkg.littlewriter.dto.PageDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class BookInProgressRedisService {
    private static final String KEY = "book_in_progress";
    private static final Long EXPIRATION_SEC = 3600L;
    private final HashOperations<String, String, BookInProgressRedis> hashOperations;

    @Autowired
    public BookInProgressRedisService(RedisTemplate<String, BookInProgressRedis> bookInProgressRedisRedisTemplate) {
        this.hashOperations = bookInProgressRedisRedisTemplate.opsForHash();
        bookInProgressRedisRedisTemplate.expire(KEY, EXPIRATION_SEC, TimeUnit.SECONDS);
    }

    public void put(BookInProgressRedis bookInProgressRedis) {
        validate(bookInProgressRedis);
        hashOperations.put(KEY, bookInProgressRedis.getUserId().toString(), bookInProgressRedis);
    }


    public BookInProgressRedis getByUserId(Long userId) {
        String userIdString = String.valueOf(userId);
        return hashOperations.get(KEY, userIdString);
    }

    public BookInProgressRedis deleteByUserId(Long userId) {
        BookInProgressRedis deletedBookInProgressRedis = getByUserId(userId);
        hashOperations.delete(KEY, String.valueOf(userId));
        return deletedBookInProgressRedis;
    }

    public BookInProgressRedis appendPageTo(Long userId, PageDTO pageDTO) {
        BookInProgressRedis previous = getByUserId(userId);
        List<PageDTO> pages = previous.getPreviousPages();
        pages.add(pageDTO);
        return update(previous);
    }

    public BookInProgressRedis update(BookInProgressRedis bookInProgressRedis) {
        Long userId = bookInProgressRedis.getUserId();
        if(existsByUserId(userId)) {
            hashOperations.put(KEY, String.valueOf(bookInProgressRedis.getUserId()), bookInProgressRedis);
            return getByUserId(userId);
        }
        throw new IllegalArgumentException("no redis entity");
    }

    public boolean existsByUserId(Long userId) {
       return hashOperations.hasKey(KEY, String.valueOf(userId));
    }

    private void validate(BookInProgressRedis bookInProgressRedis) {
        if (bookInProgressRedis.getBookId() == null || bookInProgressRedis.getUserId() == null) {
            throw new IllegalArgumentException("bookInProgress field cannot be null");
        }
    }
}
