package com.pkg.littlewriter.domain.generativeAi.openAi.chat.dictionary;

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
public class ContextDictionary implements OpenAi<RawResponse, ContextDictionaryInputJsonable> {
    @Autowired
    private OpenAiService openAiService;

    private static final ChatMessage SYS_MESSAGE = new ChatMessage("system",
            """
            you're a helpful assistant who explain words to kids using simple and easy words. your task is to explain meaning of the words when asked.
            if there's question not related to your task or asked words are not in the given context, answer : 그 질문은 잘 모르겠어요
            context will be given.   
            while explaining, should follow
            first-sentence : explain words in a simple and easy way include dictionary definition. Do not use asked words while explaining it.
            second-sentence: give a simple example of usage and explain.
            use only Korean""");

    @Override
    public RawResponse getResponse(ContextDictionaryInputJsonable jsonable) throws OpenAiException {
        try{
            ChatMessage userRequestMessage = new ChatMessage("user", jsonable.toJsonString());
            ChatCompletionRequest request = ChatCompletionRequest.builder()
                    .maxTokens(1000)
                    .model(OpenAiModelEnum.GPT_4_TURBO_PREVIEW.getName())
                    .temperature(0.5)
                    .messages(List.of(SYS_MESSAGE,userRequestMessage))
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
