package edu.sysu.pmglab.commandParser.exception;

/**
 * 解析器设置异常
 */

public class CommandParserException extends RuntimeException {
    public CommandParserException() {
        this("");
    }

    public CommandParserException(String message) {
        super(message);
    }

    public CommandParserException(Throwable throwable) {
        super(throwable);
    }
}
