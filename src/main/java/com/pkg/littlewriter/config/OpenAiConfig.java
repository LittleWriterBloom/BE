package com.pkg.littlewriter.config;

import com.theokanning.openai.service.OpenAiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class OpenAiConfig {
    @Value("${openai-api.access-key}")
    private String key;

    @Bean
    public OpenAiService openAiService() {
        log.info("openai {}",key);
        OpenAiService openAiService = new OpenAiService(key);
        log.info("openai ??? {}", openAiService.toString());
        return openAiService;
    }
}
