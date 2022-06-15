package edu.sysu.pmglab.commandParser;

import edu.sysu.pmglab.commandParser.exception.CommandParserException;
import edu.sysu.pmglab.commandParser.exception.ParameterException;
import edu.sysu.pmglab.commandParser.types.IType;
import edu.sysu.pmglab.commandParser.usage.DefaultStyleUsage;
import edu.sysu.pmglab.commandParser.usage.IUsage;
import edu.sysu.pmglab.container.File;
import edu.sysu.pmglab.container.array.Array;
import edu.sysu.pmglab.container.array.BaseArray;
import edu.sysu.pmglab.container.array.StringArray;
import edu.sysu.pmglab.easytools.ValueUtils;
import edu.sysu.pmglab.unifyIO.FileStream;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;

/**
 * @author suranyi
 */

public class CommandParser implements Iterable<CommandItem>, Cloneable {
    /**
     * 版本号
     */
    public static final String VERSION = "1.1";

    /**
     * 注册的参数组
     */
    private final BaseArray<CommandGroup> groups = new Array<>();

    /**
     * 注册的规则
     */
    private final BaseArray<CommandRule> rules = new Array<>();

    /**
     * 忽略前 offset 个指令
     */
    private int offset = 0;

    /**
     * 是否解析 debug 指令
     */
    private boolean debug = false;

    /**
     * 使用 @ 表示转译字符内容
     */
    private boolean usingAtSymbol = true;

    /**
     * 最大匹配参数个数, 用于入口方法使用, 不能等于 0
     */
    private int maxMatchedNum = -1;

    /**
     * 自动添加 help 指定
     */
    private boolean autoHelp = false;

    /**
     * 程序帮助文档
     */
    private IUsage usage = DefaultStyleUsage.UNIX_TYPE_1;

    /**
     * 主程序名
     */
    private String programName;

    public CommandParser() {
        this(true, "<main class>");
    }

    public CommandParser(String programName) {
        this(true, programName);
    }

    public CommandParser(boolean init) {
        this(init, "<main class>");
    }

    public CommandParser(boolean init, String programName) {
        if (init) {
            CommandGroup optionGroup = addCommandGroup("Options");
            optionGroup.register(IType.NONE, "--help", "-help", "-h")
                    .addOptions(CommandItem.HELP, CommandItem.HIDDEN);
        }

        this.programName = ValueUtils.notNull(programName, "");
    }

    /**
     * 获取程序名
     *
     * @return 程序名
     */
    public String getProgramName() {
        return this.programName;
    }

    /**
     * 重设主类名
     *
     * @param programName 主程序名
     * @return 当前解析器
     */
    public CommandParser setProgramName(String programName) {
        this.programName = ValueUtils.notNull(programName, "");
        return this;
    }

    /**
     * 设置文档风格
     *
     * @param usageStyle 文档风格, 建议使用内置的
     * @return 当前解析器
     */
    public CommandParser setUsageStyle(IUsage usageStyle) {
        if (usageStyle == null) {
            this.usage = DefaultStyleUsage.UNIX_TYPE_1;
        } else {
            this.usage = usageStyle;
        }

        return this;
    }

    /**
     * 跳过前面的 length 个参数
     *
     * @param length 偏移参数
     * @return 当前解析器
     */
    public CommandParser offset(int length) {
        this.offset = Math.max(length, 0);
        return this;
    }

    /**
     * debug 模式
     *
     * @param enable 是否为 debug 模式
     * @return 当前解析器
     */
    public CommandParser debug(boolean enable) {
        this.debug = enable;
        return this;
    }

    /**
     * 使用 @ 识别路径信息
     *
     * @param enable 是否识别 @ 为取地址符
     * @return 当前解析器
     */
    public CommandParser usingAt(boolean enable) {
        this.usingAtSymbol = enable;
        return this;
    }

    /**
     * 设置最大匹配参数个数 (不能为 0)
     *
     * @param maxMatchedNum 最大匹配参数个数
     * @return 当前解析器
     */
    public CommandParser setMaxMatchedNum(int maxMatchedNum) {
        if (maxMatchedNum == 0) {
            maxMatchedNum = -1;
        }

        this.maxMatchedNum = Math.max(maxMatchedNum, -1);
        return this;
    }

