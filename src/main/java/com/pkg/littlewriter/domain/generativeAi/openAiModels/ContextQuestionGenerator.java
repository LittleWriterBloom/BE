package com.pkg.littlewriter.domain.generativeAi.openAiModels;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.pkg.littlewriter.domain.generativeAi.GenerativeAi;
import com.pkg.littlewriter.domain.generativeAi.GenerativeAiResponse;
import com.pkg.littlewriter.domain.generativeAi.Jsonable;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.OpenAiService;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@NoArgsConstructor
public class ContextQuestionGenerator implements GenerativeAi {
    @Autowired
    private OpenAiService openAiService;
    private static final ChatMessage SYSTEM_MESSAGE = new ChatMessage("system",
            """
                    you're a helpful assistant who helps fairytale writer to continue the story
                    create a 3-question to help continuing fairytale story
                    question must related to how the next story will be.
                    questions should based on
                    - character's traits
                    - how to act like
                    - how to converse with others
                    - background of the story
                    - last content of given story lines
                    - be specific
                    answer in Korean"""
    );

    @Override
    public GenerativeAiResponse getResponse(Jsonable fairyTaleJsonable) throws JsonProcessingException {
        ChatMessage fairyTaleInfo = new ChatMessage("user", fairyTaleJsonable.toJsonString());
        ChatCompletionRequest request = ChatCompletionRequest.builder()
                .model(OpenAiModelEnum.GPT_3_5_TURBO_16K.getName())
                .messages(List.of(SYSTEM_MESSAGE, fairyTaleInfo))
                .temperature(0.6)
                .maxTokens(1000)
                .build();
        ChatMessage response = openAiService.createChatCompletion(request)
                .getChoices()
                .get(0)
                .getMessage();
        return new GptResponse(response);
    }
}
