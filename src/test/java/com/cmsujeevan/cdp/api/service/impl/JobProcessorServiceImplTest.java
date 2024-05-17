package com.cmsujeevan.cdp.api.service.impl;


import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.cmsujeevan.cdp.CdpPartnerIntegrationBulkApplication;
import com.cmsujeevan.cdp.api.dao.entity.BatchRequest;
import com.cmsujeevan.cdp.api.dao.repository.BatchRequestRepository;
import com.cmsujeevan.cdp.api.service.S3Service;
import com.cmsujeevan.cdp.common.util.TimeUtil;
import com.cmsujeevan.cdp.exception.exceptions.CustomException;
import com.cmsujeevan.cdp.exception.exceptions.S3Exception;
import com.cmsujeevan.cdp.common.constants.Constants;
import com.cmsujeevan.cdp.common.util.Util;
import org.apache.commons.io.FileUtils;
import org.apache.http.client.methods.HttpRequestBase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import static com.cmsujeevan.cdp.common.constants.Constants.PATH_SLASH;
import static com.cmsujeevan.cdp.common.constants.Constants.TEMP_FOLDER;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest(classes = CdpPartnerIntegrationBulkApplication.class)
@TestPropertySource(locations = {"classpath:application-test.properties"})
public class JobProcessorServiceImplTest {

    @Autowired
    private BatchRequestRepository batchRequestRepository;

    @Mock
    private S3Service s3Service;

    @Autowired
    private String sourceBucket;

    @Autowired
    private String sourceFolder;

    @Autowired
    private String destinationBucket;

    @Autowired
    private int preSignedUrlExpiration;

    @Autowired
    private String preSignedUrlExpirationTimeUnit;


    private JobProcessorServiceImpl jobProcessorService;

    @BeforeEach
    public void initialize() {

        jobProcessorService = spy(new JobProcessorServiceImpl(
                batchRequestRepository,
                s3Service,
                sourceBucket,
                sourceFolder,
                destinationBucket,
                preSignedUrlExpiration,
                preSignedUrlExpirationTimeUnit
        ));

    }


    @Test
    public void testCreateJobSuccess() throws Exception {

        var jobId = System.currentTimeMillis() + "_" + UUID.randomUUID();
        var jobEntity = BatchRequest.builder()
                .jobId(jobId)
                .status(Constants.JobStatus.PENDING.getStatus())
                .requestBody("")
                .submissionTimestamp(TimeUtil.getCurrentTimestampInTimezone
                        (TimeUtil.DEFAULT_TIME_ZONE))
                .build();
        var job = batchRequestRepository.save(jobEntity);

        String dataType = "id_graph";
        String fromDate = "2022-09-04";
        String toDate = "2022-09-05";

        doNothing().when(jobProcessorService).process(job, dataType, fromDate, toDate);

        var future = jobProcessorService
                .createJob(jobId, dataType, fromDate, toDate);
        //wait till complete
        future.get();

        assertEquals(Constants.JobStatus.STARTED.getStatus()
                , batchRequestRepository.findById(jobId).get().getStatus());

    }

    @Test
    public void testCreateJobIdNotFound() throws ExecutionException, InterruptedException {

        String dataType = "id_graph";
        String fromDate = "2022-09-04";
        String toDate = "2022-09-05";
        var future = jobProcessorService
                .createJob("1234", dataType, fromDate, toDate);
        //wait till complete
        future.get();
        verify(jobProcessorService, times(1)).createJob("1234", dataType, fromDate, toDate);

    }