    /**
     * 设置: 当没有指令被传入时, 自动添加 help 指令
     *
     * @param enable 是否自动添加 help 指令
     * @return 当前解析器
     */
    public CommandParser setAutoHelp(boolean enable) {
        this.autoHelp = enable;
        return this;
    }

    /**
     * 是否为 debug 模式
     *
     * @return 是否为 debug 模式
     */
    public boolean isDebug() {
        return this.debug;
    }

    /**
     * 是否使用 @ 语法
     *
     * @return 是否使用 @ 语法
     */
    public boolean isUsingAtSyntax() {
        return this.usingAtSymbol;
    }

    /**
     * 是否在无指令传入时自动添加 help 指令
     *
     * @return 是否自动添加 help 指令
     */
    public boolean isAutoHelp() {
        return this.autoHelp;
    }

    /**
     * 获取偏移量
     *
     * @return 偏移量
     */
    public int getOffset() {
        return this.offset;
    }

    /**
     * 获取最大匹配个数
     *
     * @return 最大匹配个数
     */
    public int getMaxMatchedNum() {
        return maxMatchedNum;
    }

    /**
     * 获取文档格式化器
     *
     * @return 文档格式化器
     */
    public IUsage getUsage() {
        return usage;
    }

    /**
     * 添加参数组
     *
     * @param groupName 参数组名
     * @return 添加的参数组
     */
    public CommandGroup addCommandGroup(String groupName) {
        for (CommandGroup group : this.groups) {
            if (group.getGroupName().equals(groupName)) {
                return group;
            }
        }

        CommandGroup group = new CommandGroup(groupName);
        this.groups.add(group);
        return group;
    }

    /**
     * 添加参数组
     *
     * @param group 参数组
     * @return 添加的参数组
     */
    public CommandGroup addCommandGroup(CommandGroup group) {
        if (group == null) {
            throw new CommandParserException("syntax error: commandGroup is null");
        }

        for (CommandGroup existsGroup : this.groups) {
            if (group.getGroupName().equals(existsGroup.name)) {
                // 已经存在相应的组名, 则合并参数
                existsGroup.registerAll(group);
                return existsGroup;
            }
        }

        // 不存在相应的组
        this.groups.add(group);
        return group;
    }

    /**
     * 向最后一个添加的参数组添加指令
     *
     * @param type         参数类型
     * @param commandNames 参数名
     * @return 添加的参数项
     */
    public CommandItem register(IType type, String... commandNames) {
        if (this.groups.size() == 0) {
            return addCommandGroup("Options").register(type, commandNames);
        } else {
            return this.groups.get(-1).register(type, commandNames);
        }
    }

    /**
     * 向最后一个添加的参数组添加指令
     *
     * @param tClass       参数类型
     * @param commandNames 参数名
     * @return 添加的参数项
     */
    public CommandItem register(Class<?> tClass, String... commandNames) {
        if (this.groups.size() == 0) {
            return addCommandGroup("Options").register(tClass, commandNames);
        } else {
            return this.groups.get(-1).register(tClass, commandNames);
        }
    }

    /**
     * 向最后一个添加的参数组添加指令
     *
     * @param commandItem 参数项
     * @return 添加的参数项
     */
    public CommandItem register(CommandItem commandItem) {
        if (this.groups.size() == 0) {
            return addCommandGroup("Options").register(commandItem);
        } else {
            return this.groups.get(-1).register(commandItem);
        }
    }

    /**
     * 获取参数组
     *
     * @param groupName 参数组名
     * @return 获取组名为 groupName 的参数组, 不存在时返回 null
     */
    public CommandGroup getCommandGroup(String groupName) {
        for (CommandGroup group : this.groups) {
            if (group.getGroupName().equals(groupName)) {
                return group;
            }
        }

        return null;
    }

    /**
     * 添加参数规则
     *
     * @param rule 参数规则
     * @return 当前解析器
     */
    public CommandParser addRule(CommandRule rule) {
        StringArray commandNames = (rule.getCommands());

        if (rule.isNumberedRule()) {
            this.rules.add(new CommandRule(rule.getRuleType(), rule.getNumber(), commandNames));
        } else {
            this.rules.add(new CommandRule(rule.getRuleType(), commandNames));
        }
        return this;
    }

    /**
     * 添加参数规则
     *
     * @param ruleType         参数规则类型
     * @param conditionalValue 条件数
     * @param commands         作用的参数名
     * @return 当前解析器
     */
    public CommandParser addRule(String ruleType, int conditionalValue, String... commands) {
        this.rules.add(new CommandRule(ruleType, conditionalValue, StringArray.wrap(commands)));
        return this;
    }

