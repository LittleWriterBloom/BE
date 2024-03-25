package com.pkg.littlewriter.domain.generativeAi.openAiModels;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.pkg.littlewriter.domain.generativeAi.GenerativeAi;
import com.pkg.littlewriter.domain.generativeAi.GenerativeAiResponse;
import com.pkg.littlewriter.domain.generativeAi.Jsonable;
import com.theokanning.openai.image.CreateImageRequest;
import com.theokanning.openai.service.OpenAiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class KeywordToImageGenerator implements GenerativeAi {
    @Autowired
    private OpenAiService openAiService;

    @Override
    public GenerativeAiResponse getResponse(Jsonable keyWordJsonable) throws JsonProcessingException {
        CreateImageRequest request = CreateImageRequest.builder()
                .model(OpenAiModelEnum.DALL_E_2.getName())
                .quality("standard")
                .size("512x512")
                .prompt(keyWordJsonable.toJsonString())
                .n(1)
                .build();
        String resultImageUrl = openAiService.createImage(request)
                .getData()
                .get(0)
                .getUrl();
        return () -> resultImageUrl;
    }
}
