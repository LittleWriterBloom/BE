package com.pkg.littlewriter.domain.generativeAi.stableDiffusion.api.response.fetch;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.pkg.littlewriter.domain.generativeAi.stableDiffusion.api.response.StatusResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@AllArgsConstructor
@NoArgsConstructor
public class FetchSuccessResponse extends StatusResponse {
    private Long id;
    private List<String> output;
}
