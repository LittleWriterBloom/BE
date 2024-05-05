package com.pkg.littlewriter.domain.generativeAi.openAi.chat.contextEricher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.pkg.littlewriter.domain.generativeAi.openAi.OpenAi;
import com.pkg.littlewriter.domain.generativeAi.openAi.OpenAiException;
import com.pkg.littlewriter.domain.generativeAi.openAi.chat.common.RawResponse;
import com.pkg.littlewriter.domain.generativeAi.openAiModels.OpenAiModelEnum;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.OpenAiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ContextEnricher2 implements OpenAi<RawResponse, ContextEnricherInputJsonable> {
    @Autowired
    private OpenAiService openAiService;
    private static final ChatMessage SYSTEM_MESSAGE = new ChatMessage("system",
            """
                    now you're a fairytale writer.
                    generate sentences for fairytale.
                    you should follow
                    - if given sentences are awkward, change naturally
                    - enrich given sentence to 3 - 5 sentences with details
                    - imagine the details when lack of information
                    - can contain conversations with other character
                    - enrich sentences
                    - answer in korean.
                    - ~해요 체로 바꿔"""
    );

    @Override
    public RawResponse getResponse(ContextEnricherInputJsonable jsonable) throws OpenAiException {
        try {
        ChatMessage fairyTaleInfo = new ChatMessage("user", jsonable.toJsonString());

        ChatCompletionRequest request = ChatCompletionRequest.builder()
                .model(OpenAiModelEnum.GPT_4_TURBO_PREVIEW.getName())
                .messages(List.of(SYSTEM_MESSAGE, fairyTaleInfo))
                .temperature(0.5)
                .maxTokens(1000)
                .build();
        ChatMessage response = openAiService.createChatCompletion(request)
                .getChoices()
                .get(0)
                .getMessage();
        return new RawResponse(response.getContent());
        } catch (JsonProcessingException e) {
            throw new OpenAiException(e.getMessage());
        }
    }
}
