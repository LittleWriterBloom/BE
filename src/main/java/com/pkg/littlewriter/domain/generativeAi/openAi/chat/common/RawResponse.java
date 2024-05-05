package com.pkg.littlewriter.domain.generativeAi.openAi.chat.common;

import com.pkg.littlewriter.domain.generativeAi.openAi.OpenAiResponse;

public class RawResponse implements OpenAiResponse {
    private final String contentString;

    public RawResponse(String chatResponse) {
        this.contentString = chatResponse;
    }

    @Override
    public String getMessage() {
        return contentString;
    }
}
