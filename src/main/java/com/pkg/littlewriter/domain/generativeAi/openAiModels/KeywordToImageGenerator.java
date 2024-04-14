package com.pkg.littlewriter.domain.generativeAi.openAiModels;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.pkg.littlewriter.domain.generativeAi.GenerativeAi;
import com.pkg.littlewriter.domain.generativeAi.GenerativeAiResponse;
import com.pkg.littlewriter.domain.generativeAi.ImageKeywordJsonable;
import com.pkg.littlewriter.domain.generativeAi.Jsonable;
import com.theokanning.openai.image.CreateImageRequest;
import com.theokanning.openai.service.OpenAiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class KeywordToImageGenerator implements GenerativeAi {
    @Autowired
    private OpenAiService openAiService;
    private static final String BASIC_PROMPT = """
                                       draw a cartoon illustration about
            """;
    private static final String STYLE_PROMPT = """
            you must follow
            - do not contain any characters
            - using very thick and heavy black strokes for borderlines,
            - draw objects with simplicity
            - do not draw small objects
            - using soft, vivid, bright and similar color
            - using Eric Carl's illustration style""";

    @Override
    public GenerativeAiResponse getResponse(Jsonable keyWordJsonable) throws JsonProcessingException {
        ImageKeywordJsonable imageKeywordJsonable = (ImageKeywordJsonable) keyWordJsonable;
        CreateImageRequest request = CreateImageRequest.builder()
                .model(OpenAiModelEnum.DALL_E_2.getName())
                .quality("standard")
                .size("512x512")
                .prompt(createPrompt(imageKeywordJsonable.getKeyword()))
                .n(1)
                .build();
        String resultImageUrl = openAiService.createImage(request)
                .getData()
                .get(0)
                .getUrl();
        return () -> resultImageUrl;
    }

    private String createPrompt(String keywords) {
        return BASIC_PROMPT + keywords + STYLE_PROMPT;
    };
}
