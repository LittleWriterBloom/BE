package com.pkg.littlewriter.domain.generativeAi.openAi.chat.common;

import com.pkg.littlewriter.dto.CharacterDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class CharacterNamePersonalityDTO {
    private String name;
    private String personality;

    public CharacterNamePersonalityDTO(CharacterDTO characterDTO) {
        this.name = characterDTO.getPersonality();
        this.personality = characterDTO.getPersonality();
    }
}
