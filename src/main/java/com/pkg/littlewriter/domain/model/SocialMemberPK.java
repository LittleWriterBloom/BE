package com.pkg.littlewriter.domain.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class SocialMemberPK implements Serializable {
    private String authProvider;
    private Long providedId;
}
