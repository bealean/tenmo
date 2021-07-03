package com.techelevator.tenmo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Insufficient Funds for Transfer.")
public class NotEnoughFundsException extends Exception {
    private static final long serialVersionUID = 1L;

    public NotEnoughFundsException() {
        super("Insufficient Funds for Transfer.");
    }
}
