package com.pkg.littlewriter.domain.generativeAi.async;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ContextAndQuestion {
    private String generatedContext;
    private List<String> generatedQuestions;
}
