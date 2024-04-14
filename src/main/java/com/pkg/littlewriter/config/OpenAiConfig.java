package com.pkg.littlewriter.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.theokanning.openai.client.OpenAiApi;
import com.theokanning.openai.service.OpenAiService;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpLogging;
import retrofit2.Retrofit;

import java.time.Duration;
import java.time.temporal.TemporalUnit;
import java.util.concurrent.TimeUnit;

import static com.theokanning.openai.service.OpenAiService.*;

@Slf4j
@Configuration
public class OpenAiConfig {
    @Value("${openai-api.access-key}")
    private String key;

    @Bean
    public OpenAiService openAiService() {
/*        log.info("openai {}",key);
        OpenAiService openAiService = new OpenAiService(key);
        log.info("openai ??? {}", openAiService.toString());*/
//        OpenAiService service = new OpenAiService(api);
        ObjectMapper mapper = defaultObjectMapper();
        OkHttpClient client = defaultClient(key, Duration.ofSeconds(360))
                .newBuilder()
                .build();
        Retrofit retrofit = defaultRetrofit(client, mapper);
        OpenAiApi api = retrofit.create(OpenAiApi.class);
        return new OpenAiService(api);
    }
}
