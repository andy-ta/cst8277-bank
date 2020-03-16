package com.algonquincollege.cst8277bank.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class ClientException extends Exception {

    public ClientException(String message) {
        super(message);
    }
}
