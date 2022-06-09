package edu.sysu.pmglab.commandParser.exception;

/**
 * @author suranyi
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
