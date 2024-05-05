package com.pkg.littlewriter.domain.generativeAi.openAi.chat.dictionary;

import com.pkg.littlewriter.domain.generativeAi.ContextDictionaryInputDTO;
import com.pkg.littlewriter.domain.generativeAi.Jsonable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@Builder
public class ContextDictionaryInputJsonable extends Jsonable {
    private String context;
    private String question;

    public ContextDictionaryInputJsonable(ContextDictionaryInputDTO contextDictionaryInputDTO) {
        this.context = contextDictionaryInputDTO.getContext();
        this.question = contextDictionaryInputDTO.getQuestion();
    }
}
