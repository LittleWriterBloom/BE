package com.pkg.littlewriter.domain.generativeAi.stableDiffusion.api.response.fetch;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.pkg.littlewriter.domain.generativeAi.stableDiffusion.api.response.StatusResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@AllArgsConstructor
@NoArgsConstructor
public class FetchProcessingResponse extends StatusResponse {
    private String message;
    private String output;
}
