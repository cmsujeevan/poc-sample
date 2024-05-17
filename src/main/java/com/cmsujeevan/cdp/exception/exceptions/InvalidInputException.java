package com.cmsujeevan.cdp.exception.exceptions;

import lombok.*;

@Getter
@Setter
public class InvalidInputException extends CustomException {
    private static final long serialVersionUID = 4292077091698735591L;

    public InvalidInputException(String message, Object... args) {
        super(message, args);
    }

}
