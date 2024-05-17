package com.cmsujeevan.cdp.api.service;

import java.io.IOException;
import java.util.List;

import com.amazonaws.services.s3.model.S3Object;

public interface S3Service {

    /**
     * Check folder is exists inside bucket
     *
     * @param bucketName
     * @param folderName
     * @return
     */
    boolean isFolderExists(String bucketName, String folderName);

    List<String> readListS3Objects(String bucketName, String sourceFolderName);

    /**
     * @param inputFilePath
     * @param s3FileKeyName
     * @throws IOException
     */
    void uploadToS3(String bucketName, String s3FileKeyName, String inputFilePath);

    /**
     * get file from S3 source bucket
     *
     * @param objectKey
     */
    S3Object getS3Object(String bucketName, String objectKey);

    /**
     * generate pre signed url for s3 object
     *
     * @param bucket                         - bucket name
     * @param s3FilePath                     - bucket path of the file
     * @param preSignedUrlExpiration         - duration signed url is valid
     * @param preSignedUrlExpirationTimeUnit - time unit of duration
     * @return
     */
    String generatePreSignedUrl(String bucket, String s3FilePath, int preSignedUrlExpiration, String preSignedUrlExpirationTimeUnit);
}
