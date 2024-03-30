package com.pkg.littlewriter.utils;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class S3File {
    private String endPoint;
    private String directories;
    private String fileName;
}
