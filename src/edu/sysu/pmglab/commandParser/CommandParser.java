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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

/**
 * @author suranyi
 * @description 命令解析器
 */

public class CommandParser implements Iterable<CommandItem>, Cloneable {
    private final static Logger logger = LoggerFactory.getLogger("CommandParser-1.1");

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
     * 最大匹配参数个数, 用于入口方法使用
     */
    private int maxMatchedNum = -1;

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
     */
    public String getProgramName() {
        return this.programName;
    }

    /**
     * 重设主类名
     *
     * @param programName 主程序名
     */
    public CommandParser setProgramName(String programName) {
        this.programName = ValueUtils.notNull(programName, "");
        return this;
    }

    /**
     * 设置文档风格
     *
     * @param usageStyle 文档风格, 建议使用内置的
     */
    public CommandParser setUsageStyle(IUsage usageStyle) {
        if (usageStyle == null) {
            if (isDebug()) {
                logger.warn("Syntax error: parser.setUsageStyle(null)");
            }
        } else {
            this.usage = usageStyle;
        }

        return this;
    }

    /**
     * 跳过前面的 length 个参数
     *
     * @param length 偏移参数
     */
    public CommandParser offset(int length) {
        if (length < 0) {
            this.offset = 0;
            if (isDebug()) {
                logger.warn("Syntax error: parser.offset({}), offset must be equal to a non-negative integer", length);
            }
        } else {
            this.offset = length;
        }

        return this;
    }

    /**
     * debug 模式
     */
    public CommandParser debug(boolean enable) {
        this.debug = enable;

        if (enable) {
            logger.info("Debug mode is activated, the work log of commandParser will be output to the console");
        }

        return this;
    }

    /**
     * 使用 @ 识别路径信息
     */
    public CommandParser usingAt(boolean enable) {
        this.usingAtSymbol = enable;

        if (isDebug() && enable) {
            logger.info("The @ syntax is activated, which allows users to put options into the file and pass in with @file");
        }

        return this;
    }

    /**
     * 设置最大匹配参数个数
     *
     * @param maxMatchedNum 最大匹配参数个数
     */
    public CommandParser setMaxMatchedNum(int maxMatchedNum) {
        if (maxMatchedNum == -1) {
            // 代表不受限制
            this.maxMatchedNum = maxMatchedNum;
        } else if (maxMatchedNum < -1) {
            this.maxMatchedNum = -1;
            if (isDebug()) {
                logger.warn("Syntax error: parser.setMaxMatchedNum({}), maxMatchedNum must be equal to -1 (means no limits) or a non-negative integer", maxMatchedNum);
            }
        } else {
            this.maxMatchedNum = maxMatchedNum;
        }

        return this;
    }

    /**
     * 是否为 debug 模式
     */
    public boolean isDebug() {
        return this.debug;
    }

    /**
     * 是否使用 @ 语法
     */
    public boolean isUsingAtSyntax() {
        return this.usingAtSymbol;
    }

    /**
     * 获取偏移量
     */
    public int getOffset() {
        return this.offset;
    }

    /**
     * 获取最大匹配个数
     */
    public int getMaxMatchedNum() {
        return maxMatchedNum;
    }

    /**
     * 获取文档格式化器
     */
    public IUsage getUsage() {
        return usage;
    }

    /**
     * 添加命令组
     *
     * @param groupName 命令组名
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
     * 添加命令组
     *
     * @param group 命令组
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
     */
    public CommandItem register(CommandItem commandItem) {
        if (this.groups.size() == 0) {
            return addCommandGroup("Options").register(commandItem);
        } else {
            return this.groups.get(-1).register(commandItem);
        }
    }

    /**
     * 获取命令组
     *
     * @param groupName 命令组名
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
     * 添加规则
     */
    public CommandParser addRule(CommandRule rule) {
        StringArray commandNames = checkRule(rule.getCommands());

        if (rule.isNumberedRule()) {
            this.rules.add(new CommandRule(rule.getRuleType(), rule.getNumber(), commandNames));
        } else {
            this.rules.add(new CommandRule(rule.getRuleType(), commandNames));
        }
        return this;
    }

    /**
     * 添加规则
     */
    public CommandParser addRule(String ruleType, int conditionalValue, String... commands) {
        this.rules.add(new CommandRule(ruleType, conditionalValue, checkRule(StringArray.wrap(commands))));
        return this;
    }

    /**
     * 添加规则
     */
    public CommandParser addRule(String ruleType, String... commands) {
        this.rules.add(new CommandRule(ruleType, checkRule(StringArray.wrap(commands))));
        return this;
    }

