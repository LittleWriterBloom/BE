package com.pkg.littlewriter.domain;

import com.pkg.littlewriter.domain.generativeAi.Jsonable;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserInputJsonable extends Jsonable {
    private String previousContext;
    private String currentContext;
    private UserInputCharacterDTO character;
}
