package com.pkg.littlewriter.utils.tts;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

@Service
public class NaverTtsService {
    @Value("${naver-tts-api.client-id}")
    private String clientId;
    @Value("${naver-tts-api.client-secret}")
    private String clientSecret;
    private final String ENDPOINT = "https://naveropenapi.apigw.ntruss.com/tts-premium/v1/tts";
    private static final ObjectMapper MAPPER = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    public NaverTtsResponseDto toVoice(NaverTtsRequestBodyDto naverTtsRequestBodyDto) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(ENDPOINT))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("X-NCP-APIGW-API-KEY-ID", clientId)
                .header("X-NCP-APIGW-API-KEY", clientSecret)
                .POST(HttpRequest.BodyPublishers.ofString(naverTtsRequestBodyDto.toDataString()))
                .build();
        HttpClient client = HttpClient.newHttpClient();
//        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
//        String body = response.body();
        HttpResponse<byte[]> rawResponse = client.send(request, HttpResponse.BodyHandlers.ofByteArray());
        byte[] body = rawResponse.body();
        int code = rawResponse.statusCode();
        if(code == 200) {
            return NaverTtsResponseDto.builder()
                    .code(code)
                    .mp3Binary(body).build();
        }
        String message = new String(body, StandardCharsets.UTF_8);
        return NaverTtsResponseDto.builder()
                .code(code)
                .message(message)
                .build();
    }
}
