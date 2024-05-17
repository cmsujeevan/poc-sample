package com.cmsujeevan.cdp.exception.handler;

import com.cmsujeevan.cdp.exception.AppExceptionHandler;
import com.cmsujeevan.cdp.exception.exceptions.CustomException;
import com.cmsujeevan.cdp.exception.exceptions.InvalidInputException;
import com.cmsujeevan.cdp.exception.exceptions.JobNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertAll;

public class AppExceptionHandlerTest {

    private AppExceptionHandler appExceptionHandler;

    @BeforeEach
    void setup() {
        appExceptionHandler = new AppExceptionHandler();
    }

    @Test
    void testHandleMethodArgumentNotValid(){

    }

    @Test
    void testCustomExceptionHandler() {
        assertAll(() -> appExceptionHandler.handleCustomException(new CustomException((""))));
    }

    @Test
    void testInvalidInputExceptionHandler() {
        assertAll(() -> appExceptionHandler.handleInvalidInputException(new InvalidInputException((""))));
    }

    @Test
    void testJobNotFoundExceptionHandler() {
        assertAll(() -> appExceptionHandler.handleJobNotFoundException(new JobNotFoundException((""))));
    }

    @Test
    void testGeneralExceptionHandler() {
        assertAll(() -> appExceptionHandler.generalExceptionHandler(new IOException((""))));
    }
}