    /**
     * 添加参数规则
     *
     * @param ruleType 参数规则类型
     * @param commands 作用的参数名
     * @return 当前解析器
     */
    public CommandParser addRule(String ruleType, String... commands) {
        this.rules.add(new CommandRule(ruleType, StringArray.wrap(commands)));
        return this;
    }

    // 在 help 传入时, 使用简单解析模式
    private CommandOptions simpleParse(StringArray params) {
        CommandOptions options = new CommandOptions(this, true);
        int matchedNums = 0;

        if (isDebug()) {
            while (params.size() > 0) {
                String param = params.popFirst();
                CommandItem matchedItem = getCommandItem(param);

                if (matchedItem != null) {
                    matchedNums += 1;
                    options.add(matchedItem, null);

                    if (matchedNums == this.maxMatchedNum) {
                        break;
                    }
                }
            }
        } else {
            while (params.size() > 0) {
                String param = params.popFirst();
                CommandItem matchedItem = getCommandItem(param);

                if (matchedItem != null && !matchedItem.isDebug()) {
                    matchedNums += 1;
                    options.add(matchedItem, null);

                    if (matchedNums == this.maxMatchedNum) {
                        break;
                    }
                }
            }
        }

        return options;
    }

    // 没有 help 传入时, 使用完整解析模式
    private CommandOptions fullParse(StringArray params) {
        CommandOptions options = new CommandOptions(this, false);
        int matchedNums = 0;

        if (isDebug()) {
            while (params.size() > 0) {
                String param = params.popFirst();
                CommandItem matchedItem = getCommandItem(param);

                if (matchedItem == null) {
                    throw new ParameterException(param + " is passed in but no commandItem was defined in Parser");
                } else {
                    matchedNums += 1;

                    if (options.isPassedIn(matchedItem)) {
                        throw new ParameterException("Keyword argument repeated: " + matchedItem.getCommandName());
                    } else if (matchedNums == this.maxMatchedNum) {
                        // 看看后面还有没有参数
                        if (matchedItem.getArity() == -1 || matchedItem.getArity() == params.size()) {
                            // 不定长参数或刚好长度符合, 则将所有的参数值都给最后一个参数
                            options.add(matchedItem, params.toArray());
                            params.clear();
                            break;
                        } else {
                            throw new ParameterException(matchedItem.getCommandName() + " takes " + matchedItem.getArity() + " positional argument (" + params.size() + " given)");
                        }
                    }

                    // 第一次被传入该参数, 并且未达到最大匹配长度, 则匹配指定长度的参数
                    if (matchedItem.getArity() == -1) {
                        // 不定长参数
                        int length = 0;
                        while (length < params.size()) {
                            if (containCommandItem(params.get(length))) {
                                break;
                            } else {
                                length++;
                            }
                        }

                        if (length == 0) {
                            // 任意长度的参数，但是没有输入
                            options.add(matchedItem, new String[0]);
                        } else {
                            // 添加捕获组
                            options.add(matchedItem, params.popFirst(length).toArray());
                        }
                    } else if (matchedItem.getArity() == 0) {
                        options.add(matchedItem, new String[0]);
                    } else {
                        // 有指定长度
                        int length = 0;
                        int maxLength = Math.min(params.size(), matchedItem.getArity());
                        while (length < maxLength) {
                            if (containCommandItem(params.get(length))) {
                                break;
                            } else {
                                length++;
                            }
                        }

                        if (length < matchedItem.getArity()) {
                            throw new ParameterException(matchedItem.getCommandName() + " takes " + matchedItem.getArity() + " positional argument (" + length + " given)");
                        } else {
                            // 添加捕获组
                            options.add(matchedItem, params.popFirst(length).toArray());
                        }
                    }
                }
            }

            // 检查所有 request 参数
            for (CommandItem commandItem : this) {
                if (commandItem.isRequest() && !options.isPassedIn(commandItem)) {
                    throw new ParameterException("Missing required positional argument: " + commandItem.getCommandName());
                }
            }

        } else {
            while (params.size() > 0) {
                String param = params.popFirst();
                CommandItem matchedItem = getCommandItem(param);

                if (matchedItem == null) {
                    throw new ParameterException(param + " is passed in but no commandItem was defined in Parser");
                } else if (matchedItem.isDebug()) {
                    throw new ParameterException(param + " is a debug parameter that can only be used when debug=true (currently: debug=false)");
                } else {
                    matchedNums += 1;

                    if (options.isPassedIn(matchedItem)) {
                        throw new ParameterException("keyword argument repeated: " + matchedItem.getCommandName());
                    }

                    if (matchedNums == this.maxMatchedNum) {
                        // 看看后面还有没有参数
                        if (matchedItem.getArity() == -1 || matchedItem.getArity() == params.size()) {
                            // 不定长参数, 则将所有的参数值都给最后一个参数
                            options.add(matchedItem, params.toArray());
                            params.clear();
                            continue;
                        } else {
                            throw new ParameterException(matchedItem.getCommandName() + " takes " + matchedItem.getArity() + " positional argument (" + params.size() + " given)");
                        }
                    }

                    // 匹配指定长度的参数
                    if (matchedItem.getArity() == -1) {
                        // 不定长参数
                        int length = 0;
                        while (length < params.size()) {
                            CommandItem existsCommandItem = getCommandItem(params.get(length));
                            if (existsCommandItem != null && !existsCommandItem.isDebug()) {
                                break;
                            } else {
                                length++;
                            }
                        }

                        if (length == 0) {
                            // 任意长度的参数，但是没有输入
                            options.add(matchedItem, new String[0]);
                        } else {
                            // 添加捕获组
                            options.add(matchedItem, params.popFirst(length).toArray());
                        }
                    } else if (matchedItem.getArity() == 0) {
                        options.add(matchedItem, new String[0]);
                    } else {
                        // 有指定长度
                        int length = 0;
                        int maxLength = Math.min(params.size(), matchedItem.getArity());
                        while (length < maxLength) {
                            CommandItem existsCommandItem = getCommandItem(params.get(length));
                            if (existsCommandItem != null && !existsCommandItem.isDebug()) {
                                break;
                            } else {
                                length++;
                            }
                        }

                        if (length < matchedItem.getArity()) {
                            throw new ParameterException(matchedItem.getCommandName() + " takes " + matchedItem.getArity() + " positional argument (" + length + " given)");
                        } else {
                            // 添加捕获组
                            options.add(matchedItem, params.popFirst(length).toArray());
                        }
                    }
                }
            }

            // 检查所有 request 参数
            for (CommandItem commandItem : this) {
                if (!commandItem.isDebug() && commandItem.isRequest() && !options.isPassedIn(commandItem)) {
                    throw new ParameterException("missing required positional argument: " + commandItem.getCommandName());
                }
            }
        }

        // 检查参数间的规则
        for (CommandRule rule : this.rules) {
            if (!rule.check(options)) {
                throw new ParameterException(rule.toString());
            }
        }
        return options;
    }

