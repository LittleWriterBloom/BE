package com.pkg.littlewriter.domain.generativeAi;

import com.pkg.littlewriter.dto.WordQuestionDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Builder
@AllArgsConstructor
@Data
public class WordQuestionJsonable extends Jsonable{
    private String context;
    private String question;

    public WordQuestionJsonable(WordQuestionDTO wordQuestionDTO) {
        this.context = wordQuestionDTO.getContext();
        this.question = wordQuestionDTO.getQuestion();
    }
}
