package com.pkg.littlewriter.domain.generativeAi.async;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ContextAndQuestion {
    private String refinedText;
    private List<String> questions;
}
