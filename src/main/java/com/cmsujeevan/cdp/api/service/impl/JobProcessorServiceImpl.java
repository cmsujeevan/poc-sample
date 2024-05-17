package com.cmsujeevan.cdp.api.service.impl;

import com.amazonaws.services.s3.model.S3Object;
import com.cmsujeevan.cdp.api.dao.entity.BatchRequest;
import com.cmsujeevan.cdp.api.dao.repository.BatchRequestRepository;
import com.cmsujeevan.cdp.api.service.JobProcessorService;
import com.cmsujeevan.cdp.api.service.S3Service;
import com.cmsujeevan.cdp.common.util.TimeUtil;
import com.cmsujeevan.cdp.exception.exceptions.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.MDC;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.*;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import static com.cmsujeevan.cdp.common.constants.Constants.*;
import static com.cmsujeevan.cdp.common.util.Util.deleteFile;
import static com.cmsujeevan.cdp.common.util.Util.getFileSize;

@Service
@RequiredArgsConstructor
@Slf4j
public class JobProcessorServiceImpl implements JobProcessorService {

    private final BatchRequestRepository batchRequestRepository;

    private final S3Service s3Service;

    private final String sourceBucket;

    private final String sourceFolder;

    private final String destinationBucket;

    private final int preSignedUrlExpiration;

    private final String preSignedUrlExpirationTimeUnit;


    /**
     * Async method to executes job
     *
     * @param jobId
     * @param dataType
     * @param fromDate
     * @param toDate
     * @return
     */
    @Override
    @Async("JobProcessorThread")
    public Future<String> createJob(String jobId, String dataType, String fromDate, String toDate) {

        MDC.put(JOB_ID, jobId);

        BatchRequest batchRequest = null;
        try {
            log.info("JobProcessorThread - create job thread triggered for jobId-{} " +
                            "dataType-{} fromDate-{}" +
                            " toDate-{}."
                    , jobId, dataType, fromDate, toDate);

            var optional = batchRequestRepository.findById(jobId);
            if (optional.isEmpty()) {
                log.error("JobProcessorThread - job id {} was not found in DB", jobId);
                return completeJob();
            }

            batchRequest = optional.get();
            //set job status: started
            batchRequest.setStatus(JobStatus.STARTED.getStatus());
            batchRequestRepository.save(batchRequest);

            process(batchRequest, dataType, fromDate, toDate);

        } catch (CustomException e) {
            log.error("Exception occurred while creating job {}", e.getMessage());
            if (batchRequest != null) {
                updateDB(batchRequest, JobStatus.FAILED.getStatus(), e.getMessage());
            }
        } catch (Exception e) {
            log.error("Unknown Exception occurred while creating job {}", e.getMessage());
            if (batchRequest != null) {
                updateDB(batchRequest, JobStatus.FAILED.getStatus(), "Unknown Exception occurred while creating job");
            }
        } finally {
            MDC.remove(JOB_ID);
        }

        return completeJob();
    }

    protected void process(BatchRequest batchRequest, String dataType, String fromDate, String toDate) throws Exception {

        var tempDownloadPath = "";

        try {
            var sourceFolder = removeBeginningSlash(this.sourceFolder + PATH_SLASH + dataType);

            log.info("check folder {} in bucket {}", sourceFolder, sourceBucket);
            boolean dataTypeFolderExists = s3Service.isFolderExists(sourceBucket, sourceFolder + PATH_SLASH);

            if (!dataTypeFolderExists) {
                log.error("data type \"{}\" does not found in the source folder {}", dataType, sourceFolder);
                updateDB(batchRequest, JobStatus.DATA_TYPE_NOT_FOUND.getStatus(),
                        String.format("folder not found for data type %s", dataType));
                return;
            }

            List<String> s3Records = s3Service.readListS3Objects(sourceBucket, sourceFolder);
            log.info("{} s3 records found for data type {}.", s3Records.size(), dataType);

            var from = fromDate != null ? LocalDate.parse(fromDate) : null;
            var to = toDate != null ? LocalDate.parse(toDate) : null;

            List<String> s3FilesWithinDates = filterFilesWithinDays(s3Records, sourceFolder, from, to);
            log.info("Filtered file withing from and to dates {}", s3FilesWithinDates);

            if (s3FilesWithinDates.isEmpty()) {
                log.error("No file found created between {} and  {}", fromDate, toDate);
                updateDB(batchRequest, JobStatus.EMPTY_DATA.getStatus(),
                        String.format("No file created for data type %s between %s and %s", dataType, fromDate, toDate));
                return;
            }

            var latestFileS3Key = getLatestFile(s3FilesWithinDates, sourceFolder);

            log.info("latest file {}", latestFileS3Key);
            var fileName = FilenameUtils.getName(latestFileS3Key);

            //Temporary file download path
            tempDownloadPath = getTempDownloadPath(dataType, batchRequest.getJobId(), fileName);

            //Download file to local path(tempDownloadPath)
            downloadFile(sourceBucket, latestFileS3Key, tempDownloadPath);

            //Generate s3 bucket location
            String s3FileUploadPath = generateFileUploadPath(dataType, batchRequest.getJobId(), fileName);

            //Upload downloaded file to S3 bucket
            uploadFileToS3(destinationBucket, s3FileUploadPath, tempDownloadPath);

            //Generate pre signed url
            String preSignedUrl = generatePreSignedS3Url(destinationBucket, s3FileUploadPath);
            batchRequest.setPreSignedUrl(preSignedUrl);

            updateDB(batchRequest, JobStatus.COMPLETED.getStatus(), "");
        } finally {
            if (!tempDownloadPath.isBlank()) {
                deleteLocalFile(tempDownloadPath);
            }
        }
    }


