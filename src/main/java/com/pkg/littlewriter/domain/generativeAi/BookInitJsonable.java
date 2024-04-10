package com.pkg.littlewriter.domain.generativeAi;

import com.pkg.littlewriter.dto.BookInitRequestDTO;
import com.pkg.littlewriter.dto.CharacterDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Builder
@AllArgsConstructor
@Data
public class BookInitJsonable extends Jsonable{
    private String backgroundInfo;
    private String currentContext;
    private CharacterJsonable characterJsonable;

    public BookInitJsonable(BookInit bookInit) {
        this.backgroundInfo = bookInit.getBackgroundInfo();
        this.currentContext = bookInit.getCurrentContext();
        this.characterJsonable = new CharacterJsonable(bookInit.getCharacterDTO());
    }
}
