package com.pkg.littlewriter.utils.tts;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NaverTtsResponseDto {
    private int code;
    private byte[] mp3Binary;
    private String message;
}
