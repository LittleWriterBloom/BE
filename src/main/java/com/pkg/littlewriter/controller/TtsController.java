package com.pkg.littlewriter.controller;

import com.pkg.littlewriter.dto.ResponseDTO;
import com.pkg.littlewriter.utils.tts.NaverTtsRequestBodyDto;
import com.pkg.littlewriter.utils.tts.NaverTtsResponseDto;
import com.pkg.littlewriter.utils.tts.NaverTtsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/tts")
public class TtsController {
    @Autowired
    private NaverTtsService tts;

    @PostMapping
    public ResponseEntity<?> getResponse(@RequestBody NaverTtsRequestBodyDto naverTtsRequestBodyDto) throws IOException, InterruptedException {
        NaverTtsResponseDto response = tts.toVoice(naverTtsRequestBodyDto);
        if(response.getCode() == 200) {
            MediaType.parseMediaType("audio/mpeg");
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType("audio/mpeg"))
                    .body(response.getMp3Binary());
        }
        return ResponseEntity.ok().body(response.getMessage());
    }
}
