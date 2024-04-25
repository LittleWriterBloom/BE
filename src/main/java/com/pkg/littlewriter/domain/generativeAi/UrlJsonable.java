package com.pkg.littlewriter.domain.generativeAi;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Builder
@Data
public class UrlJsonable extends Jsonable {
    private final String url;

    public UrlJsonable(String url) {
        this.url = url;
    }
}
