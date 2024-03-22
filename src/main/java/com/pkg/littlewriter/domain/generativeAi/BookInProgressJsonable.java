package com.pkg.littlewriter.domain.generativeAi;

import com.pkg.littlewriter.dto.CharacterDTO;
import com.pkg.littlewriter.dto.PageDTO;
import lombok.*;

import java.util.stream.Collectors;


@EqualsAndHashCode(callSuper = true)
@Builder
@AllArgsConstructor
@Data
public class BookInProgressJsonable extends Jsonable {
    private String backgroundInfo;
    private String previousContext;
    private String currentContext;
    private CharacterJsonable mainCharacter;

    public BookInProgressJsonable(BookInProgress bookInProgress) {
        this.backgroundInfo = bookInProgress.getBackgroundInfo();
        this.previousContext = bookInProgress.getPreviousPages()
                .stream()
                .map(PageDTO::getContext)
                .collect(Collectors.joining());
        this.currentContext = bookInProgress.getCurrentContext();
        this.mainCharacter = new CharacterJsonable(bookInProgress.getCharacterDTO());
    }
}
