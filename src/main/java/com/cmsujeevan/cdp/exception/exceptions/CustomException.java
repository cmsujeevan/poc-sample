package com.cmsujeevan.cdp.exception.exceptions;

import lombok.*;

@Getter
@Setter
public class CustomException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public CustomException(String message, Object ...args){
        super(String.format(message, args));
    }

}
