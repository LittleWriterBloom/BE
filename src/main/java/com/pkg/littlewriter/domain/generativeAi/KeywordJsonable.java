package com.pkg.littlewriter.domain.generativeAi;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Builder
@Data
public class KeywordJsonable extends Jsonable {
    private String keyword;

    public KeywordJsonable(String keyword) {
        this.keyword = keyword;
    }
}
