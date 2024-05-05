package com.pkg.littlewriter.domain.generativeAi.openAi.image.payload;

import lombok.Data;

@Data
public class TextType extends Content {
    private String text;

    public TextType() {
        super.type = "text";
    }
}