    /**
     * 解析指令
     *
     * @param args 待解析的指令列表
     * @return 返回解析结果
     */
    public CommandOptions parse(String... args) {
        // 检查基本参数长度
        if (this.offset > args.length) {
            throw new ParameterException("Program takes at least " + this.offset + " positional argument (because offset=" + this.offset + ", but " + args.length + " given)");
        }

        // 检查是否有指令重叠
        if (!checkParser()) {
            throw new CommandParserException("unable to parse parameters within illegal parser (repeated commandItem)");
        }

        // 包装输入的参数信息
        StringArray params = new StringArray(args, this.offset, args.length - this.offset);

        // 查看是否包含 @ 指令，如果包含则将内容解析出来
        parseAtSymbol(params);

        boolean passedInHelp;
        if (this.autoHelp && params.size() == 0) {
            // 查看该解析器中是否有 help 参数
            for (CommandItem item : this) {
                if (item.isHelp()) {
                    params.add(item.getCommandName());
                    break;
                }
            }
        }

        passedInHelp = passedInHelp(params);

        // 查看是否包含 help 指令, 如果包含 help 指令，则不进行强制的参数解析工作
        if (passedInHelp) {
            // 传入了 help 指令, 则使用简单解析工作
            return simpleParse(params);
        } else {
            return fullParse(params);
        }
    }

    /**
     * 解析指令
     *
     * @param file 参数文件名
     * @return 返回解析结果
     * @throws IOException 读取文件时可能触发 IO 异常
     */
    public CommandOptions parse(File file) throws IOException {
        return parse(readFromFile(file));
    }