    @Test
    public void testCreateJobFailed() throws Exception {

        var jobId = System.currentTimeMillis() + "_" + UUID.randomUUID();
        var jobEntity = BatchRequest.builder()
                .jobId(jobId)
                .status(Constants.JobStatus.PENDING.getStatus())
                .requestBody("")
                .submissionTimestamp(TimeUtil.getCurrentTimestampInTimezone
                        (TimeUtil.DEFAULT_TIME_ZONE))
                .build();
        var job = batchRequestRepository.save(jobEntity);

        String dataType = "id_graph";
        String fromDate = "2022-09-04";
        String toDate = "2022-09-05";

        //test exception
        doThrow(new Exception("mock exception"))
                .when(jobProcessorService).process(job, dataType, fromDate, toDate);

        var future = jobProcessorService
                .createJob(jobId, dataType, fromDate, toDate);
        //wait till complete
        future.get();
        assertEquals(Constants.JobStatus.FAILED.getStatus()
                , batchRequestRepository.findById(jobId).get().getStatus());


        doThrow(new Exception("Lorem ipsum dolor sit amet, consectetuer adipiscing elit. " +
                "Aenean commodo ligula eget dolor. Aenean massa. Cum sociis natoque penatibus et " +
                "magnis dis parturient montes, nascetur ridiculus mus. Donec quam felis, " +
                "ultricies nec, pellentesque eu, pretium quis, sem. Nulla consequat massa quis enim. " +
                "Donec pede justo, fringilla vel, aliquet nec, vulputate eget, arcu. In enim justo, " +
                "rhoncus ut, imperdiet a,"))
                .when(jobProcessorService).process(job, dataType, fromDate, toDate);

        future = jobProcessorService
                .createJob(jobId, dataType, fromDate, toDate);
        //wait till complete
        future.get();
        assertEquals(Constants.JobStatus.FAILED.getStatus()
                , batchRequestRepository.findById(jobId).get().getStatus());

    }

    @Test
    public void testProcessDataTypeFolderNotFound() throws Exception {

        var job = getBatchRequest();
        String dataType = "id_graph";
        String fromDate = "2022-09-06";
        String toDate = "2022-09-10";

        doReturn(false).when(s3Service).isFolderExists(anyString(), anyString());

        jobProcessorService.process(job, dataType, fromDate, toDate);

        assertEquals(job.getStatus(), Constants.JobStatus.DATA_TYPE_NOT_FOUND.getStatus());
    }

    @Test
    public void testProcessWithEmptyData() throws Exception {

        var job = getBatchRequest();

        String dataType = "id_graph";
        String fromDate = "2022-09-06";
        String toDate = "2022-09-10";

        doReturn(true).when(s3Service).isFolderExists(anyString(), anyString());
        doReturn(Arrays.asList(
                "test/id_graph/2022/",
                "test/id_graph/2022/9/5/",
                "test/id_graph/2022/9/5/id_graph-2022-09-05.gz",
                "test/id_graph/2022/9/11/id_graph-2022-09-11.gz"
        ))
                .when(s3Service).readListS3Objects(anyString(), anyString());

        jobProcessorService.process(job, dataType, fromDate, toDate);

        assertEquals(job.getStatus(), Constants.JobStatus.EMPTY_DATA.getStatus());

    }

    @Test
    public void testProcessSuccess() throws Exception {

        var job = getBatchRequest();

        String dataType = "id_graph";
        String fromDate = "2022-09-08";
        String toDate = "2022-09-10";

        doReturn(true).when(s3Service).isFolderExists(anyString(), anyString());
        doReturn(Arrays.asList(
                "test/id_graph/2022/",
                "test/id_graph/2022/9/9/",
                "test/id_graph/2022/9/7/id_graph-2022-09-07.gz",
                "test/id_graph/2022/9/8/id_graph-2022-09-08.gz",
                "test/id_graph/2022/9/9/id_graph-2022-09-09.gz",
                "test/id_graph/2022/9/10/id_graph-2022-09-10.gz"
        )).when(s3Service).readListS3Objects(anyString(), anyString());

        var tempDownloadPath = TEMP_FOLDER + dataType + PATH_SLASH + job.getJobId() + "-" + "id_graph-2022-09-10.gz";
        doNothing().when(jobProcessorService)
                .downloadFile(sourceBucket, sourceFolder + "/id_graph/2022/9/10/id_graph-2022-09-10.gz"
                        , tempDownloadPath);

        String s3FileUploadPath = jobProcessorService.generateFileUploadPath(dataType, job.getJobId(),
                "id_graph-2022-09-10.gz");

        doNothing().when(jobProcessorService).uploadFileToS3(destinationBucket, s3FileUploadPath, tempDownloadPath);

        doReturn("https://s3-bucket/id_graph/2022/9/10/id_graph-2022-09-10.gz/")
                .when(s3Service).generatePreSignedUrl(anyString(), anyString(), anyInt(), anyString());

        jobProcessorService.process(job, dataType, fromDate, toDate);

        assertEquals(job.getStatus(), Constants.JobStatus.COMPLETED.getStatus());


    }

