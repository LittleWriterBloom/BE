package com.pkg.littlewriter.domain.generativeAi.openAi;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pkg.littlewriter.domain.generativeAi.Jsonable;

public interface OpenAi<T ,E extends Jsonable> {
    ObjectMapper defaultMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    T getResponse(E jsonable) throws OpenAiException;
}
