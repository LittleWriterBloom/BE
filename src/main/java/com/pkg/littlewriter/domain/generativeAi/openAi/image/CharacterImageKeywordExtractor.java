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
public class CharacterImageKeywordExtractor implements OpenAi<RawResponse, CharacterImageToTextInputJsonable> {
    @Value("${openai-api.access-key}")
    String key;

    private static final String PROMPT = "you're a helpful assistant who depict character's appearance. depict with details only using only nouns, adjectives and conjunctions  - if it's human, depict shirt , pants , hair color and length, shoes - if it's animal or others, depict color, species and other details - do not depict facial expression - first word : given description if not English, translate to English - depict with details using given description with only using one sentence.";

    private static final String payload = """
            {   "model": "gpt-4-turbo",
                "messages": [{"role" : "user","content" : [""";
    private static final String payload2 = """  
            ],
            "max_tokens": 1000}""";

    private static HttpRequest createHttpRequestFrom(String body, String header) {
        return HttpRequest.newBuilder()
                .uri(URI.create("https://api.openai.com/v1/chat/completions"))
                .header("Content-Type", "application/json")
                .header("Authorization", header)
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();
    }

    @Override
    public RawResponse getResponse(CharacterImageToTextInputJsonable jsonable) throws OpenAiException {
        try {
            StringBuilder builder = new StringBuilder(payload);
            builder.append(getImageContent(jsonable.getImageUrl()));
            builder.append(getDescriptionContent(jsonable));
            builder.append(getPrompt());
            builder.append(payload2);
            String body = builder.toString();
            System.out.println(body);
            String header = "Bearer " + key;
            HttpRequest request = createHttpRequestFrom(body, header);
            HttpClient client = HttpClient.newBuilder().build();
            String response = client.send(request, HttpResponse.BodyHandlers.ofString()).body();
            System.out.println(response);
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

    private String getImageContent(String imageUrl) {
        StringBuilder builder = new StringBuilder("{\"type\":\"image_url\",");
        builder.append("\"image_url\": {");
        builder.append("\"url\": ");
        builder.append("\"").append(imageUrl).append("\"}},");
        System.out.println(builder.toString());
        return builder.toString();
    }

    private String getDescriptionContent(CharacterImageToTextInputJsonable jsonable) throws JsonProcessingException {
        StringBuilder builder = new StringBuilder("{\"type\":\"text\",");
        builder.append("\"text\": \"");
        builder.append("description : " + jsonable.getDescription()).append("\"},");
        return builder.toString();
    }

    private String getPrompt() {
        StringBuilder builder = new StringBuilder("{\"type\": \"text\",");
        builder.append("\"text\": \"");
        builder.append(PROMPT + "\"}]}");
        return builder.toString();
    }


}
