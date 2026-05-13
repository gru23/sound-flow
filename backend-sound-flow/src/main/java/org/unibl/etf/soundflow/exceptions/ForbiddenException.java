package org.unibl.etf.soundflow.exceptions;

import org.springframework.http.HttpStatus;

public class ForbiddenException extends HttpException {
    public ForbiddenException(Object object) {
        super(HttpStatus.FORBIDDEN, object);
    }
}
