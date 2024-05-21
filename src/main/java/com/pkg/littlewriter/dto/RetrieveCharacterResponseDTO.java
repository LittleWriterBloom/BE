package com.pkg.littlewriter.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@NoArgsConstructor
@Data
@AllArgsConstructor
public class RetrieveCharacterResponseDTO {
    private List<CharacterDTO> characters;
    private Pagination pageInfo;
}
