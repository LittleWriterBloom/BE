package com.pkg.littlewriter.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class SocialUserPK implements Serializable {
    private String authProvider;
    private Long providedId;
}
