package edu.sysu.pmglab.commandParser.exception;

/**
 * @author suranyi
 * @description 参数异常
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
