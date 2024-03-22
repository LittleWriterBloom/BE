package com.pkg.littlewriter.domain.generativeAi;

import com.pkg.littlewriter.dto.CharacterDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Builder
@AllArgsConstructor
@Data
public class CharacterJsonable extends Jsonable {
    private String name;
    private String personality;
    public CharacterJsonable(CharacterDTO characterDTO) {
        this.name = characterDTO.getName();
        this.personality = characterDTO.getPersonality();
    }
}
