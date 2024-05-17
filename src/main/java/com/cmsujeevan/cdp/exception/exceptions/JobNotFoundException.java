package com.cmsujeevan.cdp.exception.exceptions;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JobNotFoundException extends CustomException {
    private static final long serialVersionUID = 4292156091098736789L;

    public JobNotFoundException(String message, Object... args) {
        super(message, args);
    }
}