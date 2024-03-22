package com.pkg.littlewriter.domain.generativeAi;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class Jsonable {
    protected static final ObjectMapper MAPPER = new ObjectMapper();
    public String toJsonString() throws JsonProcessingException {
        return MAPPER.writeValueAsString(this);
    }
}