    /**
     * Get recently created file
     *
     * @param files        - list of s3 file path
     * @param sourceFolder - source folder
     * @return
     */
    private String getLatestFile(List<String> files, String sourceFolder) {
        return files.stream().max((o1, o2) -> {
            var date1 = extractDateFromPath(o1, sourceFolder);
            var date2 = extractDateFromPath(o2, sourceFolder);
            return date1.compareTo(date2);
        }).orElseThrow(() -> new CustomException("No latest file"));
    }

    /**
     * Filter only files and files created with from and two days
     *
     * @param s3records    - List of S3 records
     * @param sourceFolder - source folder
     * @param from         - starting date
     * @param to           - end date
     * @return
     */
    private List<String> filterFilesWithinDays(List<String> s3records, String sourceFolder, LocalDate from, LocalDate to) {
        if (from == null && to == null) {
            // get all the files if the dates are empty
            return s3records.stream()
                    .filter(s -> !s.endsWith(PATH_SLASH))
                    .collect(Collectors.toList());
        }
        return s3records.stream()
                .filter(s -> !s.endsWith(PATH_SLASH) && withingDays(s, sourceFolder, from, to))
                .collect(Collectors.toList());
    }

    /**
     * Filter files created withing from and to dates
     *
     * @param filePath - file path
     * @param skip     - source folder
     * @param from     - start date
     * @param to       - end date
     * @return
     */
    private boolean withingDays(String filePath, String skip, LocalDate from, LocalDate to) {
        LocalDate fileDate = extractDateFromPath(filePath, skip);
        return (fileDate.isAfter(from) || fileDate.isEqual(from)) &&
                (fileDate.isBefore(to) || fileDate.isEqual(to));
    }

    /**
     * extract date from the file path
     *
     * @param filePath - file path (folder1/folder2/folder3/file)
     * @param skip     - source folder (folder1)
     * @return
     */
    private LocalDate extractDateFromPath(String filePath, String skip) {
        //remove source folder name from file path; +1 is to remove "/"
        var subPath = filePath.substring(skip.length() + 1);
        String[] splits = subPath.split(PATH_SLASH);
        var year = Integer.parseInt(splits[0]);
        var month = Integer.parseInt(splits[1]);
        var days = Integer.parseInt(splits[2]);

        return LocalDate.of(year, month, days);
    }

    private String removeBeginningSlash(String path) {
        if (path.startsWith(PATH_SLASH)) {
            path = path.substring(1);
        }
        return path;
    }

    private CompletableFuture<String> completeJob() {
        var result = new CompletableFuture<String>();
        result.complete("Complete");
        return result;
    }

    /**
     * update db record
     *
     * @param batchRequest
     * @param status
     * @param message
     */
    private void updateDB(BatchRequest batchRequest, String status, String message) {
        batchRequest.setStatus(status);
        if (message.length() < MAX_ERROR_MSG_LENGTH) {
            batchRequest.setError(message);
        } else {
            batchRequest.setError(message.substring(0, MAX_ERROR_MSG_LENGTH));
        }

        batchRequest.setCompletionTimestamp(TimeUtil.getCurrentTimestampInTimezone(TimeUtil.DEFAULT_TIME_ZONE));
        batchRequestRepository.save(batchRequest);
    }

