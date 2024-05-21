package com.pkg.littlewriter.utils.tts;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NaverTtsRequestBodyDto {
    private String speaker;
    private String text;
    private String volume;
    private String speed;
    private String pitch;

    public String toDataString() {
        return "speaker=" + speaker + "&text=" + text +"&volume=" + volume + "&speed=" + speed + "&pitch=" + pitch;
    }
}
