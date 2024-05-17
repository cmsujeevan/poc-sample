package com.cmsujeevan.cdp.api.controller;


import com.cmsujeevan.cdp.api.dao.entity.ContactAccount360;
import com.cmsujeevan.cdp.api.service.ContactAccount360Service;
import com.cmsujeevan.cdp.api.model.dto.AccountDto;
import com.cmsujeevan.cdp.api.model.response.ErrorMessage;
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
@RequestMapping(value = "/${spring.application.name}/accounts")
public class AccountController {

    private final ContactAccount360Service contactAccount360Service;

    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully Created",
                    content = @Content(
                            schema = @Schema(implementation = ContactAccount360.class),
                            mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "Internal System Error",
                    content = @Content(
                            schema = @Schema(implementation = ErrorMessage.class),
                            mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content(
                            schema = @Schema(implementation = ErrorMessage.class),
                            mediaType = "application/json"))})
    @Operation(description = "Create contact account 360",
            summary = "")
    @PostMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<ContactAccount360> createContactAccount360(@Valid @RequestBody ContactAccount360 contactAccount360) {
        log.info("Create new contactAccount360 for: {}", contactAccount360.getAmpId());
        return new ResponseEntity<>(contactAccount360Service.saveContactAccount360(contactAccount360), HttpStatus.CREATED);

    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully Updated",
                    content = @Content(
                            schema = @Schema(implementation = ContactAccount360.class),
                            mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Record Not Found",
                    content = @Content(
                            schema = @Schema(implementation = ErrorMessage.class),
                            mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "Internal System Error",
                    content = @Content(
                            schema = @Schema(implementation = ErrorMessage.class),
                            mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content(
                            schema = @Schema(implementation = ErrorMessage.class),
                            mediaType = "application/json"))})
    @Operation(description = "Update Contact Account 360",
            summary = "")
    @PutMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity updateContactAccount360(@Valid @RequestBody ContactAccount360 contactAccount360) {
        log.info("Create new contactAccount360 for: {}", contactAccount360.getAmpId());
        return contactAccount360Service.updateContactAccount360(contactAccount360);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success Response",
                    content = @Content(
                            schema = @Schema(implementation = AccountDto.class),
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
                            mediaType = "application/json"))})
    @Operation(description = "request account records",
            summary = "")
    @GetMapping(path = "/amp_id/{id}")
    public ResponseEntity<AccountDto> getContactAccountByAmpId(@PathVariable String id) {
        log.info("get contact account by amperity id: {}", id);
            return new ResponseEntity<>(contactAccount360Service.getContactAccountByAmpId(id), HttpStatus.OK);
    }


    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success Response",
                    content = @Content(
                            schema = @Schema(implementation = AccountDto.class),
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
                            mediaType = "application/json"))})
    @Operation(description = "request account records",
            summary = "")
    @GetMapping(path = "/sys_cust_id/{id}")
    public ResponseEntity<AccountDto> getContactAccountBySyscoCustomerId(@PathVariable String id) {
        log.info("get contact account by account sysco customer id: {}", id);
        return new ResponseEntity<>(contactAccount360Service.getContactAccountBySyscoCustomerId(id), HttpStatus.OK);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success Response",
                    content = @Content(
                            schema = @Schema(implementation = AccountDto.class),
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
                            mediaType = "application/json"))})
    @Operation(description = "request account records",
            summary = "")
    @GetMapping(path = "/contact_sf_id/{id}")
    public ResponseEntity<AccountDto> getContactAccountByContactSfId(@PathVariable String id) {
        log.info("get contacts by contacts salesforce id: {}", id);
        return new ResponseEntity<>(contactAccount360Service.getContactAccountByContactSfId(id), HttpStatus.OK);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success Response",
                    content = @Content(
                            schema = @Schema(implementation = AccountDto.class),
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
                            mediaType = "application/json"))})
    @Operation(description = "request account records",
            summary = "")
    @GetMapping(path = "/account_sf_id/{id}")
    public ResponseEntity<AccountDto> getContactAccountByAccountSfId(@PathVariable String id) {
        log.info("get contacts by account salesforce id: {}", id);
        return new ResponseEntity<>(contactAccount360Service.getContactAccountByAccountSfId(id), HttpStatus.OK);
    }
}
