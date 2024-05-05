package com.pkg.littlewriter.domain.generativeAi.openAi.chat.contextEricher;

import com.pkg.littlewriter.domain.generativeAi.Jsonable;
import com.pkg.littlewriter.domain.generativeAi.openAi.chat.common.CharacterNamePersonalityDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@AllArgsConstructor
public class ContextEnricherInputJsonable extends Jsonable {
    private String previousContext;
    private String currentContext;
    private String background;
    private CharacterNamePersonalityDTO character;
}
