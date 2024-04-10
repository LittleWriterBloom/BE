package com.pkg.littlewriter.domain.generativeAi;

import com.fasterxml.jackson.core.JsonProcessingException;

public interface GenerativeAi {
    GenerativeAiResponse getResponse(Jsonable jsonable) throws JsonProcessingException;
}