    /**
     * Upload files to S3 bucket
     *
     * @param destinationBucket - upload bucket name
     * @param s3FileUploadPath  - s3 key path
     * @param inputFilePath     - local file path of the uploading file
     * @throws IOException
     */
    protected void uploadFileToS3(String destinationBucket, String s3FileUploadPath, String inputFilePath) throws IOException {

        try {
            double fileSize = getFileSize(inputFilePath);
            double fileSizeInMb = fileSize / BLOCK_SIZE / BLOCK_SIZE;

            log.info("start uploading {}, file size {} Mb, to the bucket {} as key {}", inputFilePath, fileSizeInMb,
                    destinationBucket, s3FileUploadPath);
            s3Service.uploadToS3(destinationBucket, s3FileUploadPath, inputFilePath);
            log.info("successfully uploaded file");
        } catch (Exception e) {
            log.error("exception occurred while uploading file {}", e.getMessage());
            throw new CustomException("exception in uploading file to s3 path %s", FilenameUtils.getName(inputFilePath));
        }

    }

    /**
     * Generate S3 destination file path
     *
     * @param dataType
     * @param jobId
     * @param fileName
     * @return
     */
    protected String generateFileUploadPath(String dataType, String jobId, String fileName) {

        LocalDate localDate = LocalDate.now();
        // get file extension start from the first .
        String fileExtension = fileName.substring(fileName.indexOf(FILE_EXTENSION_SEPARATOR));
        return dataType + PATH_SLASH + localDate.getYear() + PATH_SLASH + localDate.getMonthValue() + PATH_SLASH
                + localDate.getDayOfMonth() + PATH_SLASH + jobId + fileExtension;
    }


    /**
     * Download file from S3 bucket to local file path
     *
     * @param sourceBucket          - S3 bucket
     * @param s3Key                 - s3 file path
     * @param localDownloadFilePath - local download file path
     */
    protected void downloadFile(String sourceBucket, String s3Key, String localDownloadFilePath) {

        try {
            log.info("start downloading {} to the local path {}", s3Key, localDownloadFilePath);
            S3Object s3object = s3Service.getS3Object(sourceBucket, s3Key);
            //create parent directories for the file
            FileUtils.createParentDirectories(new File(localDownloadFilePath));

            try (InputStream input = new BufferedInputStream(
                    s3object.getObjectContent());
                 OutputStream output = new BufferedOutputStream(new FileOutputStream(localDownloadFilePath))) {

                // At a time, write 10MB buffer
                byte[] buffer = new byte[10 * 1024 * 1024];
                int read;
                while ((read = input.read(buffer)) != -1) {
                    output.write(buffer, 0, read);
                }
            }
            log.info("download complete");
        } catch (Exception ex) {
            log.error("error occurred while downloading file : {}", ex.getMessage());
            throw new CustomException("Exception occurs while downloading file %s.", FilenameUtils.getName(s3Key));
        }
    }

    /**
     * Generate pre-signed url for s3 object
     *
     * @param bucket
     * @param s3FileUploadPath
     * @return
     */
    protected String generatePreSignedS3Url(String bucket, String s3FileUploadPath) {
        log.info("Generate pre-signed url for s3 object {} in bucket {}", bucket, s3FileUploadPath);
        String url = s3Service.generatePreSignedUrl(bucket, s3FileUploadPath,
                preSignedUrlExpiration, preSignedUrlExpirationTimeUnit);
        log.info("pre-signed url {}", url);
        if (url.length() > MAX_URL_LENGTH) {
            throw new CustomException(String.format("Pre-signed url length %d exceeded the Max url length %d",
                    url.length(), MAX_URL_LENGTH));
        }
        return url;
    }

    private String getTempDownloadPath(String dataType, String jobId, String fileName) {
        return TEMP_FOLDER + dataType + PATH_SLASH + jobId + "-" + fileName;
    }

    private void deleteLocalFile(String tempDownloadPath) {
        boolean deleted = deleteFile(tempDownloadPath);

        if (deleted)
            log.info("successfully deleted file {}", tempDownloadPath);
        else
            log.error("file {} deletion unsuccessfully", tempDownloadPath);
    }
}
