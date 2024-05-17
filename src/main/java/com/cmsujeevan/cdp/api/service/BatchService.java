package com.cmsujeevan.cdp.api.service;

import com.cmsujeevan.cdp.api.model.dto.BatchRequestDto;
import com.cmsujeevan.cdp.api.model.dto.JobDto;

public interface BatchService {
    JobDto createJob(String jobId, BatchRequestDto batchRequest);

    JobDto getJobStatus(String id);
}