    private BatchRequest getBatchRequest() {
        var jobId = System.currentTimeMillis() + "_" + UUID.randomUUID();
        var jobEntity = BatchRequest.builder()
                .jobId(jobId)
                .status(Constants.JobStatus.PENDING.getStatus())
                .requestBody("")
                .submissionTimestamp(TimeUtil.getCurrentTimestampInTimezone
                        (TimeUtil.DEFAULT_TIME_ZONE))
                .build();
        return batchRequestRepository.save(jobEntity);
    }

    @Test
    public void testDownloadFile() throws IOException {

        String s3Key = sourceFolder + "/id_graph/2022/9/11/id_graph-2022-09-11.gz";
        String localDownloadFilePath = "/tmp/jobId/file.txt";

        writeMockDataFile("file.txt", "mock data");
        S3Object s3Object = mock(S3Object.class);
        try (S3ObjectInputStream s3ObjectInputStream = new S3ObjectInputStream(
                new FileInputStream("file.txt"), new HttpRequestBase() {
            @Override
            public String getMethod() {
                return "get";
            }
        })) {
            doReturn(s3ObjectInputStream).when(s3Object).getObjectContent();
            doReturn(s3Object).when(s3Service).getS3Object(anyString(), anyString());
            jobProcessorService.downloadFile(sourceBucket, s3Key, localDownloadFilePath);

            FileUtils.deleteQuietly(new File("file.txt"));
        }
    }

    @Test
    public void testDownloadFileException() {

        String s3Key = sourceFolder + "/id_graph/2022/9/11/id_graph-2022-09-11.gz";
        String localDownloadFilePath = "/tmp/jobId/file.txt";

        Mockito.doThrow(new S3Exception("mock exception")).when(s3Service).getS3Object(anyString(), anyString());

        Assertions.assertThrows(CustomException.class
                , () -> jobProcessorService.downloadFile(sourceBucket, s3Key, localDownloadFilePath));

    }

    @Test
    public void uploadFileToS3() {

        String s3Path = "s3/file.txt";
        String localFilePath = "/tmp/file.txt";
        try (MockedStatic<Util> mockUtil = Mockito.mockStatic(Util.class)) {

            mockUtil.when(() -> Util.getFileSize(anyString()))
                    .thenReturn(1024L * 1024L)
                    .thenCallRealMethod();

            doNothing().when(s3Service).uploadToS3(anyString(), anyString(), anyString());
            jobProcessorService.uploadFileToS3(destinationBucket, s3Path, localFilePath);
            verify(jobProcessorService, times(1)).uploadFileToS3(destinationBucket, s3Path,
                    localFilePath);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void writeMockDataFile(String path, String data) {

        try {
            Files.write(Paths.get(path), data.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGeneratePreSignedS3Url() {
        doReturn("https://s3-bucket/id_graph/2022/9/10/id_graph-2022-09-10.gz/")
                .when(s3Service).generatePreSignedUrl(anyString(), anyString(), anyInt(), anyString());
        assertNotNull(jobProcessorService.generatePreSignedS3Url("bucket", "path"));
    }
}

