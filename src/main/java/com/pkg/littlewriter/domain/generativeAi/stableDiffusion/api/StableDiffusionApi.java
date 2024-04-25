package com.pkg.littlewriter.domain.generativeAi.stableDiffusion.api;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pkg.littlewriter.domain.generativeAi.stableDiffusion.api.request.FetchRequestBody;
import com.pkg.littlewriter.domain.generativeAi.stableDiffusion.api.request.ImageToImageRequestBody;
import com.pkg.littlewriter.domain.generativeAi.stableDiffusion.api.request.TextToImageRequestBody;
import com.pkg.littlewriter.domain.generativeAi.stableDiffusion.api.response.*;
import com.pkg.littlewriter.domain.generativeAi.stableDiffusion.api.response.fetch.FetchProcessingResponse;
import com.pkg.littlewriter.domain.generativeAi.stableDiffusion.api.response.fetch.FetchSuccessResponse;
import com.pkg.littlewriter.domain.generativeAi.stableDiffusion.api.response.imageToimage.ImageToImageProcessingResponse;
import com.pkg.littlewriter.domain.generativeAi.stableDiffusion.api.response.imageToimage.ImageToImageSuccessResponse;
import com.pkg.littlewriter.domain.generativeAi.stableDiffusion.api.response.textToimage.TextToImageProcessingResponse;
import com.pkg.littlewriter.domain.generativeAi.stableDiffusion.api.response.textToimage.TextToImageSuccessResponse;


import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class StableDiffusionApi {
    private static final String BASE_URL = "https://modelslab.com";
    private static final String TEXT_TO_IMAGE_ENDPOINT = "/api/v6/images/text2img";
    private static final String CONTROLNET_ENDPOINT = "/api/v5/controlnet";
    private static final String FETCH_ENDPOINT = "/api/v6/images/fetch";
    private static final ObjectMapper MAPPER = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    private final String apiKey;

    public StableDiffusionApi(String apiKey) {
        this.apiKey = apiKey;
    }

    public Response<?> getTextToImageResponse(TextToImageRequestBody textToImageRequestBody) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + TEXT_TO_IMAGE_ENDPOINT))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(MAPPER.writeValueAsString(textToImageRequestBody)))
                .build();
        HttpClient client = HttpClient.newHttpClient();
        String response = client.send(request, HttpResponse.BodyHandlers.ofString()).body();
        JsonNode jsonNode = MAPPER.readTree(response);
        if(jsonNode.get("status").textValue().equals("success")) {
            return new Response<>(response, TextToImageSuccessResponse.class);
        }
        return new Response<>(response, TextToImageProcessingResponse.class);
    }

    public Response<?> getImageToImageResponse(ImageToImageRequestBody imageToImageRequestBody) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + CONTROLNET_ENDPOINT))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(MAPPER.writeValueAsString(imageToImageRequestBody)))
                .build();
        HttpClient client = HttpClient.newHttpClient();
        String response = client.send(request, HttpResponse.BodyHandlers.ofString()).body();
        JsonNode jsonNode = MAPPER.readTree(response);
        if(jsonNode.get("status").textValue().equals("success")) {
            return new Response<>(response, ImageToImageSuccessResponse.class);
        }
        return new Response<>(response, ImageToImageProcessingResponse.class);
    }

    public Response<?> getFetchResponse(FetchRequestBody fetchRequestBody) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + FETCH_ENDPOINT))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(MAPPER.writeValueAsString(fetchRequestBody)))
                .build();
        HttpClient client = HttpClient.newHttpClient();
        String response = client.send(request, HttpResponse.BodyHandlers.ofString()).body();
        JsonNode jsonNode = MAPPER.readTree(response);
        if(jsonNode.get("status").textValue().equals("success")) {
            return new Response<>(response, FetchSuccessResponse.class);
        }
        return new Response<>(response, FetchProcessingResponse.class);
    }

    public String getKey() {
        return apiKey;
    }
}
