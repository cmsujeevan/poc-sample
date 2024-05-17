package com.cmsujeevan.cdp.api.service.impl;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.Upload;
import com.cmsujeevan.cdp.CdpPartnerIntegrationBulkApplication;
import com.cmsujeevan.cdp.exception.exceptions.S3Exception;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = CdpPartnerIntegrationBulkApplication.class)
@TestPropertySource(locations = {"classpath:application-test.properties"})
@Slf4j
public class S3ServiceImplTest {

    @Mock
    private AmazonS3 s3Client;

    @Spy
    @InjectMocks
    private S3ServiceImpl s3Service;

    @Test
    public void testIsFolderExists() {
        ListObjectsV2Result result = new ListObjectsV2Result();
        result.setKeyCount(2);
        doReturn(result).when(s3Client).listObjectsV2(anyString(), anyString());

        boolean res = s3Service.isFolderExists("bucket", "folder");
        assertTrue(res);

        doThrow(new SdkClientException("mock exeption")).when(s3Client).listObjectsV2(anyString(), anyString());

        assertThrows(S3Exception.class, () -> s3Service.isFolderExists("bucket", "folder"));
    }

    @Test
    public void testReadListS3Object() {
        ObjectListing objects = Mockito.mock(ObjectListing.class);
        List<S3ObjectSummary> s3ObjectSummaries = new ArrayList<>();
        when(objects.getObjectSummaries()).thenReturn(s3ObjectSummaries);
        when(s3Client.listObjects(isA(ListObjectsRequest.class))).thenReturn(objects);
        when(s3Client.listNextBatchOfObjects(isA(ObjectListing.class))).thenReturn(objects);

        List<String> keys = s3Service.readListS3Objects("sourceBucket", "soureceTest");
        assertNotNull(keys);
    }

    @Test
    public void testUploadToS3() throws InterruptedException {
        TransferManager transferManager = Mockito.mock(TransferManager.class);
        Upload result = Mockito.mock(Upload.class);
        when(transferManager.upload(Mockito.isA(String.class), Mockito.isA(String.class), Mockito.isA(File.class)))
                .thenReturn(result);

        doNothing().when(result).waitForCompletion();

        s3Service.setTransferManager(transferManager);
        s3Service.uploadToS3("destinationBucket", "test", "testpath");
        verify(s3Service, times(1)).uploadToS3("destinationBucket", "test", "testpath");
    }

    @Test
    public void testUploadToS3TestAmazonException() {
        TransferManager transferManager = Mockito.mock(TransferManager.class);
        doThrow(new AmazonClientException("mock exception")).when(transferManager).upload(Mockito.isA(String.class), Mockito.isA(String.class), Mockito.isA(File.class));
        s3Service.setTransferManager(transferManager);

        assertThrows(S3Exception.class, () -> s3Service.uploadToS3("destinationBucket", "test", "testpath"));
    }

    @Test
    public void testGetS3Object() {
        S3Object s3Object = mock(S3Object.class);
        doReturn(s3Object).when(s3Client).getObject(anyString(), anyString());

        assertNotNull(s3Service.getS3Object("bucket", "key"));

        doThrow(new AmazonServiceException("mock exception")).when(s3Client).getObject(anyString(), anyString());
        assertThrows(S3Exception.class, () -> s3Service.getS3Object("bucket", "key"));
    }

    @Test
    public void testGeneratePreSignedUrl() throws MalformedURLException {

        doReturn(new URL("https://bucket/file.txt"))
                .when(s3Client).generatePresignedUrl(isA(GeneratePresignedUrlRequest.class));
        assertNotNull(s3Service.generatePreSignedUrl("bucket", "test/file.txt", 1,
                "hour"));

        doThrow(new SdkClientException("mock exeption"))
                .when(s3Client).generatePresignedUrl(isA(GeneratePresignedUrlRequest.class));
        assertThrows(S3Exception.class, () -> s3Service.generatePreSignedUrl("bucket", "test/file.txt", 1,
                "hour"));
    }

}
