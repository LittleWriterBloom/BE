package com.pkg.littlewriter.domain.generativeAi.openAi.chat.contextAndQuestion;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.pkg.littlewriter.domain.generativeAi.openAi.OpenAi;
import com.pkg.littlewriter.domain.generativeAi.openAi.OpenAiException;
import com.pkg.littlewriter.domain.generativeAi.openAi.chat.common.BookBuildJsonable;
import com.pkg.littlewriter.domain.generativeAi.openAi.chat.common.RefineContextAndQuestionResponse;
import com.pkg.littlewriter.domain.generativeAi.openAiModels.OpenAiModelEnum;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.OpenAiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RefineContextAndQuestionGenerator implements OpenAi<RefineContextAndQuestionDTO, BookBuildJsonable> {
    @Autowired
    private OpenAiService openAiService;
    private static final ChatMessage SYSTEM_MESSAGE = new ChatMessage("system",
            """
                      you're a helpful assistant who helps writing a fairy tale
                        if sentences are given
                        first, enrich sentences more naturally in fairy tale storybook style, using ~해요 체
                        second, create 3-short question about current sentences
                          questions can based on character's personality
                          questions must related to what will happen next
                          questions must seperated by linebreak. no index.
                          questions must be korean
                        answer format must be json
                        {
                          "refinedText" : "",
                          "questions" : []
                        }
                    """
    );

    @Override
    public RefineContextAndQuestionDTO getResponse(BookBuildJsonable jsonable) throws OpenAiException {
        try {
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

            RefineContextAndQuestionResponse contextAndQuestionResponse = defaultMapper.readValue(response.getContent(), RefineContextAndQuestionResponse.class);
            return RefineContextAndQuestionDTO.builder()
                    .response(contextAndQuestionResponse)
                    .originResponse(response.getContent()).build();
        } catch (JsonProcessingException e) {
            throw new OpenAiException("gpt answer format does not follow RefineContexAndQuestionDTO.class : " + e.getMessage());
        }
    }
}
