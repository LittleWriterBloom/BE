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
public class KeywordExtractorStableDiffusion implements GenerativeAi {
    @Autowired
    private OpenAiService openAiService;
    private static final ChatMessage SYSTEM_MESSAGE = new ChatMessage("system",
            """
                    you're a helpful assistant that depict details to generate image.
                    using given json, depict background.
                    characters descriptions and appearance details will be given.
                    you should follow
                    - depict current background using base on "currentContext"
                    - use simple sentence
                    - depict current context's objects.
                    - first sentence : depict background using "currentContext" and "backgroundInfo"
                    - second sentence : depict main character using "characterAppearanceKeywords"
                    - third sentence : depict the light and facial expression.
                    - sentence is made of keywords, seperated by comma
                    for example:
                    forest with dark light, wooden hut in the background
                    a boy with short hair, yellow striped shirt, blue sneakers, with long black hair and red jeans.
                    surprising face, dark, dim light
                    answer only in english"""
    );

    @Override
    public GenerativeAiResponse getResponse(Jsonable contextJsonable) throws JsonProcessingException {
        ChatMessage fairyTaleInfo = new ChatMessage("user", contextJsonable.toJsonString());
        ChatCompletionRequest request = ChatCompletionRequest.builder()
                .model(OpenAiModelEnum.GPT_4_TURBO_PREVIEW.getName())
                .messages(List.of(SYSTEM_MESSAGE, fairyTaleInfo))
                .temperature(0.5)
                .maxTokens(100)
                .build();
        ChatMessage response = openAiService.createChatCompletion(request)
                .getChoices()
                .get(0)
                .getMessage();
        return new GptResponse(response);
    }
}

