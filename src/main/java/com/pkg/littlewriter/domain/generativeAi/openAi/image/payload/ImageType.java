package com.pkg.littlewriter.domain.generativeAi.openAi.image.payload;

import lombok.Data;

@Data
public class ImageType extends Content {
    private Img2TextUrl imageUrl;

    public ImageType() {
        super.type = "image_url";
    }
}
