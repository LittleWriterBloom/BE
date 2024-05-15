package com.pkg.littlewriter.domain.generativeAi.openAi.chat.keyword;

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
public class ContextKeyWordExtractorStableDiffusion implements OpenAi<RawResponse, KeyWordExtractorInputJsonable> {
    @Autowired
    private OpenAiService openAiService;

    private static final ChatMessage SYSTEM_MESSAGE = new ChatMessage("system",
            """
                    you're a helpful assistant that depict details to generate image.
                    using given json, depict background.
                    characters descriptions and appearance details will be given.
                    you should follow
                    - depict current background using base on "currentContext"
                    - choose random format from given 3 formats
                     1. <characterAppearanceKeyword> is <what character is doing in "currentContext">, in the <imagine the details of background in "currentContext">
                     2. <imagine the details of background in "currentContext">
                     3. <encounters> in <imagine the details of background in "currentContext">
                    - example of each format
                     1. a red colored fox with short tail and pointy ears is chased by a bear, in the old forest
                     2. a cabin with old rusty metal plates
                     3. a furious shark in deep blue ocean
                     answer choose random format from above"""
    );

    @Override
    public RawResponse getResponse(KeyWordExtractorInputJsonable jsonable) throws OpenAiException {
        try{
            ChatMessage fairyTaleInfo = new ChatMessage("user", jsonable.toJsonString());
            ChatCompletionRequest request = ChatCompletionRequest.builder()
                    .model(OpenAiModelEnum.GPT_4_TURBO_PREVIEW.getName())
                    .messages(List.of(SYSTEM_MESSAGE, fairyTaleInfo))
                    .temperature(0.5)
                    .maxTokens(500)
                    .build();
            ChatMessage response = openAiService.createChatCompletion(request)
                    .getChoices()
                    .get(0)
                    .getMessage();
            System.out.println(response.getContent());
            return new RawResponse(response.getContent());
        } catch (JsonProcessingException e) {
            throw new OpenAiException(e.getMessage());
        }
    }
}
