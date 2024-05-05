package com.pkg.littlewriter.domain.generativeAi.openAi.chat.common;

import com.pkg.littlewriter.domain.UserInputCharacterDTO;
import com.pkg.littlewriter.domain.generativeAi.Jsonable;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
public class UserInputJsonable extends Jsonable {
    private String previousContext;
    private String currentContext;
    private UserInputCharacterDTO character;
}
