package org.unibl.etf.soundflow.separationworker.exceptions;

public class NotFoundException extends RuntimeException {
    public NotFoundException() {}

    public NotFoundException(String message) {
        super(message);
    }
}
