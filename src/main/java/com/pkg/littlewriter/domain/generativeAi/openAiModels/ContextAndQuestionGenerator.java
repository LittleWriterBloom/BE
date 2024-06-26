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
public class ContextAndQuestionGenerator implements GenerativeAi {
    @Autowired
    private OpenAiService openAiService;
    private static final ChatMessage SYSTEM_MESSAGE = new ChatMessage("system",
            """
                      you're a helpful assistant who helps writing a fairy tale
                        if senteces are given
                        first, enrich senteces more naturally in fairy tale storybook style, using ~해요 체
                        second, create 3-short question about current senteces
                          questions can based on character's personality
                          questions must related to what will happen next
                          questions must sepearted by linebreak. no index.
                          questions must be korean
                        answer format must be json
                        {
                          "refinedText" : "",
                          "questions" : []
                        }
                    """
    );

    @Override
    public GenerativeAiResponse getResponse(Jsonable contextJsonable) throws JsonProcessingException {
        ChatMessage fairyTaleInfo = new ChatMessage("user", contextJsonable.toJsonString());
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
        return new GptResponse(response);
    }
}
