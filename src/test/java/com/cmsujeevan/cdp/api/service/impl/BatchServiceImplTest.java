package com.cmsujeevan.cdp.api.service.impl;

import com.cmsujeevan.cdp.CdpPartnerIntegrationBulkApplication;
import com.cmsujeevan.cdp.api.model.dto.BatchRequestDto;
import com.cmsujeevan.cdp.api.dao.repository.BatchRequestRepository;
import com.cmsujeevan.cdp.api.service.BatchService;
import com.cmsujeevan.cdp.api.service.JobProcessorService;
import com.cmsujeevan.cdp.exception.exceptions.InvalidInputException;
import com.cmsujeevan.cdp.exception.exceptions.JobNotFoundException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;

import java.util.concurrent.CompletableFuture;

import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest(classes = CdpPartnerIntegrationBulkApplication.class)
@TestPropertySource(locations = {"classpath:application-test.properties"})
public class BatchServiceImplTest {

    @Autowired
    private BatchRequestRepository batchRequestRepository;

    @MockBean
    private JobProcessorService jobProcessorService;

    @Autowired
    private BatchService batchService;

    @BeforeEach
    public void initialize() {
        doReturn(CompletableFuture.completedFuture("completed")).when(jobProcessorService)
                .createJob(anyString(), anyString(), anyString(), anyString());
    }

    @Test
    public void testCreateJobSuccess() {
        BatchRequestDto dto = BatchRequestDto.builder()
                .dataType("id_graph")
                .fromDate("2022-09-05")
                .toDate("2022-09-06")
                .build();

        var job = batchService.createJob("job-123", dto);
        var savedJob = batchRequestRepository.findById(job.getJobId());
        Assert.assertEquals(savedJob.get().getJobId(), job.getJobId());
        assertNotNull(batchService.getJobStatus(job.getJobId()));
    }

    @Test
    public void testGetJobStatusJobIdNull() {

        Assertions.assertThrows(InvalidInputException.class
                , () -> batchService.getJobStatus(""));
    }

    @Test
    public void testGetJobStatusJobIdInValid() {

        Assertions.assertThrows(JobNotFoundException.class
                , () -> batchService.getJobStatus("123"));

    }
}