package com.sj.Petory.common.s3;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.sj.Petory.exception.S3Exception;
import com.sj.Petory.exception.type.ErrorCode;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static com.sj.Petory.exception.type.ErrorCode.IMAGE_UPLOAD_FAIL;

@RequiredArgsConstructor
@Slf4j
@Service
public class AmazonS3Service {
    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucketName}")
    private String bucketName;

    public String uploadImageforKakao(String imageUrl) {

        if (StringUtils.isEmpty(imageUrl)) {
            return "";
        }
        String randomFilename = generateRandomFilename(imageUrl);
        try {
            InputStream inputStream = new URL(imageUrl).openStream();

            ObjectMetadata metaData = new ObjectMetadata();
            metaData.setContentType("image/jpeg");

            amazonS3.putObject(bucketName, randomFilename, inputStream, metaData);

            return amazonS3.getUrl(bucketName, randomFilename).toString();

        } catch (Exception e) {
            throw new S3Exception(IMAGE_UPLOAD_FAIL);
        }
    }

    public String upload(MultipartFile image) {
        if (image.isEmpty() || Objects.isNull(image.getOriginalFilename())) {
            throw new S3Exception(ErrorCode.FILE_EMPTY);
        }
        return this.uploadImage(image);
    }

    private String uploadImage(MultipartFile image) {
        String randomFilename = generateRandomFilename(image.getOriginalFilename());

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(image.getSize());
        metadata.setContentType(image.getContentType());

        log.info("File Upload Started : " + randomFilename);
        try {
            amazonS3.putObject(bucketName, randomFilename, image.getInputStream(), metadata);
        } catch (AmazonS3Exception e) {
            log.error("Amazon S3 error while uploading file: " + e.getMessage());
            throw new S3Exception(IMAGE_UPLOAD_FAIL);
        } catch (SdkClientException e) {
            log.error("AWS SDK client error while uploading file: " + e.getMessage());
            throw new S3Exception(IMAGE_UPLOAD_FAIL);
        } catch (IOException e) {
            log.error("IO error while uploading file: " + e.getMessage());
            throw new S3Exception(IMAGE_UPLOAD_FAIL);
        }

        log.info("File upload Success : " + randomFilename);

        return amazonS3.getUrl(bucketName, randomFilename).toString();
    }

    private String generateRandomFilename(String originName) {
        String fileExtension = validateFileExtension(originName);

        return UUID.randomUUID() + "." + fileExtension;
    }

    private String validateFileExtension(String originName) {
        String fileExtension = originName.substring(originName.lastIndexOf(".") + 1);
        log.info(fileExtension);
        List<String> allowedExtensions = Arrays.asList("jpg", "png", "gif", "jpeg");

        if (!allowedExtensions.contains(fileExtension)) {
            throw new S3Exception(ErrorCode.FILE_EXTENSION_NOT_ALLOWED);
        }
        return fileExtension;
    }
}
