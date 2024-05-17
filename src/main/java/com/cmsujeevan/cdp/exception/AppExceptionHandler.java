package com.cmsujeevan.cdp.exception;

import com.cmsujeevan.cdp.api.model.response.ErrorMessage;
import com.cmsujeevan.cdp.api.model.response.ProfilerErrorMessage;
import com.cmsujeevan.cdp.exception.constants.ErrorCode;
import com.cmsujeevan.cdp.exception.constants.ErrorConstants;
import com.cmsujeevan.cdp.exception.exceptions.CustomException;
import com.cmsujeevan.cdp.exception.exceptions.InvalidInputException;
import com.cmsujeevan.cdp.exception.exceptions.JobNotFoundException;
import com.cmsujeevan.cdp.exception.exceptions.RecordsNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;
import java.util.UUID;

@Slf4j
@ControllerAdvice
public class AppExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {

        ErrorMessage errorResponse = ErrorMessage.builder()
                .errorCode(ErrorCode.INVALID_INPUT.getCode())
                .message(processFieldErrors(ex.getBindingResult().getFieldErrors())).build();

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleMissingPathVariable(MissingPathVariableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        ErrorMessage errorResponse = ErrorMessage.builder()
                .errorCode(ErrorCode.INVALID_INPUT.getCode())
                .message(ex.getMessage()).build();

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    private String processFieldErrors(List<FieldError> fieldErrors) {
        StringBuilder errorStr = new StringBuilder();
        for (org.springframework.validation.FieldError fieldError : fieldErrors) {
            errorStr.append(fieldError.getDefaultMessage() + ".");
        }
        return errorStr.toString();
    }


    @ExceptionHandler(CustomException.class)
    @ResponseBody
    public final ResponseEntity<ErrorMessage> handleCustomException(CustomException ex) {
        ErrorMessage errorResponse = ErrorMessage.builder()
                .errorCode(ErrorCode.INTERNAL_ERROR.getCode())
                .message(ex.getMessage()).build();
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(InvalidInputException.class)
    @ResponseBody
    public final ResponseEntity<ErrorMessage> handleInvalidInputException(
            InvalidInputException ex) {
        ErrorMessage errorResponse = ErrorMessage.builder()
                .errorCode(ErrorCode.INVALID_INPUT.getCode())
                .message(ex.getMessage()).build();
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(JobNotFoundException.class)
    @ResponseBody
    public final ResponseEntity<ErrorMessage> handleJobNotFoundException(
            JobNotFoundException ex) {
        ErrorMessage errorResponse = ErrorMessage.builder()
                .errorCode(ErrorCode.INVALID_JOB_ID.getCode())
                .message(ex.getMessage()).build();
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public final ResponseEntity<ErrorMessage> generalExceptionHandler(Exception ex) {
        String uuid = UUID.randomUUID().toString();
        String message = String.format(ErrorConstants.UNEXPECTED_EXCEPTION_MSG_FORMAT, uuid, ex.getMessage());

        log.error(message, ex);
        ErrorMessage errorResponse = ErrorMessage.builder()
                .errorCode(ErrorCode.UNEXPECTED_EXCEPTION.getCode())
                .message(ErrorConstants.UNEXPECTED_EXCEPTION_MSG_CONTENT)
                .errorRefId(uuid)
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(RecordsNotFoundException.class)
    @ResponseBody
    public final ResponseEntity<ProfilerErrorMessage> handleRecordsNotFoundException(
            RecordsNotFoundException ex) {
        ProfilerErrorMessage errorResponse = ProfilerErrorMessage.builder()
                .errorCode(ErrorCode.INVALID_INPUT.getCode())
                .errorMessage(ex.getMessage()).build();
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }
}
