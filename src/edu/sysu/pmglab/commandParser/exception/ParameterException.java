package edu.sysu.pmglab.commandParser.exception;

/**
 * 参数设置异常
 */

public class ParameterException extends RuntimeException {
    public ParameterException() {
        this("");
    }

    public ParameterException(String message) {
        super(message);
    }

    public ParameterException(Throwable throwable) {
        super(throwable);
    }

    public ParameterException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
