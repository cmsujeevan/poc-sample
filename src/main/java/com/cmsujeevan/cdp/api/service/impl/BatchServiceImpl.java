package com.cmsujeevan.cdp.api.service.impl;

import com.cmsujeevan.cdp.api.model.dto.JobDto;
import com.cmsujeevan.cdp.api.service.BatchService;
import com.cmsujeevan.cdp.api.service.JobProcessorService;
import com.cmsujeevan.cdp.api.model.dto.BatchRequestDto;
import com.cmsujeevan.cdp.api.dao.entity.BatchRequest;
import com.cmsujeevan.cdp.api.dao.repository.BatchRequestRepository;
import com.cmsujeevan.cdp.common.constants.Constants.JobStatus;
import com.cmsujeevan.cdp.common.util.TimeUtil;
import com.cmsujeevan.cdp.exception.exceptions.InvalidInputException;
import com.cmsujeevan.cdp.exception.exceptions.JobNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import static com.cmsujeevan.cdp.exception.constants.ErrorConstants.EXCEPTION_MSG_JOB_ID_NULL;
import static com.cmsujeevan.cdp.exception.constants.ErrorConstants.EXCEPTION_MSG_JOB_NOT_FOUND;

@Service
@Slf4j
@RequiredArgsConstructor
public class BatchServiceImpl implements BatchService {

    private final BatchRequestRepository batchRequestRepository;
    private final JobProcessorService jobProcessorService;


    /**
     * Save batch request and start the batch job asynchronously
     *
     * @param jobId
     * @param batchRequest
     * @return job id with status
     */
    @Override
    public JobDto createJob(String jobId, BatchRequestDto batchRequest) {

        alterBatchRequest(batchRequest);
        //create a job record in batch_request table
        var job = BatchRequest.builder()
                .jobId(jobId)
                .status(JobStatus.PENDING.getStatus())
                .requestBody(batchRequest.toString())
                .submissionTimestamp(TimeUtil.getCurrentTimestampInTimezone(TimeUtil.DEFAULT_TIME_ZONE))
                .build();

        log.info("job record for batch_request table {}", job);
        batchRequestRepository.save(job);

        //trigger a processor job
        jobProcessorService.createJob(jobId, batchRequest.getDataType(),
                batchRequest.getFromDate(), batchRequest.getToDate());

        return JobDto.builder()
                .jobId(jobId)
                .status(JobStatus.PENDING.getStatus())
                .build();
    }

    /**
     * Get batch request status from database table
     *
     * @param jobId
     * @param
     * @return job status with pre-signed url
     */
    @Override
    public JobDto getJobStatus(String jobId) {

        if (ObjectUtils.isEmpty(jobId)) {
            log.error("given job id is empty");
            throw new InvalidInputException(EXCEPTION_MSG_JOB_ID_NULL);
        }
        final var optional = batchRequestRepository.findById(jobId);
        if (optional.isEmpty()) {
            log.error("job id {} was not found in DB", jobId);
            throw new JobNotFoundException(EXCEPTION_MSG_JOB_NOT_FOUND);
        }

        var batchRequest = optional.get();
        return JobDto.builder()
                .jobId(batchRequest.getJobId())
                .status(batchRequest.getStatus())
                .url(batchRequest.getPreSignedUrl())
                .errorMessage(batchRequest.getError())
                .build();
    }

    private void alterBatchRequest(BatchRequestDto batchRequest) {
        batchRequest.setDataType(batchRequest.getDataType().toLowerCase());
    }

}
