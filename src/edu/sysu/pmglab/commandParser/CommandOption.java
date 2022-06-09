package edu.sysu.pmglab.commandParser;

/**
 * @author suranyi
 */

public class CommandOption<T> {
    /**
     * 参数是否被传入
     */
    public final boolean isPassedIn;

    /**
     * 参数值 (未被传入时, 该值为默认值)
     */
    public final T value;

    /**
     * 捕获的字符串文本
     */
    public final String matchedParameter;

    public CommandOption(String commandName, CommandOptions options) {
        this.isPassedIn = options.isPassedIn(commandName);
        this.value = (T) options.get(commandName);
        this.matchedParameter = options.getMatchedParameter(commandName);
    }
}
