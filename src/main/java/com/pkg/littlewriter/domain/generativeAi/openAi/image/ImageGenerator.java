package com.pkg.littlewriter.domain.generativeAi.openAi.image;

import com.pkg.littlewriter.domain.generativeAi.openAi.OpenAi;
import com.pkg.littlewriter.domain.generativeAi.openAi.chat.common.OneAttributeJsonable;
import com.pkg.littlewriter.domain.generativeAi.openAi.chat.common.RawResponse;
import com.pkg.littlewriter.domain.generativeAi.openAiModels.OpenAiModelEnum;
import com.theokanning.openai.image.CreateImageRequest;
import com.theokanning.openai.service.OpenAiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ImageGenerator implements OpenAi<RawResponse, OneAttributeJsonable> {
    @Autowired
    private OpenAiService openAiService;
    @Override
    public RawResponse getResponse(OneAttributeJsonable keyWordJsonable) {
        String keywords = keyWordJsonable.getValue();
        CreateImageRequest request = CreateImageRequest.builder()
                .model(OpenAiModelEnum.DALL_E_3.getName())
                .quality("standard")
                .size("1024x1024")
                .prompt(createPrompt(keywords))
                .n(1)
                .build();
        String resultImageUrl = openAiService.createImage(request)
                .getData()
                .get(0)
                .getUrl();
        return new RawResponse(resultImageUrl);
    }

    private static final String STYLE_PROMPT = ", black and white line sketches, full sized";

    private String createPrompt(String keywords) {
        return keywords + STYLE_PROMPT;
    };

}
