package org.unibl.etf.soundflow.exceptions;

import org.springframework.http.HttpStatus;

public class DuplicateValueException extends HttpException {
    public DuplicateValueException() {
        super(HttpStatus.CONFLICT, null);
    }

    public DuplicateValueException(Object data) {
        super(HttpStatus.CONFLICT, data);
    }
}
