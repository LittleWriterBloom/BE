package com.pkg.littlewriter.domain.generativeAi.openAi.chat.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RefineContextAndQuestionResponse {
    private String refinedText;
    private List<String> questions;
}
