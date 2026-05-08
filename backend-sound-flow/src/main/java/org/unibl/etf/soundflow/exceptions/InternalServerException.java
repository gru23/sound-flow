package org.unibl.etf.soundflow.exceptions;

import org.springframework.http.HttpStatus;

public class InternalServerException extends HttpException {
    public InternalServerException() {
        super(HttpStatus.INTERNAL_SERVER_ERROR, null);
    }

    public InternalServerException(Object data) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, data);
    }
}
