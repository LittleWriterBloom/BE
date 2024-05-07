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
public class SketchKeywordExtractor implements OpenAi<RawResponse, SketchKeywordExtractorInputJsonable> {
    @Autowired
    private OpenAiService openAiService;
    private static final ChatMessage SYSTEM_MESSAGE = new ChatMessage("system",
            """
                    you're a helpful assistant that depict details to generate image.
                    using given json, depict "currentContext" scene.
                    you should follow
                  - depict current scene in "mainCharacter"'s perspect of view
                  - do not contain any information of "mainCharacter"
                  - depict info must follow one of these format
                    [character] is [character's posture], in [background], [lights]
                    [object] is in [background], [lights]
                  - answer must be in english"""
    );

    @Override
    public RawResponse getResponse(SketchKeywordExtractorInputJsonable contextJsonable) throws OpenAiException {
        try{
            ChatMessage fairyTaleInfo = new ChatMessage("user", contextJsonable.toJsonString());
            ChatCompletionRequest request = ChatCompletionRequest.builder()
                    .model(OpenAiModelEnum.GPT_4_TURBO.getName())
                    .messages(List.of(SYSTEM_MESSAGE, fairyTaleInfo))
                    .temperature(0.5)
                    .maxTokens(200)
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
