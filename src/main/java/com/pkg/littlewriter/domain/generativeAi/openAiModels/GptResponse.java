package com.pkg.littlewriter.domain.generativeAi.openAiModels;

import com.pkg.littlewriter.domain.generativeAi.GenerativeAiResponse;
import com.theokanning.openai.completion.chat.ChatMessage;

public class GptResponse implements GenerativeAiResponse {
    private final String chatCompletionMessageContent;

    public GptResponse(ChatMessage chatMessage) {
        this.chatCompletionMessageContent = chatMessage.getContent();
    }
    @Override
    public String getMessage() {
        return chatCompletionMessageContent;
    }
}
