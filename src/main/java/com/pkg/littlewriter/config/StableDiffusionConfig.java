package com.pkg.littlewriter.config;

import com.pkg.littlewriter.domain.generativeAi.stableDiffusion.api.StableDiffusionApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StableDiffusionConfig {
    @Value("${models-lab-api.access-key}")
    private String key;
    @Bean
    StableDiffusionApi stableDiffusionApi() {
        return new StableDiffusionApi(key);
    }
}
