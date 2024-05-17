package com.cmsujeevan.cdp.exception.exceptions;

import lombok.*;

@Getter
@Setter
public class S3Exception extends CustomException {

    private static final long serialVersionUID = 4292156091098736789L;


    public S3Exception(String message, Object... args) {
        super(message, args);
    }
}
