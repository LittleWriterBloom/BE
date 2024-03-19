package com.pkg.littlewriter.utils;

import com.amazonaws.AmazonClientException;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class S3BucketUtils {
    @Autowired
    private AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;
    @Value("${cloud.aws.s3.baseUrl}")
    private String baseUrl;
    public void uploadToS3Bucket(MultipartFile image, String uploadName) throws IOException {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(image.getContentType());
        metadata.setContentLength(image.getSize());
        amazonS3Client.putObject(bucketName,uploadName,image.getInputStream(),metadata);
    }

    public void deleteFromS3Bucket(String fileName) {
        try{
            amazonS3Client.deleteObject(bucketName, fileName);
        } catch (AmazonClientException e) {
            log.error("failed to delete {} from bucket", fileName);
        }
    }

    public String getBucketEndpoint() {
        return baseUrl;
    }
}
