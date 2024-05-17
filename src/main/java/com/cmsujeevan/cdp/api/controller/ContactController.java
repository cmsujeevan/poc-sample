package com.cmsujeevan.cdp.api.controller;

import com.cmsujeevan.cdp.api.model.dto.Contact360Dto;
import com.cmsujeevan.cdp.api.model.response.ProfilerErrorMessage;
import com.cmsujeevan.cdp.api.service.CDTContactsAccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/${spring.application.name}/contacts")
public class ContactController {

    private final CDTContactsAccountService cdtContactsAccountService;

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success Response",
                    content = @Content(
                            schema = @Schema(implementation = Contact360Dto.class),
                            mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "Internal System Error",
                    content = @Content(
                            schema = @Schema(implementation = ProfilerErrorMessage.class),
                            mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content(
                            schema = @Schema(implementation = ProfilerErrorMessage.class),
                            mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Not Found",
                    content = @Content(
                            schema = @Schema(implementation = ProfilerErrorMessage.class),
                            mediaType = "application/json"))})
    @Operation(description = "request contact records",
            summary = "")
    @GetMapping(path = "/amp_id/{id}")
    public ResponseEntity<Contact360Dto> getContactsByAmpId(@PathVariable String id) {
        log.info("get contacts by amperity id: {}", id);
            return new ResponseEntity<>(cdtContactsAccountService.getContactsByAmpId(id), HttpStatus.OK);

    }


    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success Response",
                    content = @Content(
                            schema = @Schema(implementation = Contact360Dto.class),
                            mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "Internal System Error",
                    content = @Content(
                            schema = @Schema(implementation = ProfilerErrorMessage.class),
                            mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content(
                            schema = @Schema(implementation = ProfilerErrorMessage.class),
                            mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Not Found",
                    content = @Content(
                            schema = @Schema(implementation = ProfilerErrorMessage.class),
                            mediaType = "application/json"))})
    @Operation(description = "request contact records",
            summary = "")
    @GetMapping(path = "/email_id/{id}")
    public ResponseEntity<Contact360Dto> getContactsByEmailId(@PathVariable String id) {
        log.info("get contacts by email id: {}", id);
        return new ResponseEntity<>(cdtContactsAccountService.getContactsByEmailId(id), HttpStatus.OK);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success Response",
                    content = @Content(
                            schema = @Schema(implementation = Contact360Dto.class),
                            mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "Internal System Error",
                    content = @Content(
                            schema = @Schema(implementation = ProfilerErrorMessage.class),
                            mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content(
                            schema = @Schema(implementation = ProfilerErrorMessage.class),
                            mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Not Found",
                    content = @Content(
                            schema = @Schema(implementation = ProfilerErrorMessage.class),
                            mediaType = "application/json"))})
    @Operation(description = "request contact records",
            summary = "")
    @GetMapping(path = "/okta_user_id/{id}")
    public ResponseEntity<Contact360Dto> getContactsByOktaUserId(@PathVariable String id) {
        log.info("get contacts by okta user id: {}", id);
        return new ResponseEntity<>(cdtContactsAccountService.getContactsByOktaUserId(id), HttpStatus.OK);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success Response",
                    content = @Content(
                            schema = @Schema(implementation = Contact360Dto.class),
                            mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "Internal System Error",
                    content = @Content(
                            schema = @Schema(implementation = ProfilerErrorMessage.class),
                            mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content(
                            schema = @Schema(implementation = ProfilerErrorMessage.class),
                            mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Not Found",
                    content = @Content(
                            schema = @Schema(implementation = ProfilerErrorMessage.class),
                            mediaType = "application/json"))})
    @Operation(description = "request contact records",
            summary = "")
    @GetMapping(path = "/contact_sf_id/{id}")
    public ResponseEntity<Contact360Dto> getContactsByContactSfId(@PathVariable String id) {
        log.info("get contacts by contact salesforce id: {}", id);
        return new ResponseEntity<>(cdtContactsAccountService.getContactsByContactSfId(id), HttpStatus.OK);
    }
}
