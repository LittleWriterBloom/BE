package com.pkg.littlewriter.domain.generativeAi.openAi.image;

import com.pkg.littlewriter.domain.generativeAi.Jsonable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@Builder
public class CharacterImageToTextInputJsonable extends Jsonable {
    private String imageUrl;
    private String description;
}
