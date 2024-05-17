package com.cmsujeevan.cdp.exception.constants;

import lombok.Getter;

@Getter
public enum ErrorCode {

    UNEXPECTED_EXCEPTION("unexpected_error"),
    INVALID_INPUT_FORMAT("invalid_input_format"),
    INVALID_INPUT("invalid_input"),
    INVALID_JOB_ID("invalid_job_id"),
    INTERNAL_ERROR("internal_error");


    String code;

    ErrorCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
