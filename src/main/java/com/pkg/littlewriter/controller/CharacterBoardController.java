package com.pkg.littlewriter.controller;

import com.pkg.littlewriter.domain.model.CharacterEntity;
import com.pkg.littlewriter.dto.CharacterDTO;
import com.pkg.littlewriter.dto.ResponseDTO;
import com.pkg.littlewriter.service.CharacterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.List;

@Controller
@RequestMapping("/character/board")
public class CharacterBoardController {
    @Autowired
    CharacterService characterService;

    @GetMapping("/{characterId}")
    public ResponseEntity<?> getCharacter(@PathVariable Long characterId) {
        CharacterEntity characterEntity = characterService.getById(characterId);
        CharacterDTO characterDTO = new CharacterDTO(characterEntity);
        ResponseDTO<CharacterDTO> responseDTO = ResponseDTO.<CharacterDTO>builder()
                .data(List.of(characterDTO))
                .build();
        return ResponseEntity.ok().body(responseDTO);
    }
}
