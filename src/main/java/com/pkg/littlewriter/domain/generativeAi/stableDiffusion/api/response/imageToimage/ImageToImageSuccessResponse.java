package com.pkg.littlewriter.domain.generativeAi.stableDiffusion.api.response.imageToimage;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.pkg.littlewriter.domain.generativeAi.stableDiffusion.api.response.Meta;
import com.pkg.littlewriter.domain.generativeAi.stableDiffusion.api.response.StatusResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@AllArgsConstructor
@NoArgsConstructor
public class ImageToImageSuccessResponse extends StatusResponse {
    private Float generationTime;
    private Long id;
    private List<String> output;
    private Meta meta;
}
