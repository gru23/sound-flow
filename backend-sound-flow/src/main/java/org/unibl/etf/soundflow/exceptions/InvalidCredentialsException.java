package org.unibl.etf.soundflow.exceptions;

public class InvalidCredentialsException extends HttpException {
    public InvalidCredentialsException() {
        super();
    }

    public InvalidCredentialsException(String message) {
        super(message);
    }
}
