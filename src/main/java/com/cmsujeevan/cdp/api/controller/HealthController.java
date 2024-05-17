package com.cmsujeevan.cdp.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/${spring.application.name}")
public class HealthController {

    private final HealthEndpoint healthEndpoint;

    @GetMapping(value = "/health")
    @Operation(summary = "Check API health",
            description = "This API is configured in the container health check path for load balancer calls")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success Response",
                    content = @Content(
                            schema = @Schema(implementation = String.class),
                            mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "Internal System Error",
                    content = @Content(schema = @Schema(implementation = Exception.class),
                            mediaType = "application/json"))})
    public ResponseEntity<Health> health() {

        int mb = 1024 * 1024;
        var runtime = Runtime.getRuntime();
        var usedMemory = (runtime.totalMemory() - runtime.freeMemory()) / mb;
        var freeMemory = runtime.freeMemory() / mb;
        var totalMemory = runtime.totalMemory() / mb;
        var maxMemory = runtime.maxMemory() / mb;
        log.info("HeapUsedMemory: {} HeapFreeMemory: {} HeapTotalMemory:{} HeapMaxMemory {}"
                , usedMemory, freeMemory, totalMemory, maxMemory);

        var healthStatus = healthEndpoint.health().getStatus();

        Health result = Health.status(healthStatus)
                .withDetail("HeapUsedMemory", usedMemory)
                .withDetail("HeapFreeMemory", freeMemory)
                .withDetail("HeapTotalMemory", totalMemory)
                .withDetail("HeapMaxMemory", maxMemory)
                .build();

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

}
