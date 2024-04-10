package com.pkg.littlewriter.domain.generativeAi;

import com.pkg.littlewriter.dto.CharacterDTO;
import com.pkg.littlewriter.dto.PageDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class BookInit {
    private String backgroundInfo;
    private String currentContext;
    private String currentCharacterActionInfo;
    private CharacterDTO characterDTO;
}
