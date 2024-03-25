package com.pkg.littlewriter.domain.generativeAi;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Builder
@Data
public class ImageKeywordJsonable extends Jsonable {
    private String keyword;

    public ImageKeywordJsonable(String keyword) {
        this.keyword = keyword;
    }
}
