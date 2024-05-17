package com.cmsujeevan.cdp.api.service.impl;

import static com.cmsujeevan.cdp.common.constants.Constants.PATH_SLASH;
import static com.cmsujeevan.cdp.common.util.TimeUtil.getTimeInMillis;

import java.io.File;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import com.cmsujeevan.cdp.api.service.S3Service;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import com.amazonaws.services.s3.transfer.Upload;
import com.cmsujeevan.cdp.exception.exceptions.S3Exception;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class S3ServiceImpl implements S3Service {

    private final AmazonS3 s3Client;

    private TransferManager transferManager;

    public S3ServiceImpl(AmazonS3 s3Client) {

        this.s3Client = s3Client;
        this.transferManager = TransferManagerBuilder.standard().withS3Client(s3Client).build();
    }

    @Override
    public boolean isFolderExists(String bucketName, String folderName) {

        try {
            //check folder name ends with /. If not add / end of the folder name
            if (!folderName.endsWith(PATH_SLASH)) {
                folderName = folderName + PATH_SLASH;
            }

            ListObjectsV2Result result = s3Client.listObjectsV2(bucketName, folderName);
            // return the boolean based on comparing if the count exists
            return result.getKeyCount() > 0;
        } catch (Exception e) {
            log.error("exception in S3Service isFolderExists : {}", e.getMessage());
            throw new S3Exception(String.format("exception occurs while checking data type %s location in bucket.", folderName));
        }

    }

    @Override
    public List<String> readListS3Objects(String bucketName, String sourceFolderName) {

        try {
            List<String> keys = new ArrayList<>();
            ListObjectsRequest listObjectsRequest = new ListObjectsRequest().withBucketName(bucketName)
                    .withPrefix(sourceFolderName + "/");

            ObjectListing objects = s3Client.listObjects(listObjectsRequest);

            for (; ; ) {
                List<S3ObjectSummary> summaries = objects.getObjectSummaries();
                if (summaries.isEmpty()) {
                    break;
                }
                // list objects which is on or after the current date, to fetch the latest files
                // object of SimpleDateFormat class  
                for (S3ObjectSummary summ : summaries) {
                    keys.add(summ.getKey());
                }
                // summaries.forEach(s -> keys.add(s.getKey()));
                objects = s3Client.listNextBatchOfObjects(objects);
            }
            return keys;
        } catch (Exception ex) {
            log.error("exception in S3Service readListS3Objects: {}", ex.getMessage());
            throw new S3Exception("exception in list objects from s3 folder.");
        }
    }

    @Override
    public void uploadToS3(String bucketName, String s3FileKeyName, String inputFilePath) {
        try {
            File inputFile = new File(inputFilePath);
            // Upload the file to the destination bucket
            Upload upload = transferManager.upload(bucketName, s3FileKeyName, inputFile);
            upload.waitForCompletion();
        } catch (Exception ex) {
            log.error("Exception S3Service | uploadToS3: {}", ex.getMessage());
            Thread.currentThread().interrupt();
            throw new S3Exception("exception occurred when uploading file %s to s3 bucket.",
                    FilenameUtils.getName(inputFilePath));
        }
    }

    @Override
    public S3Object getS3Object(String bucketName, String objectKey) {
        try {
            return s3Client.getObject(bucketName, objectKey);
        } catch (Exception ex) {
            log.error("Exception S3Service | getS3Object: {}", ex.getMessage());
            throw new S3Exception("exception occurs when get s3 record %s.", FilenameUtils.getName(objectKey));
        }

    }

    @Override
    public String generatePreSignedUrl(String bucket, String s3FilePath,
                                       int preSignedUrlExpiration, String preSignedUrlExpirationTimeUnit) {

        try {
            log.info("request pre-signed url for {} bucket {}, to be expired in {} {}", s3FilePath, bucket,
                    preSignedUrlExpiration, preSignedUrlExpirationTimeUnit);
            // Set the presigned URL to expire after one hour.
            var expiration = new java.util.Date();
            long expTimeMillis = Instant.now().toEpochMilli();

            long expireInMillis = getTimeInMillis(preSignedUrlExpiration, preSignedUrlExpirationTimeUnit);
            expTimeMillis += expireInMillis;

            log.info("set expiration time as {} millis, link will be expired at {} millis", expireInMillis, expTimeMillis);

            expiration.setTime(expTimeMillis);

            var generatedPresignedUrlRequest =
                    new GeneratePresignedUrlRequest(bucket, s3FilePath)
                            .withMethod(HttpMethod.GET)
                            .withExpiration(expiration);
            return s3Client.generatePresignedUrl(generatedPresignedUrlRequest).toString();
        } catch (Exception ex) {
            log.error("Exception in S3Service | generatePreSignedUrl {}: ", ex.getMessage());
            throw new S3Exception("exception occurs when generating pre-signed url for s3 file %s.",
                    FilenameUtils.getName(s3FilePath));
        }
    }

    public void setTransferManager(TransferManager transferManager) {
        this.transferManager = transferManager;
    }
}