    /**
     * 检查参数名是否合法, 并转为标准参数名
     */
    private StringArray checkRule(StringArray commands) {
        HashSet<String> commandNameSet = new HashSet<>(commands.size());
        StringArray commandNames = new StringArray(commands.size());

        for (String commandName : commands) {
            CommandItem commandItem = getCommandItem(commandName);
            if (commandItem == null) {
                throw new CommandParserException(commandName + " is not defined in Parser");
            }

            if (commandItem.isRequest()) {
                throw new CommandParserException(commandName + " is a required commandItem and cannot register rule with other command items");
            }

            if (commandItem.isHelp()) {
                throw new CommandParserException(commandName + " is a help-marked commandItem and cannot register rule with other command items");
            }

            if (commandNameSet.contains(commandItem.getCommandName())) {
                throw new CommandParserException("syntax error: add rule for the same commandItem(" + commandItem.getCommandName() + ")");
            } else {
                commandNameSet.add(commandItem.getCommandName());
                commandNames.add(commandItem.getCommandName());
            }
        }
        return commandNames;
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
                    logger.info("Match commandItem: {}", matchedItem.getCommandName());

                    if (matchedNums == this.maxMatchedNum) {
                        break;
                    }
                }
            }
            logger.info("Finish matching parameters, {} commandItems in total", matchedNums);
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
            int errorNums = 0;
            boolean error = false;
            while (params.size() > 0) {
                String param = params.popFirst();
                CommandItem matchedItem = getCommandItem(param);

                if (matchedItem == null) {
                    error = true;
                    errorNums++;
                    logger.error("{} is passed in but no commandItem was defined in Parser", param);
                } else {
                    matchedNums += 1;

                    if (options.isPassedIn(matchedItem)) {
                        error = true;
                        errorNums++;
                        logger.error("Keyword argument repeated: {}", matchedItem.getCommandName());
                    } else if (matchedNums == this.maxMatchedNum) {
                        // 看看后面还有没有参数
                        if (matchedItem.getArity() == -1 || matchedItem.getArity() == params.size()) {
                            // 不定长参数或刚好长度符合, 则将所有的参数值都给最后一个参数
                            options.add(matchedItem, params.toArray());
                            logger.info("Match commandItem: {} {}", matchedItem.getCommandName(), options.getMatchedParameter(matchedItem));
                            params.clear();
                            break;
                        } else {
                            error = true;
                            errorNums++;
                            logger.error("{} takes {} positional argument ({} given)", matchedItem.getCommandName(), matchedItem.getArity(), params.size());
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
                        logger.info("Match commandItem: {} {}", matchedItem.getCommandName(), options.getMatchedParameter(matchedItem));
                    } else if (matchedItem.getArity() == 0) {
                        options.add(matchedItem, new String[0]);
                        logger.info("Match commandItem: {} {}", matchedItem.getCommandName(), options.getMatchedParameter(matchedItem));
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
                            error = true;
                            errorNums++;
                            logger.error("{} takes {} positional argument ({} given)", matchedItem.getCommandName(), matchedItem.getArity(), length);
                        } else {
                            // 添加捕获组
                            options.add(matchedItem, params.popFirst(length).toArray());
                            logger.info("Match commandItem: {} {}", matchedItem.getCommandName(), options.getMatchedParameter(matchedItem));
                        }
                    }
                }
            }

            if (error) {
                logger.error("Finish matching parameters, {} error{} found", errorNums, errorNums > 1 ? "s" : "");
                return null;
            } else {
                logger.info("Finish matching parameters, {} commandItem{} in total", matchedNums, matchedNums > 1 ? "s" : "");
            }

            // 检查所有 request 参数
            for (CommandItem commandItem : this) {
                if (commandItem.isRequest() && !options.isPassedIn(commandItem)) {
                    error = true;
                    errorNums += 1;
                    logger.error("Missing required positional argument: " + commandItem.getCommandName());
                }
            }

            // 检查参数间的规则
            for (CommandRule rule : this.rules) {
                if (rule.isValidRule() && !rule.check(options)) {
                    error = true;
                    errorNums += 1;
                    logger.error("{}", rule);
                }
            }

            if (error) {
                logger.error("Finish checking parameters, {} error{} found", errorNums, errorNums > 1 ? "s" : "");
                return null;
            } else {
                logger.info("Finish checking parameters");
                logger.info("CommandParser matched and formatted the {} command items. You can use 'options.isPassedIn(commandName)' to determine if the commandItem was passed in, and use 'options.get(commandName)' to get the value of the commandItem.", options.getValues().size());
                return options;
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
                            throw new ParameterException(matchedItem.getCommandName() + " takes " + matchedItem.getArity() + " positional argument (" + params.size() + " given");
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

            // 检查参数间的规则
            for (CommandRule rule : this.rules) {
                if (!rule.check(options)) {
                    throw new ParameterException(rule.toString());
                }
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
            if (isDebug()) {
                logger.error("Program takes at least {} positional argument, but {} given)", this.offset, args.length);
                return null;
            }
            throw new ParameterException("Program takes at least " + this.offset + " positional argument (because offset=" + this.offset + ", but " + args.length + " given)");
        }

        // 检查是否有指令重叠
        if (!checkParser()) {
            if (isDebug()) {
                logger.error("Unable to parse parameters within illegal parser (repeated commandItem)");
                return null;
            }
            throw new CommandParserException("unable to parse parameters within illegal parser (repeated commandItem)");
        }

        // 包装输入的参数信息
        StringArray params = new StringArray(args, this.offset, args.length - this.offset);

        // 查看是否包含 @ 指令，如果包含则将内容解析出来
        if (!parseAtSymbol(params) && isDebug()) {
            return null;
        }

        // 查看是否包含 help 指令, 如果包含 help 指令，则不进行强制的参数解析工作
        boolean passedInHelp = passedInHelp(params);

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
     */
    public CommandOptions parse(File file) {
        try {
            return parse(readFromFile(file));
        } catch (IOException e) {
            if (isDebug()) {
                if (logger.isDebugEnabled()) {
                    logger.debug("{}", e.getMessage(), e);
                } else {
                    logger.error("{}", e.getMessage());
                }
            }

            throw new ParameterException(e.getMessage());
        }
    }

    /**
     * 从文件中读取信息, 并转为可解析指令
     *
     * @param file 文件名
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
     * 获取注册的命令组
     *
     * @param commandName 命令名
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
     * 是否包含该命令
     *
     * @param commandName 命令名
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
     * @return 是否转换成功 (发生错误时, 返回 false)
     */
    private boolean parseAtSymbol(StringArray args) {
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
                            if (isDebug()) {
                                if (logger.isDebugEnabled()) {
                                    logger.debug("Unable to get parameters from {}: {}", arg.substring(1), e.getMessage(), e);
                                } else {
                                    logger.error("Unable to get parameters from {}: {}", arg.substring(1), e.getMessage());
                                }
                                return false;
                            } else {
                                throw new ParameterException("unable to get parameters from " + arg.substring(1) + ": " + e.getMessage());
                            }
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
        return true;
    }

    /**
     * 检查 parser 的可用性 (检查参数)
     */
    private boolean checkParser() {
        if (isDebug()) {
            // 先检查指令名称是否有重复
            boolean error = false;
            int count = 0;
            int effCount = 0;
            HashMap<String, CommandGroup> commandNames = new HashMap<>();
            for (CommandGroup commandGroup : this.groups) {
                logger.info("Create command group: name=\"{}\"", commandGroup.getGroupName());
                for (CommandItem commandItem : commandGroup) {
                    boolean duplicate = false;
                    for (String commandName : commandItem) {
                        if (commandNames.containsKey(commandName)) {
                            duplicate = true;
                            error = true;
                            logger.error("CommandItem \"{}\" in commandGroup \"{}\" is repeated with commandItem \"{}\" in commandGroup \"{}\"",
                                    commandName, commandNames.get(commandName).getGroupName(), commandName, commandGroup.getGroupName());
                            break;
                        } else {
                            commandNames.put(commandName, commandGroup);
                        }
                    }

                    count++;
                    if (!duplicate) {
                        effCount++;
                        logger.info("Register commandItem \"{}\" to commandGroup \"{}\": {}", commandItem.getCommandName(), commandGroup.getGroupName(), commandItem);
                    } else {
                        logger.error("Register commandItem \"{}\" to commandGroup \"{}\": {}", commandItem.getCommandName(), commandGroup.getGroupName(), commandItem);
                    }
                }
            }

            if (count == effCount) {
                logger.info("All commandItems were registered, {} in total", count);
            } else {
                logger.warn("All commandItems were registered, {} in total, {} are valid", count, effCount);
            }
            commandNames.clear();
            commandNames = null;

            if (error) {
                return false;
            }
        } else {
            // 先检查指令名称是否有重复
            HashSet<String> commandNames = new HashSet<>();
            for (CommandItem commandItem : this) {
                for (String commandName : commandItem) {
                    if (commandNames.contains(commandName)) {
                        return false;
                    } else {
                        commandNames.add(commandName);
                    }
                }
            }

            commandNames.clear();
            commandNames = null;
        }

        return true;
    }

    /**
     * 检查是否为 help 模式
     */
    private boolean passedInHelp(StringArray args) {
        int matchedNums = 0;
        if (isDebug()) {
            for (String arg : args) {
                CommandItem matchedItem = getCommandItem(arg);
                if (matchedItem != null) {
                    matchedNums += 1;

                    if (matchedItem.isHelp()) {
                        logger.info("\"HELP\" parameter is passed in, the current parser runs without parameter checking and format conversion");
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
     */
    public Iterator<CommandGroup> groupIterator() {
        return this.groups.iterator();
    }

    /**
     * 参数组迭代器
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
     */
    public int numOfCommandRules() {
        return this.rules.size();
    }

    /**
     * 参数组的个数
     */
    public int numOfCommandGroups() {
        return this.groups.size();
    }
}

