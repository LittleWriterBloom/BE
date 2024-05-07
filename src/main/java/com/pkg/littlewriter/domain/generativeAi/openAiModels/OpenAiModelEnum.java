package com.pkg.littlewriter.domain.generativeAi.openAiModels;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OpenAiModelEnum {
    GPT_3_5_TURBO("gpt-3.5-turbo"),
    GPT_3_5_TURBO_1106("gpt-3.5-turbo-1106"),
    GPT_3_5_TURBO_16K("gpt-3.5-turbo-16k"),
    CONTEXT_QUESTION_FINE_TUNED("ft:gpt-3.5-turbo-0125:personal::9IxMV10R"),
    GPT_4("gpt-4"),
    GPT_4_TURBO("gpt-4-turbo"),
    GPT_4_TURBO_PREVIEW("gpt-4-turbo-preview"),
    GPT_4_VISION_PREVIEW("gpt-4-vision-preview"),
    DALL_E_2("dall-e-2"),
    DALL_E_3("dall-e-3");
    private final String name;
}