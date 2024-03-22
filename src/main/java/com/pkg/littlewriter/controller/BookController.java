package com.pkg.littlewriter.controller;

import com.pkg.littlewriter.domain.generativeAi.BookInProgress;
import com.pkg.littlewriter.domain.model.CharacterEntity;
import com.pkg.littlewriter.dto.CharacterDTO;
import com.pkg.littlewriter.dto.PageProgressRequestDTO;
import com.pkg.littlewriter.dto.QuestionAndImageDTO;
import com.pkg.littlewriter.dto.ResponseDTO;
import com.pkg.littlewriter.security.CustomUserDetails;
import com.pkg.littlewriter.service.BookService;
import com.pkg.littlewriter.service.CharacterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/books")
public class BookController {
    @Autowired
    private BookService bookService;
    @Autowired
    private CharacterService characterService;

    @PostMapping("/page-help")
    public ResponseEntity<?> createHelperContents(@AuthenticationPrincipal CustomUserDetails customUserDetails, @RequestBody PageProgressRequestDTO pageProgressRequestDTO) {
        CharacterEntity characterEntity = characterService.getById(pageProgressRequestDTO.getCharacterId());
        CharacterDTO characterDTO = CharacterDTO.builder()
                .name(characterEntity.getName())
                .personality(characterEntity.getPersonality())
                .build();
        BookInProgress bookInProgress = BookInProgress.builder()
                .previousPages(pageProgressRequestDTO.getPreviousPages())
                .backgroundInfo(pageProgressRequestDTO.getBackgroundInfo())
                .currentCharacterActionInfo(pageProgressRequestDTO.getCharacterActionInfo())
                .currentContext(pageProgressRequestDTO.getUserContext())
                .characterDTO(characterDTO)
                .build();
        QuestionAndImageDTO questionAndImageDTO = bookService.generateHelperContents(bookInProgress);
        ResponseDTO<QuestionAndImageDTO> responseDTO = ResponseDTO.<QuestionAndImageDTO>builder()
                .data(List.of(questionAndImageDTO))
                .build();
        return ResponseEntity.ok().body(responseDTO);
    }
}
