package com.pkg.littlewriter.domain.generativeAi;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ContextDictionaryInputDTO {
    private String context;
    private String question;
}
