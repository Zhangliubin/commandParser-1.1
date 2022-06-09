package edu.sysu.pmglab.commandParser;

import edu.sysu.pmglab.commandParser.exception.CommandParserException;
import edu.sysu.pmglab.container.BiDict;
import edu.sysu.pmglab.container.array.StringArray;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author suranyi
 */

public class CommandOptions {
    /**
     * 参数解析列表
     */
    private final Map<String, Object> values = new HashMap<>();
    private final StringArray matchedParameters = new StringArray();
    private final BiDict<String, Integer> matchedParameterIndexes = new BiDict<>();
    private final CommandParser parser;
    private final boolean help;

    CommandOptions(CommandParser parser, boolean help) {
        this.parser = parser;
        this.help = help;
    }

    /**
     * 是否为帮助模式
     *
     * @return 帮助模式, 此时没有 get 方法
     */
    public boolean isHelp() {
        return this.help;
    }

    /**
     * 注册参数
     *
     * @param commandItem 参数项
     * @param args        参数值, null 在 help 模式下产生
     */
    void add(CommandItem commandItem, String[] args) {
        String commandName = commandItem.getCommandName();

        if (args == null) {
            this.values.put(commandName, commandItem.getDefaultValue());
        } else {
            this.values.put(commandName, commandItem.parseValue(args));
            this.matchedParameters.add(StringArray.wrap(args).join(" "));
            this.matchedParameterIndexes.put(commandName, this.matchedParameters.size() - 1);
        }
    }

    /**
     * 获取指令捕捉的参数值
     *
     * @param commandName 参数名
     * @return 捕捉值
     */
    public String getMatchedParameter(String commandName) {
        int index = this.matchedParameterIndexes.valueOfOrDefault(getMainCommandName(commandName), -1);
        if (index == -1) {
            return null;
        } else {
            return this.matchedParameters.get(index);
        }
    }

    /**
     * 获取指令捕捉的参数值 (内部方法, 不校验)
     *
     * @param commandItem 参数名
     * @return 捕捉值
     */
    String getMatchedParameter(CommandItem commandItem) {
        int index = this.matchedParameterIndexes.valueOfOrDefault(commandItem.getCommandName(), -1);
        if (index == -1) {
            return null;
        } else {
            return this.matchedParameters.get(index);
        }
    }

    /**
     * 某个指令是否被传入
     *
     * @param commandName 参数名
     * @return 指令是否被传入
     */
    public boolean isPassedIn(String commandName) {
        return this.values.containsKey(getMainCommandName(commandName));
    }

    /**
     * 某个指令是否被传入 (内部方法, 不校验)
     *
     * @param commandItem 参数名
     * @return 指令是否被传入
     */
    boolean isPassedIn(CommandItem commandItem) {
        return this.values.containsKey(commandItem.getCommandName());
    }

    /**
     * 获取参数对应的值
     *
     * @param commandName 参数键
     * @return 参数值
     */
    public Object get(String commandName) {
        CommandItem commandItem = getMainCommandItem(commandName);
        return this.values.getOrDefault(commandItem.getCommandName(), commandItem.getDefaultValue());
    }

    /**
     * 获取所有值 (返回一个不可修改的 Map 对象)
     *
     * @return 获取所有参数的解析结果
     */
    public Map<String, Object> getValues() {
        return Collections.unmodifiableMap(this.values);
    }

    /**
     * 获取主参数项名
     *
     * @return 主参数项名
     */
    String getMainCommandName(String commandName) {
        return getMainCommandItem(commandName).getCommandName();
    }

    /**
     * 获取主参数项
     *
     * @return 主参数项
     */
    CommandItem getMainCommandItem(String commandName) {
        if (commandName == null) {
            throw new CommandParserException("syntax error: command name cannot be null value");
        }

        CommandItem item = this.parser.getCommandItem(commandName);
        if (item == null) {
            throw new CommandParserException("undefined command item: " + commandName);
        }

        return item;
    }

    @Override
    public String toString() {
        if (matchedParameters.size() == 0) {
            return "";
        } else {
            StringArray links = new StringArray();
            for (int i = 0; i < this.matchedParameters.size(); i++) {
                links.add(this.matchedParameterIndexes.keyOf(i) + " " + this.matchedParameters.get(i));
            }
            return links.join(" \\\n");
        }
    }
}