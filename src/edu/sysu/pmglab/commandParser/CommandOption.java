package edu.sysu.pmglab.commandParser;

/**
 * @author suranyi
 */

public class CommandOption<T> {
    public final boolean isPassedIn;
    public final T value;
    public final String matchedParameter;

    public CommandOption(String commandName, CommandOptions options) {
        this.isPassedIn = options.isPassedIn(commandName);
        this.value = (T) options.get(commandName);
        this.matchedParameter = options.getMatchedParameter(commandName);
    }
}
