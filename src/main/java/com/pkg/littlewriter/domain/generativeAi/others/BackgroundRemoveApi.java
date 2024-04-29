package com.pkg.littlewriter.domain.generativeAi.others;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class BackgroundRemoveApi {
    @Value("${removal-api.access-key}")
    String key;
    private static final String ENDPOINT = "https://api.removal.ai/3.0/remove";
    private final ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    public BackgroundRemoveResponse removeBackground(String imageUrl) throws IOException {
        OkHttpClient client = new OkHttpClient();
        RequestBody formBody = new FormBody.Builder()
                .add("image_url", imageUrl)
                .build();
        Request request = new Request.Builder()
                .url(ENDPOINT)
                .header("accept", "application/json")
                .header("RM-Token", key)
                .header("Content-Type", "multipart/form-data")
                .post(formBody)
                .build();
        Response response = client.newCall(request).execute();
        String responseString = response.body().string();
        return objectMapper.readValue(responseString, BackgroundRemoveResponse.class);
    }
}
