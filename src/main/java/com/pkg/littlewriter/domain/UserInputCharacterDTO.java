package com.pkg.littlewriter.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class UserInputCharacterDTO {
    private String name;
    private String personality;
}
