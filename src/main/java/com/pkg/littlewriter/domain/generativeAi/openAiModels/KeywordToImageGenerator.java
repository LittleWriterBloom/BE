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
    private static final String STYLE_PROMPT = " cute children's simple cartoon illustration. vivid color, flat 2D with simple shading";

    @Override
    public GenerativeAiResponse getResponse(Jsonable keyWordJsonable) throws JsonProcessingException {
        ImageKeywordJsonable imageKeywordJsonable = (ImageKeywordJsonable) keyWordJsonable;
        CreateImageRequest request = CreateImageRequest.builder()
                .model(OpenAiModelEnum.DALL_E_3.getName())
                .quality("standard")
                .size("1024x1024")
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
        return keywords + STYLE_PROMPT;
    };
}
