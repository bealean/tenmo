package com.techelevator.tenmo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Transfer could not be processed.")
public class TransferCreationException extends Exception {
    private static final long serialVersionUID = 1L;

    public TransferCreationException() {
        super("Transfer could not be processed.");
    }
}