    /**
     * 从文件中读取信息, 并转为可解析指令
     *
     * @param file 文件名
     * @return 从文件中读取的指令
     * @throws IOException 读取文件时可能触发 IO 异常
     */
    public static String[] readFromFile(File file) throws IOException {
        try (FileStream fileStream = new FileStream(file, file.getFileName().endsWith(".gz") ? FileStream.GZIP_READER : FileStream.DEFAULT_READER)) {
            return convertStrings(new String(fileStream.readAll()));
        }
    }

    /**
     * 解析指令
     *
     * @param strings 长字符串
     * @return 返回解析结果
     */
    public static String[] convertStrings(String strings) {
        // 按行切割数据
        String[] lines = strings.split("\n");

        StringArray args = new StringArray();

        for (String line : lines) {
            // 去除首尾空白信息, 把 \t 换为空格，把多个空格替换为一个空格
            line = line.replace("\t", " ").replaceAll(" +", " ").trim();
            if (line.startsWith("#") || line.equals("\\")) {
                // 当作注释字段、空白行过滤
                continue;
            }

            if (line.endsWith(" \\")) {
                line = line.substring(0, line.length() - 2);
            }

            if (line.length() > 0) {
                // 以 \ 结尾，去除该字符
                for (String arg : line.split(" ")) {
                    if (arg.length() > 0) {
                        args.add(arg);
                    }
                }
            }
        }

        return args.toArray();
    }

    /**
     * 获取注册的参数项
     *
     * @param commandName 参数名
     * @return 根据参数名获取的参数项
     */
    public CommandItem getCommandItem(String commandName) {
        for (CommandGroup group : this.groups) {
            CommandItem commandItem = group.get(commandName);
            if (commandItem != null) {
                return commandItem;
            }
        }
        return null;
    }

