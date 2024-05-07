package com.pkg.littlewriter.dto;

import com.pkg.littlewriter.domain.model.CharacterEntity;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CharacterDTO {
    private Long id;
    private String name;
    private String personality;
    private String imageUrl;
    private String originImageUrl;
    private String description;
    private String appearanceKeywords;

    public CharacterDTO(CharacterEntity characterEntity) {
        this.id = characterEntity.getId();
        this.name = characterEntity.getName();
        this.personality = characterEntity.getPersonality();
        this.imageUrl = characterEntity.getImageUrl();
        this.originImageUrl = characterEntity.getOriginImageUrl();
        this.appearanceKeywords = characterEntity.getAppearanceKeywords();
        this.description = characterEntity.getUserDescription();
    }
}
