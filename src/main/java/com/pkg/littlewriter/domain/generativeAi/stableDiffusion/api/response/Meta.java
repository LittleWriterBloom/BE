package com.pkg.littlewriter.domain.generativeAi.stableDiffusion.api.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@AllArgsConstructor
@NoArgsConstructor
public class Meta {
    private String prompt;
    private String modelId;
    private String controlnetModel;
    private String controlnetType;
    private String negativePrompt;
    private String scheduler;
    private String safetyChecker;
    private String autoHint;
    private String guessMode;
    private float strength;
    private int W;
    private int H;
    private float guidanceScale;
    private String controlnetConditioningScale;
    private Long seed;
    private String useKarrasSigmas;
    private String algorithmType;
    private String safetyCheckerType;
    private String instantResponse;
    private String tomesd;
    private String initImage;
    private String maskImage;
    private String vae;
    private int steps;
    private String fullUrl;
    private String upscale;
    private int nSamples;
    private String embeddings;
    private String lora;
    private String loraStrength;
    private String temp;
    private String base64;
    private int clipSkip;
    private String filePrefix;
}
