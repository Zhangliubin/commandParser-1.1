package edu.sysu.pmglab.commandParser;


/**
 * 参数项捕获信息
 *
 * @param <T> 参数值类型
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

    /**
     * 构造器方法
     *
     * @param commandName 参数名
     * @param options     参数项
     */
    @SuppressWarnings("unchecked")
    public CommandOption(String commandName, CommandOptions options) {
        this.isPassedIn = options.isPassedIn(commandName);
        this.value = (T) options.get(commandName);
        this.matchedParameter = options.getMatchedParameter(commandName);
    }
}
