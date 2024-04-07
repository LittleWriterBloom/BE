package com.pkg.littlewriter.utils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
//https://littlewriter.s3.ap-northeast-2.amazonaws.com/testuser1/character/e4729021-acd5-4891-9286-3b7ce1e62ea2.png
@Data
@Builder
@AllArgsConstructor
public class S3File {
    private String endPoint;
    private String directories;
    private String fileName;

    public String getUrl() {
        return endPoint + directories + fileName;
    }

    public String getKey() {
        return directories + fileName;
    }

    public S3File(String urlString) {
        try{
            URL url = new URL(urlString);
            this.endPoint = url.getProtocol() + "://" + url.getHost() + '/';
            String path = url.getPath();
            this.directories = path.substring(1, path.lastIndexOf('/') +1 );
            this.fileName = path.substring(path.lastIndexOf('/') + 1);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("url string not valid");
        }
    }
}
