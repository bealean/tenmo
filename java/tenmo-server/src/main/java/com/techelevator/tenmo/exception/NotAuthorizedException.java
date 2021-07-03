package com.techelevator.tenmo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.FORBIDDEN, reason = "User is not authorized to perform request.")
public class NotAuthorizedException extends Exception {
    private static final long serialVersionUID = 1L;

    public NotAuthorizedException() {
        super("User is not authorized to perform request.");
    }
}
