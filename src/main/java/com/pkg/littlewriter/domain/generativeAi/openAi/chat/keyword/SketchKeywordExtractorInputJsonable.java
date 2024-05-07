package com.pkg.littlewriter.domain.generativeAi.openAi.chat.keyword;

import com.pkg.littlewriter.domain.generativeAi.BookInProgress;
import com.pkg.littlewriter.domain.generativeAi.BookInit;
import com.pkg.littlewriter.domain.generativeAi.Jsonable;
import com.pkg.littlewriter.dto.PageDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.stream.Collectors;

@EqualsAndHashCode(callSuper = true)
@Data
public class SketchKeywordExtractorInputJsonable extends Jsonable {
    private String previousContext;
    private String currentContex;
    private String mainCharacterName;

    public SketchKeywordExtractorInputJsonable (BookInProgress bookInProgress) {
        this.previousContext = bookInProgress.getPreviousPages()
                .stream()
                .map(PageDTO::getContext)
                .collect(Collectors.joining());
        this.currentContex = bookInProgress.getCurrentContext();
        this.mainCharacterName = bookInProgress.getCharacterDTO().getName();
    }

    public SketchKeywordExtractorInputJsonable (BookInit bookInit) {
        this.previousContext = "";
        this.currentContex = bookInit.getCurrentContext();
        this.mainCharacterName = bookInit.getCharacterDTO().getName();
    }
}
