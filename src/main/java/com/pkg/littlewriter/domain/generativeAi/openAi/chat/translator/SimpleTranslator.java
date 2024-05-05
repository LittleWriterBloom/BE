package com.pkg.littlewriter.domain.generativeAi.openAi.chat.translator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.pkg.littlewriter.domain.generativeAi.openAi.OpenAi;
import com.pkg.littlewriter.domain.generativeAi.openAi.OpenAiException;
import com.pkg.littlewriter.domain.generativeAi.openAi.chat.common.OneAttributeJsonable;
import com.pkg.littlewriter.domain.generativeAi.openAi.chat.common.RawResponse;
import com.pkg.littlewriter.domain.generativeAi.openAiModels.OpenAiModelEnum;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.OpenAiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SimpleTranslator implements OpenAi<RawResponse, OneAttributeJsonable> {
    @Autowired
    private OpenAiService openAiService;
    private static final ChatMessage SYSTEM_MESSAGE = new ChatMessage("system",
            """
                    you're a helpful assistant who translate Korean to English
                    if text were given, translate to English"""
    );

    @Override
    public RawResponse getResponse(OneAttributeJsonable jsonable) throws OpenAiException {
        ChatMessage fairyTaleInfo = new ChatMessage("user", jsonable.getValue());
        ChatCompletionRequest request = ChatCompletionRequest.builder()
                .model(OpenAiModelEnum.GPT_3_5_TURBO.getName())
                .messages(List.of(SYSTEM_MESSAGE, fairyTaleInfo))
                .temperature(0.5)
                .maxTokens(100)
                .build();
        ChatMessage response = openAiService.createChatCompletion(request)
                .getChoices()
                .get(0)
                .getMessage();
        return new RawResponse(response.getContent());
    }
}
