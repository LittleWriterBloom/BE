package com.pkg.littlewriter.domain.generativeAi.stableDiffusion.api.response;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Response<T extends StatusResponse> {
    private JsonNode jsonNode;
    private boolean isDone;
    private T response;
    private static final ObjectMapper MAPPER = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    public Response(String jsonString, Class<T> responseType) throws JsonProcessingException {
        this.jsonNode = MAPPER.readTree(jsonString);
        this.isDone = jsonNode.get("status").textValue().equals("success");
        this.response = MAPPER.readValue(jsonString, responseType);
    }

    public boolean isDone() {
        return isDone;
    }

    public T getInstance() {
        return response;
    }
}
