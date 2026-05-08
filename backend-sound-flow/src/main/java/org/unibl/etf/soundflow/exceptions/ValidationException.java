package org.unibl.etf.soundflow.exceptions;

public class ValidationException extends HttpException {
    public ValidationException() {

    }

    public ValidationException(String message) {
        super(message);
    }
}
