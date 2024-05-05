package com.pkg.littlewriter.domain.generativeAi.openAi.image;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.pkg.littlewriter.domain.generativeAi.openAi.OpenAi;
import com.pkg.littlewriter.domain.generativeAi.openAi.OpenAiException;
import com.pkg.littlewriter.domain.generativeAi.openAi.chat.common.OneAttributeJsonable;
import com.pkg.littlewriter.domain.generativeAi.openAi.chat.common.RawResponse;
import com.theokanning.openai.completion.chat.ChatMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Component
@Slf4j
public class CharacterImageToTextGenerator implements OpenAi<RawResponse, OneAttributeJsonable> {
    @Value("${openai-api.access-key}")
    String key;
    private static final ChatMessage SYSTEM_MESSAGE = new ChatMessage("system", """ 
            you're a helpful assistant who depict character's appearance.
              depict with details only using only nouns and adjectives
              depict color details
               - if it's human, depict shirt, pants, hair color and length, shoes
               - if it's animal or others, depict color, species and other details
               - do not depict facial expression
               - first word : given description
              depict with details using given description with only keywords seperated by comma without line break.
            """
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
                      "url": " """;
    private static final String payload2 = """  
            "
            }
            }
            ]
            }
            ],
            "max_tokens": 1000}
            """;

    @Override
    public RawResponse getResponse(OneAttributeJsonable jsonable) throws OpenAiException {
        try {
            String body = payload + jsonable.getValue() + payload2;
            String header = "Bearer " + key;
            HttpRequest request = createHttpRequestFrom(body, header);
            HttpClient client = HttpClient.newBuilder().build();
            String response = client.send(request, HttpResponse.BodyHandlers.ofString()).body();
            JsonNode jsonNode = defaultMapper.readTree(response);
            String content = jsonNode.get("choices")
                    .get(0)
                    .get("message")
                    .get("content")
                    .textValue();
            return new RawResponse(content);
        } catch (InterruptedException | IOException ie) {
            log.error("InterruptedException: ", ie);
            Thread.currentThread().interrupt();
            throw new OpenAiException(ie.getMessage());
        }
    }

    private static HttpRequest createHttpRequestFrom(String body, String header) {
        return HttpRequest.newBuilder()
                .uri(URI.create("https://api.openai.com/v1/chat/completions"))
                .header("Content-Type", "application/json")
                .header("Authorization", header)
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();
    }
}
