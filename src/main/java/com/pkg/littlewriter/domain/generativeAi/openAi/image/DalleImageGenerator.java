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
public class DalleImageGenerator implements OpenAi<RawResponse, OneAttributeJsonable> {
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

    private static final String STYLE_PROMPT = "This should be a full-sized, children's picture book illustration style, boasting pure and vibrant colors. Maintain a flat 2D look with simple shading with no outlines, emphasizing the sweet innocence and enchantment of the scene. And no seperated image";

    private String createPrompt(String keywords) {
        return keywords + STYLE_PROMPT;
    };
}
