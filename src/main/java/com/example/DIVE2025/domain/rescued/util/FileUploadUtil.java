package com.example.DIVE2025.domain.rescued.util;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.io.ByteArrayInputStream;

@Slf4j
@Component
public class FileUploadUtil {

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucket;
    private final AmazonS3Client amazonS3Client;

    @Autowired
    public FileUploadUtil(AmazonS3Client amazonS3Client) {
        this.amazonS3Client = amazonS3Client;
    }

    public void uploadImageFromUrl(String rawUrl, String shelterId, String desertionNo) {

        try (CloseableHttpClient client = HttpClients.createDefault()) {

            String encodedUrl = rawUrl.replace("[", "%5B").replace("]", "%5D");
            HttpGet httpGet = new HttpGet(encodedUrl);
            httpGet.setHeader("User-Agent", "Mozilla/5.0");

            ClassicHttpResponse response = client.executeOpen(null, httpGet, null);

            byte[] imageBytes = EntityUtils.toByteArray(response.getEntity());

            if (imageBytes == null || imageBytes.length == 0) {
                log.error("image is null");
                return;
            }

            String key = "shelter" + shelterId + "/" + desertionNo + ".jpg";
            ByteArrayInputStream inputStream = new ByteArrayInputStream(imageBytes);

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(imageBytes.length);
            metadata.setContentType("image/jpeg");

            amazonS3Client.putObject(new PutObjectRequest(bucket, key, inputStream, metadata));
            log.info( desertionNo+ " upload success");

        } catch (Exception e) {
            log.error(desertionNo + " upload success fail");
        }
    }
}
