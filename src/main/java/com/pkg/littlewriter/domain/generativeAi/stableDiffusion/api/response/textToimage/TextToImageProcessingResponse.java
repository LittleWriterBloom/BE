package com.pkg.littlewriter.domain.generativeAi.stableDiffusion.api.response.textToimage;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.pkg.littlewriter.domain.generativeAi.stableDiffusion.api.response.Meta;
import com.pkg.littlewriter.domain.generativeAi.stableDiffusion.api.response.StatusResponse;
import lombok.*;

import java.util.List;

//@Data
//@EqualsAndHashCode(callSuper = false)
@Getter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@AllArgsConstructor
@NoArgsConstructor
public class TextToImageProcessingResponse extends StatusResponse {
    private Float eta;
    @JsonProperty("messege")
    private String message;
    private String fetchResult;

    private Long id;
    private List<String> output;
    private List<String> futureLinks;
    private Meta meta;
}

