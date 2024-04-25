package com.pkg.littlewriter.domain.generativeAi.openAiModels;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.pkg.littlewriter.domain.generativeAi.GenerativeAi;
import com.pkg.littlewriter.domain.generativeAi.GenerativeAiResponse;
import com.pkg.littlewriter.domain.generativeAi.Jsonable;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.OpenAiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ContextRefiner implements GenerativeAi {
    @Autowired
    private OpenAiService openAiService;
    private static final ChatMessage SYSTEM_MESSAGE = new ChatMessage("system",
            """
                    now you're a fairytale writer.
                    generate sentences for fairytale.
                    you should follow
                    - if given sentences are awkward, change naturally
                    - enrich sentences
                    - answer in korean.
                    - ~해요 체로 바꿔"""
    );

    @Override
    public GenerativeAiResponse getResponse(Jsonable contextJsonable) throws JsonProcessingException {
        ChatMessage fairyTaleInfo = new ChatMessage("user", contextJsonable.toJsonString());
        ChatCompletionRequest request = ChatCompletionRequest.builder()
                .model(OpenAiModelEnum.GPT_4_TURBO_PREVIEW.getName())
                .messages(List.of(SYSTEM_MESSAGE, fairyTaleInfo))
                .temperature(0.5)
                .maxTokens(300)
                .build();
        ChatMessage response = openAiService.createChatCompletion(request)
                .getChoices()
                .get(0)
                .getMessage();
        return new GptResponse(response);
    }
}
