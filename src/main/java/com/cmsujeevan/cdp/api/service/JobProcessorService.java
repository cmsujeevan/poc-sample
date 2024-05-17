package com.cmsujeevan.cdp.api.service;

import java.util.concurrent.Future;

public interface JobProcessorService {
    Future<String> createJob(String jobId, String dataType, String fromDate, String toDate);

}
