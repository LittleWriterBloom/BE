package com.pkg.littlewriter.service;

import com.amazonaws.services.s3.model.ObjectMetadata;
import com.pkg.littlewriter.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;
import java.util.UUID;

@Service
public class S3BucketService {

    @Autowired
    S3BucketUtils s3BucketUtils;

    public S3File copyTo(S3File origin, S3DirectoryEnum destinationDirectory) {
        S3File copyFile = S3File.builder()
                .endPoint(origin.getEndPoint())
                .directories(destinationDirectory.getDirectoryName())
                .fileName(origin.getFileName()).build();
        System.out.println(origin.getKey() + "    " + copyFile.getKey());
        s3BucketUtils.copyFile2(origin.getKey(), copyFile.getKey());
        return copyFile;
    }

    public S3File uploadCharacterImage(MultipartFile file) throws IOException {
        S3File s3File = createRandomNamePngFile(S3DirectoryEnum.CHARACTER);
        s3BucketUtils.uploadToS3Bucket(file, s3File.getDirectories(), s3File.getFileName());
        return s3File;
    }

    public S3File uploadTemporaryFromUrl(String urlString) throws IOException {
        URL url = new URL(urlString);
        byte[] byteArrays = StreamUtils.copyToByteArray(url.openStream());
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType("image");
        metadata.setContentLength(byteArrays.length);
        S3File uploadFile = createRandomNamePngFile(S3DirectoryEnum.TEMPORARY);
        s3BucketUtils.uploadFromByteArrays(byteArrays, uploadFile.getKey());
        return uploadFile;
    }

    private S3File createRandomNamePngFile(S3DirectoryEnum directory) {
        String fileName = UUID.randomUUID() + ".png";
        return S3File.builder()
                .directories(directory.getDirectoryName())
                .fileName(fileName)
                .endPoint(s3BucketUtils.getBucketEndpoint()).build();
    }


    public void deleteFileFromS3(S3File targetFile) {
        s3BucketUtils.deleteFromS3Bucket(targetFile);
    }
}
