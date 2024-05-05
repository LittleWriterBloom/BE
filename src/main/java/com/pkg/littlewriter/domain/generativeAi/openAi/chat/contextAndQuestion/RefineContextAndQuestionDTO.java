package com.pkg.littlewriter.domain.generativeAi.openAi.chat.contextAndQuestion;

import com.pkg.littlewriter.domain.generativeAi.openAi.OpenAiResponse;
import com.pkg.littlewriter.domain.generativeAi.openAi.chat.common.RefineContextAndQuestionResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class RefineContextAndQuestionDTO implements OpenAiResponse {
    private final RefineContextAndQuestionResponse response;
    private final String originResponse;

    @Override
    public String getMessage() {
        return response.getRefinedText();
    }
}
