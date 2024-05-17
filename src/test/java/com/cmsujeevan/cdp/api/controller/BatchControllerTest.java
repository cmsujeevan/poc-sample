package com.cmsujeevan.cdp.api.controller;


import com.cmsujeevan.cdp.api.model.dto.BatchRequestDto;
import com.cmsujeevan.cdp.api.model.dto.JobDto;
import com.cmsujeevan.cdp.api.service.BatchService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class BatchControllerTest {

    @Mock
    private BatchService batchService;

    @InjectMocks
    private BatchController batchController;

    @Test
    public void testCreateJob() {
        // Mock request attributes
        String jobId = "123";
        BatchRequestDto batchRequest = new BatchRequestDto();
        batchRequest.setDataType("email_engaged");
        batchRequest.setFromDate("2022-01-01");
        batchRequest.setToDate("2022-01-31");

        // Mock the behavior of the service layer
        JobDto jobDto = new JobDto();
        when(batchService.createJob(anyString(), any(BatchRequestDto.class))).thenReturn(jobDto);

        // Call the controller method
        ResponseEntity<JobDto> response = batchController.createJob(jobId, batchRequest);

        // Verify the response
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assert.assertEquals(jobDto, response.getBody());

    }

    @Test
    public void testJobStatus() {
        // Mock job ID
        String jobId = "123";

        // Mock the behavior of the service layer
        JobDto jobDto = new JobDto();
        when(batchService.getJobStatus(anyString())).thenReturn(jobDto);

        // Call the controller method
        ResponseEntity<JobDto> response = batchController.jobStatus(jobId);

        // Verify the response
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assert.assertEquals(jobDto, response.getBody());
    }
}