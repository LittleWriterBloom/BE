package com.pkg.littlewriter.config;

import com.pkg.littlewriter.domain.model.redis.BookInProgressRedis;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;

@EnableRedisRepositories
@Configuration
public class RedisConfig {
    @Value("${spring.data.redis.host}")
    private String host;
    @Value("${spring.data.redis.port}")
    private int port;

    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory(new RedisStandaloneConfiguration(host, port));
    }

    @Bean
    public RedisTemplate<String, BookInProgressRedis> bookInProgressRedisRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, BookInProgressRedis> template = new RedisTemplate<>();
        template.setDefaultSerializer(RedisSerializer.string());
        template.setHashValueSerializer(new Jackson2JsonRedisSerializer<>(BookInProgressRedis.class));
        template.setConnectionFactory(redisConnectionFactory);
        return template;
    }
}
