package com.cmsujeevan.cdp.api.controller;

import com.cmsujeevan.cdp.api.model.dto.BatchRequestDto;
import com.cmsujeevan.cdp.api.model.response.ErrorMessage;
import com.cmsujeevan.cdp.api.model.dto.JobDto;
import com.cmsujeevan.cdp.api.service.BatchService;
import com.cmsujeevan.cdp.common.constants.Constants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/${spring.application.name}/" + Constants.BATCH_API_PREFIX)
public class BatchController {

    private final BatchService batchService;

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success Response",
                    content = @Content(
                            schema = @Schema(implementation = JobDto.class),
                            mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "Internal System Error",
                    content = @Content(
                            schema = @Schema(implementation = ErrorMessage.class),
                            mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content(
                            schema = @Schema(implementation = ErrorMessage.class),
                            mediaType = "application/json"))})
    @Operation(description = "submit a job request",
            summary = "")
    @PostMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<JobDto> createJob(@RequestAttribute("job_id") String jobId, @Valid @RequestBody BatchRequestDto batchRequest) {
        log.info("create job request request id : {} body:  {}", jobId, batchRequest);
        return new ResponseEntity<>(batchService.createJob(jobId, batchRequest), HttpStatus.OK);
    }


    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success Response",
                    content = @Content(
                            schema = @Schema(implementation = JobDto.class),
                            mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "Internal System Error",
                    content = @Content(
                            schema = @Schema(implementation = ErrorMessage.class),
                            mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content(
                            schema = @Schema(implementation = ErrorMessage.class),
                            mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Not Found",
                    content = @Content(
                            schema = @Schema(implementation = ErrorMessage.class),
                            mediaType = "application/json"))
    })
    @Operation(description = "check job status",
            summary = "")
    @GetMapping(path = "/status/{id}")
    public ResponseEntity<JobDto> jobStatus(@PathVariable String id) {
        log.info("checking job status for job: {}", id);
        return new ResponseEntity<>(batchService.getJobStatus(id), HttpStatus.OK);
    }
}
