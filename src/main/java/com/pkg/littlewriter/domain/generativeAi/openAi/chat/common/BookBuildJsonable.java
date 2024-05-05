package com.pkg.littlewriter.domain.generativeAi.openAi.chat.common;

import com.pkg.littlewriter.domain.generativeAi.BookInProgress;
import com.pkg.littlewriter.domain.generativeAi.BookInit;
import com.pkg.littlewriter.domain.generativeAi.Jsonable;
import com.pkg.littlewriter.dto.PageDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.stream.Collectors;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@AllArgsConstructor
public class BookBuildJsonable extends Jsonable {
    private String previousContext;
    private String currentContext;
    private CharacterNamePersonalityDTO character;

    public BookBuildJsonable(BookInProgress bookInProgress) {
        this.previousContext = bookInProgress.getPreviousPages()
                .stream()
                .map(PageDTO::getContext)
                .collect(Collectors.joining());
        this.currentContext = bookInProgress.getCurrentContext();
        this.character = CharacterNamePersonalityDTO.builder()
                .personality(bookInProgress.getCharacterDTO().getPersonality())
                .name(bookInProgress.getCharacterDTO().getName()).build();
    }

    public BookBuildJsonable(BookBuildDTO bookBuildDTO) {
        this.previousContext = bookBuildDTO.getPreviousContext();
        this.currentContext = bookBuildDTO.getCurrentContext();
        this.character = bookBuildDTO.getCharacter();
    }

    public BookBuildJsonable(BookInit bookInit) {
        this.previousContext = "";
        this.currentContext = bookInit.getCurrentContext();
        this.character = CharacterNamePersonalityDTO.builder()
                .name(bookInit.getCharacterDTO().getName())
                .personality(bookInit.getCharacterDTO().getPersonality()).build();
    }
}
