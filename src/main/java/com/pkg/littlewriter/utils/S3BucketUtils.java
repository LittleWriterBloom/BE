package com.pkg.littlewriter.utils;

import com.amazonaws.AmazonClientException;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.util.ByteArrayBuffer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.web.multipart.MultipartFile;
import retrofit2.http.Url;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

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

    public void uploadToS3BucketFromUrl(String urlString, String uploadName) throws IOException {
        URL url = new URL(urlString);
        byte[] byteArrays = StreamUtils.copyToByteArray(url.openStream());
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType("image");
        metadata.setContentLength(byteArrays.length);
        amazonS3Client.putObject(bucketName, uploadName, new ByteArrayInputStream(byteArrays), metadata);
    }

    public void copyFile(String sourceFilePath, String uploadName) {
        String fileName = getFileNameFromUrl(sourceFilePath);
        amazonS3Client.copyObject(bucketName, fileName, bucketName, uploadName);
    }

    private String getFileNameFromUrl(String fullFilePathUrl) {
        return fullFilePathUrl.replace(baseUrl, "");
    }

    public String getBucketEndpoint() {
        return baseUrl;
    }
}
