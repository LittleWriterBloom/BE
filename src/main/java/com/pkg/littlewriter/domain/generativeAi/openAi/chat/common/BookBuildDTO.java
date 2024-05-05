package com.pkg.littlewriter.domain.generativeAi.openAi.chat.common;

import com.pkg.littlewriter.domain.generativeAi.BookInProgress;
import com.pkg.littlewriter.domain.generativeAi.BookInit;
import com.pkg.littlewriter.dto.PageDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@Builder
public class BookBuildDTO {
    private String previousContext;
    private String currentContext;
    private CharacterNamePersonalityDTO character;

    public BookBuildDTO(BookInProgress bookInProgress) {
        this.previousContext = bookInProgress.getPreviousPages()
                .stream()
                .map(PageDTO::getContext)
                .collect(Collectors.joining());
        this.currentContext = bookInProgress.getCurrentContext();
        this.character = CharacterNamePersonalityDTO.builder()
                .name(bookInProgress.getCharacterDTO().getName())
                .personality(bookInProgress.getCharacterDTO().getPersonality())
                .build();
    }

    public BookBuildDTO(BookInit bookInit) {
        this.previousContext = "";
        this.currentContext = bookInit.getCurrentContext();
        this.character = CharacterNamePersonalityDTO.builder()
                .name(bookInit.getCharacterDTO().getName())
                .personality(bookInit.getCharacterDTO().getPersonality())
                .build();
    }

}
