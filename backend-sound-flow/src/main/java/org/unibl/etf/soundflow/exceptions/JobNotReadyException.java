package org.unibl.etf.soundflow.exceptions;

public class JobNotReadyException extends HttpException {
    public JobNotReadyException() {
        super();
    }

    public JobNotReadyException(String message) {
        super(message);
    }
}
