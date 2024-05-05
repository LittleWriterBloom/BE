package com.pkg.littlewriter.domain.generativeAi.openAi.chat.keyword;

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
public class KeyWordExtractorInputJsonable extends Jsonable {
    private String previousContext;
    private String currentContext;
    private String characterAppearanceKeyword;
    private String characterDescription;
    private String whereStoryBegins;

    public KeyWordExtractorInputJsonable(BookInProgress bookInProgress) {
        this.previousContext = bookInProgress.getPreviousPages()
                .stream()
                .map(PageDTO::getContext)
                .collect(Collectors.joining());
        this.currentContext = bookInProgress.getCurrentContext();
        this.characterAppearanceKeyword = bookInProgress.getCharacterDTO().getAppearanceKeywords();
        this.characterDescription = bookInProgress.getCharacterDTO().getDescription();
        this.whereStoryBegins = bookInProgress.getBackgroundInfo();
    }

    public KeyWordExtractorInputJsonable(BookInit bookInProgress) {
        this.previousContext = "";
        this.currentContext = bookInProgress.getCurrentContext();
        this.characterAppearanceKeyword = bookInProgress.getCharacterDTO().getAppearanceKeywords();
        this.characterDescription = bookInProgress.getCharacterDTO().getDescription();
        this.whereStoryBegins = bookInProgress.getBackgroundInfo();
    }
}
