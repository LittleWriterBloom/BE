package com.pkg.littlewriter.domain.generativeAi;

import com.pkg.littlewriter.dto.CharacterDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class BookInit {
    private String backgroundInfo;
    private String currentContext;
    private String currentCharacterActionInfo;
    private CharacterDTO characterDTO;
    private int storyLength;
}
