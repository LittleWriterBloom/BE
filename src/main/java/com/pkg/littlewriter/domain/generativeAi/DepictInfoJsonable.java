package com.pkg.littlewriter.domain.generativeAi;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Builder
@AllArgsConstructor
@Data
public class DepictInfoJsonable extends Jsonable {
    private String currentContext;
    private String characterAppearanceKeyword;
    private String characterDescription;
    private String backgroundInfo;

    public DepictInfoJsonable(BookInit bookInit) {
        this.currentContext = bookInit.getCurrentContext();
        this.characterAppearanceKeyword = bookInit.getCharacterDTO().getAppearanceKeywords();
        this.characterDescription = bookInit.getCharacterDTO().getDescription();
        this.backgroundInfo = bookInit.getBackgroundInfo();
    }

    public DepictInfoJsonable(BookInProgress bookInProgress) {
        this.currentContext = bookInProgress.getCurrentContext();
        this.characterAppearanceKeyword = bookInProgress.getCharacterDTO().getAppearanceKeywords();
        this.characterDescription = bookInProgress.getCharacterDTO().getDescription();
        this.backgroundInfo = bookInProgress.getBackgroundInfo();
    }
}