    /**
     * 是否包含该参数项
     *
     * @param commandName 参数名
     * @return 是否包含该参数项
     */
    public boolean containCommandItem(String commandName) {
        for (CommandGroup group : this.groups) {
            if (group.contain(commandName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 解析 @ 字符，并将内容替换为文件的内容
     *
     * @param args 参数列表
     */
    private void parseAtSymbol(StringArray args) {
        if (this.usingAtSymbol) {
            args.setAutoExpansion(true);
            boolean containAtSymbol = false;

            // 先确定存在 @ 符号
            for (String arg : args) {
                if (arg.startsWith("@")) {
                    containAtSymbol = true;
                    break;
                }
            }

            while (containAtSymbol) {
                containAtSymbol = false;
                // 再进行转换
                StringArray newArgs = new StringArray();
                for (String arg : args) {
                    if (arg.startsWith("@")) {
                        try {
                            newArgs.addAll(readFromFile(File.of(arg.substring(1))));
                        } catch (IOException e) {
                            throw new ParameterException("unable to get parameters from " + arg.substring(1) + ": " + e.getMessage());
                        }
                    } else {
                        newArgs.add(arg);
                    }
                }

                args.clear();
                args.addAll(newArgs);

                for (String arg : args) {
                    if (arg.startsWith("@")) {
                        containAtSymbol = true;
                        break;
                    }
                }
            }
        }
    }

    /**
     * 检查 parser 的可用性 (检查参数)
     *
     * @return 是否为合法解析器
     */
    private boolean checkParser() {
        // 先检查指令名称是否有重复
        HashSet<String> commandNames = new HashSet<>();
        for (CommandItem commandItem : this) {
            for (String commandName : commandItem) {
                if (commandNames.contains(commandName)) {
                    commandNames.clear();
                    throw new CommandParserException("illegal parser: repeated commandItem(" + commandName + ")");
                } else {
                    commandNames.add(commandName);
                }
            }
        }

        commandNames.clear();

        // 再检查规则里是否有错误的设置
        for (CommandRule commandRule : this.rules) {
            HashSet<String> commandNameSet = new HashSet<>();

            for (String commandName : commandRule) {
                CommandItem commandItem = getCommandItem(commandName);
                if (commandItem == null) {
                    throw new CommandParserException("illegal parser: commandItem(" + commandName + ") not registered");
                }

                if (commandItem.isRequest()) {
                    throw new CommandParserException("illegal parser: commandItem(" + commandName + ") is a required commandItem and cannot register rule with other command items");
                }

                if (commandItem.isHelp()) {
                    throw new CommandParserException("illegal parser: commandItem(" + commandName + ") is a help-marked commandItem and cannot register rule with other command items");
                }

                if (commandNameSet.contains(commandItem.getCommandName())) {
                    throw new CommandParserException("illegal parser: add rule for the same commandItem(" + commandItem.getCommandName() + ")");
                } else {
                    commandNameSet.add(commandItem.getCommandName());
                }
            }

            commandNameSet.clear();
        }
        return true;
    }

    /**
     * 检查是否为 help 模式
     *
     * @param args 传入的参数列表
     * @return 是否为 help 模式
     */
    private boolean passedInHelp(StringArray args) {
        int matchedNums = 0;
        if (isDebug()) {
            for (String arg : args) {
                CommandItem matchedItem = getCommandItem(arg);
                if (matchedItem != null) {
                    matchedNums += 1;

                    if (matchedItem.isHelp()) {
                        return true;
                    }

                    if (matchedNums == this.maxMatchedNum) {
                        return false;
                    }
                }
            }
        } else {
            for (String arg : args) {
                CommandItem matchedItem = getCommandItem(arg);
                if (matchedItem != null && !matchedItem.isDebug()) {
                    matchedNums += 1;

                    if (matchedItem.isHelp()) {
                        return true;
                    }

                    if (matchedNums == this.maxMatchedNum) {
                        return false;
                    }
                }
            }
        }

        return false;
    }

    /**
     * 打印文档, 调用 Usage 实现
     */
    @Override
    public String toString() {
        if (checkParser()) {
            StringBuilder builder = new StringBuilder();
            builder.append(this.usage.formatHeader(this.programName));

            for (CommandGroup group : this.groups) {
                String groupUsage = this.usage.formatGroup(group, this.debug);
                if (groupUsage.length() > 0) {
                    builder.append("\n");
                    builder.append(groupUsage);
                }
            }
            return builder.toString();
        } else {
            throw new CommandParserException("unable to format an illegal parser (repeated commandItem)");
        }
    }

    /**
     * 参数组迭代器
     *
     * @return 参数组迭代器
     */
    public Iterator<CommandGroup> groupIterator() {
        return this.groups.iterator();
    }

    /**
     * 参数规则迭代器
     *
     * @return 参数规则迭代器
     */
    public Iterator<CommandRule> ruleIterator() {
        return this.rules.iterator();
    }

    /**
     * 指令迭代器
     */
    @Override
    public Iterator<CommandItem> iterator() {
        return new Iterator<CommandItem>() {
            int groupPointer = 0;
            Iterator<CommandItem> groupIterator = groups.size() == 0 ? null : groups.get(0).iterator();

            @Override
            public boolean hasNext() {
                if (groupIterator == null) {
                    return false;
                }

                if (groupIterator.hasNext()) {
                    return true;
                }

                while (true) {
                    groupPointer++;
                    if (groupPointer >= groups.size()) {
                        return false;
                    } else {
                        groupIterator = groups.get(groupPointer).iterator();
                        if (groupIterator.hasNext()) {
                            return true;
                        }
                    }
                }
            }

            @Override
            public CommandItem next() {
                return groupIterator.next();
            }
        };
    }

    @Override
    public CommandParser clone() {
        if (checkParser()) {
            CommandParser newParser = new CommandParser(false, this.programName);
            newParser.offset = this.offset;
            newParser.debug = this.debug;
            newParser.usingAtSymbol = this.usingAtSymbol;
            newParser.maxMatchedNum = this.maxMatchedNum;
            newParser.usage = this.usage;
            newParser.autoHelp = this.autoHelp;

            for (CommandGroup group : this.groups) {
                newParser.groups.add(group.clone());
            }

            for (CommandRule rule : this.rules) {
                newParser.rules.add(rule);
            }
            return newParser;
        } else {
            throw new CommandParserException("unable to clone an illegal parser (repeated commandItem)");
        }
    }

    /**
     * 参数规则的个数
     *
     * @return 当前解析器包含的参数规则个数
     */
    public int numOfCommandRules() {
        return this.rules.size();
    }

    /**
     * 参数组的个数
     *
     * @return 当前解析器包含的参数组个数
     */
    public int numOfCommandGroups() {
        return this.groups.size();
    }

    public static void main(String[] args) {
        System.out.println("Version: CommandParser-" + VERSION + " (https://pmglab.top/commandParser)");
    }
}

