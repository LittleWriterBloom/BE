package com.pkg.littlewriter.domain.generativeAi.openAiModels;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pkg.littlewriter.domain.generativeAi.GenerativeAi;
import com.pkg.littlewriter.domain.generativeAi.GenerativeAiResponse;
import com.pkg.littlewriter.domain.generativeAi.Jsonable;
import com.pkg.littlewriter.domain.generativeAi.UrlJsonable;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.OpenAiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Component
public class ImageToTextGenerator implements GenerativeAi {
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private OpenAiService openAiService;
    @Value("${openai-api.access-key}")
    String key;
    private static final ChatMessage SYSTEM_MESSAGE = new ChatMessage("system", """ 
            you're a helpful assistant who depict character images.
            depict with details with only keywords seperated by comma without line break.
            - hairstyle
            - clothes
            - shoes
            - hair color
            - pants
            example :
             hairstyle : spiky, clothes: white shirt, shoes: brown shoes, hair-color: brown, pants: blue jeans"""
    );

    private static final String payload = """
            { 
            "model": "gpt-4-turbo",
            "messages": [
              { "role" : "system",
                "content" : " you're a helpful assistant who depict character's appearance. depict with details only using only nouns and adjectives seperated by comma, so that can redraw it with keywords again. depict with details using given description with only keywords seperated by comma without line break.you must depict- hairstyle- top clothes- pants- shoes- hair color and length example : a boy with brown short hair, red shirt, blue shoes, brown pants if you cannot distinguish image return blank space"
              },
              {
                "role": "user",
                "content": [
                  {
                    "type": "image_url",
                    "image_url": {
                      "url": " """ ;
        private static final String payload2  = """  
            "
            }
            }
            ]
            }
            ],
            "max_tokens": 1000}
            """;

    @Override
    public GenerativeAiResponse getResponse(Jsonable jsonable) throws JsonProcessingException {
        UrlJsonable urlJsonable =  (UrlJsonable) jsonable;
        String body = payload + urlJsonable.getUrl() + payload2;
        String header = "Bearer " + key;
        System.out.println(body);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.openai.com/v1/chat/completions"))
                .header("Content-Type", "application/json")
                .header("Authorization", header)
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();
        HttpClient client = HttpClient.newBuilder().build();
        try {
            String response = client.send(request, HttpResponse.BodyHandlers.ofString()).body();
            System.out.println(response);
            JsonNode jsonNode = objectMapper.readTree(response);
            String content = jsonNode.get("choices").get(0).get("message").get("content").textValue();
            return () -> content;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
