package edu.sysu.pmglab.commandParser;

import edu.sysu.pmglab.commandParser.exception.CommandParserException;
import edu.sysu.pmglab.commandParser.exception.ParameterException;
import edu.sysu.pmglab.commandParser.types.*;
import edu.sysu.pmglab.container.File;
import edu.sysu.pmglab.container.array.StringArray;

import java.util.*;
import java.util.regex.Pattern;

/**
 * 参数项目
 */

public class CommandItem implements Iterable<String>, Cloneable {
    /**
     * 基本属性, 参数名 + 类型声明
     */
    private final String[] commandNames;
    private final IType converter;
    private int arity;

    /**
     * 权限值
     */
    private final HashSet<String> options = new LinkedHashSet<>(4);

    /**
     * 验证器
     */
    private IValidator validator;

    private Object defaultValue;
    private String defaultValueOrigin;
    private String description;
    private String format;

    /**
     * 允许的参数名规则
     */
    private final static Pattern COMMAND_NAME_RULE = Pattern.compile("(^[a-zA-Z0-9+_\\.\\-]+$)");

    /**
     * 必备参数
     */
    public static final String REQUEST = "REQUEST";

    /**
     * 不在文档中显示该参数
     */
    public static final String HIDDEN = "HIDDEN";

    /**
     * 该指令为 help 指令
     */
    public static final String HELP = "HELP";

    /**
     * 该指令为 debug 指令
     */
    public static final String DEBUG = "DEBUG";

    /**
     * 可取值验证器
     */
    private static final Set<String> VALIDATOR = StringArray.wrap(new String[]{REQUEST, HIDDEN, HELP, DEBUG}).toSet();

    /**
     * 构造器方法, 通过一系列不重复参数名构建参数. 其中第一个参数名作为主参数名.
     *
     * @param type         参数类型
     * @param commandNames 该参数的所有参数名
     */
    public CommandItem(IType type, String... commandNames) {
        if (commandNames == null || commandNames.length == 0) {
            throw new CommandParserException("syntax error: commandName(s) is null");
        }

        // 验证参数名是否合法、参数名不可重复
        for (String commandName : commandNames) {
            if (!checkCommandName(commandName)) {
                throw new CommandParserException("syntax error: commandName(" + commandName + ") contains invalid characters");
            }
        }

        // 检查类型 (只允许基本类型)
        if (!IType.checkType(type)) {
            throw new CommandParserException("syntax error: unsupported command type " + type);
        }

        // 设置参数名列表
        this.commandNames = commandNames.clone();
        this.converter = type;
        this.arity = type.getDefaultArity();
        this.defaultValue = type.getDefaultValue();
        this.defaultValueOrigin = null;
        this.format = this.arity == 0 ? "" : commandNames[0] + " " + type.getDefaultFormat();
        this.description = "";
    }

    /**
     * 构造器方法, 通过一系列不重复参数名构建参数. 其中第一个参数名作为主参数名.
     *
     * @param tClass       参数类型
     * @param commandNames 该参数的所有参数名
     */
    public CommandItem(Class<?> tClass, String... commandNames) {
        if (commandNames == null || commandNames.length == 0) {
            throw new CommandParserException("syntax error: commandName(s) is null");
        }

        // 验证参数名是否合法、参数名不可重复
        for (String commandName : commandNames) {
            if (!checkCommandName(commandName)) {
                throw new CommandParserException("syntax error: commandName(" + commandName + ") contains invalid characters");
            }
        }

        // 检查类型 (只允许基本类型)
        IType type;
        if (tClass == null) {
            type = IType.NONE;
        } else if (tClass.equals(File.class)) {
            type = FILE.VALUE;
        } else if (tClass.equals(Boolean.class)) {
            type = BOOLEAN.VALUE;
        } else if (tClass.equals(Byte.class)) {
            type = BYTE.VALUE;
        } else if (tClass.equals(Short.class)) {
            type = SHORT.VALUE;
        } else if (tClass.equals(Integer.class)) {
            type = INTEGER.VALUE;
        } else if (tClass.equals(Long.class)) {
            type = LONG.VALUE;
        } else if (tClass.equals(Float.class)) {
            type = FLOAT.VALUE;
        } else if (tClass.equals(Double.class)) {
            type = DOUBLE.VALUE;
        } else if (tClass.equals(String.class)) {
            type = STRING.VALUE;
        } else if (tClass.equals(File[].class)) {
            type = FILE.ARRAY;
        } else if (tClass.equals(boolean[].class) || tClass.equals(Boolean[].class)) {
            type = BOOLEAN.ARRAY;
        } else if (tClass.equals(byte[].class) || tClass.equals(Byte[].class)) {
            type = BYTE.ARRAY;
        } else if (tClass.equals(short[].class) || tClass.equals(Short[].class)) {
            type = SHORT.ARRAY;
        } else if (tClass.equals(int[].class) || tClass.equals(Integer[].class)) {
            type = INTEGER.ARRAY;
        } else if (tClass.equals(long[].class) || tClass.equals(Long[].class)) {
            type = LONG.ARRAY;
        } else if (tClass.equals(float[].class) || tClass.equals(Float[].class)) {
            type = FLOAT.ARRAY;
        } else if (tClass.equals(double[].class) || (tClass.equals(Double[].class))) {
            type = DOUBLE.ARRAY;
        } else if (tClass.equals(String[].class)) {
            type = STRING.ARRAY;
        } else {
            throw new CommandParserException("syntax error: unable to convert " + tClass + " to IType");
        }

        // 设置参数名列表
        this.commandNames = commandNames.clone();
        this.converter = type;
        this.arity = type.getDefaultArity();
        this.defaultValue = type.getDefaultValue();
        this.defaultValueOrigin = null;
        this.format = this.arity == 0 ? "" : commandNames[0] + " " + type.getDefaultFormat();
        this.description = "";
    }

    /**
     * 添加参数 HIDDEN, HELP, REQUEST, DEBUG
     *
     * @param options 添加的参数
     * @return 本参数项
     */
    public CommandItem addOptions(String... options) {
        if (options != null) {
            for (String option : options) {
                if (!VALIDATOR.contains(option)) {
                    throw new CommandParserException("syntax error: command item's option only support one of " + VALIDATOR);
                }

                this.options.add(option);
            }
        }

        return this;
    }

    /**
     * 删除参数 HIDDEN, HELP, REQUEST, DEBUG
     *
     * @param options 添加的参数
     * @return 本参数项
     */
    public CommandItem removeOptions(String... options) {
        if (options != null) {
            for (String option : options) {
                if (!VALIDATOR.contains(option)) {
                    throw new CommandParserException("syntax error: command item's option only support one of " + VALIDATOR);
                }

                this.options.remove(option);
            }
        }

        return this;
    }

    /**
     * 设置参数长度
     * 只有不定长参数可以主动调用参数长度设置方法
     *
     * @param length 参数长度
     * @return 本参数项
     */
    public CommandItem arity(int length) {
        if (length == this.arity) {
            // 一致，则不进行更改
            return this;
        }

        if (this.converter.getDefaultArity() == -1) {
            // 只有长度为 -1 的类型才可以进行修改
            if (length < -1) {
                throw new CommandParserException("syntax error: arity must be equal to -1 (means variable length) or a non-negative integer");
            }

            this.arity = length;
        } else {
            throw new CommandParserException("syntax error: converter(" + this.converter + ") does not support setting the length of the matched command item");
        }

        return this;
    }

    /**
     * 设置默认值 (使用字符串作为输入, 并按照格式转换器转为对应的值)
     *
     * @param defaultValue 默认值
     * @return 本参数项
     */
    public CommandItem defaultTo(String... defaultValue) {
        if (defaultValue == null) {
            this.defaultValue = this.converter.getDefaultValue();
            this.defaultValueOrigin = null;
            return this;
        }

        if (defaultValue.length == 0 || (defaultValue.length == 1 && defaultValue[0] == null)) {
            this.defaultValue = this.converter.getDefaultValue();
            this.defaultValueOrigin = null;
        } else {
            for (String value : defaultValue) {
                if (value == null) {
                    throw new CommandParserException("syntax error: defaultValue cannot be null");
                }

                if (value.length() == 0) {
                    throw new CommandParserException("syntax error: " + value + " is not a legal parameter (is empty, or contains a blank value)");
                }
            }

            try {
                this.defaultValue = parseValue(defaultValue);
            } catch (ParameterException e) {
                throw new CommandParserException("syntax error: " + e.getMessage());
            }

            this.defaultValueOrigin = String.join(" ", defaultValue);
        }
        return this;
    }

    /**
     * 设置布尔类型默认值 (该方法尝试进行格式转换为字符串, 但不一定能成功)
     *
     * @param values 默认值对象
     * @return 本参数项
     */
    public CommandItem defaultTo(Boolean... values) {
        if (values == null) {
            this.defaultValue = this.converter.getDefaultValue();
            this.defaultValueOrigin = null;
            return this;
        }

        int index = 0;
        String[] arrays = new String[values.length];
        for (Boolean value : values) {
            if (value == null) {
                throw new CommandParserException("syntax error: defaultValue cannot be null");
            }

            arrays[index++] = value.toString();
        }
        return defaultTo(arrays);
    }

    /**
     * 设置数值类型默认值 (该方法尝试进行格式转换为字符串, 但不一定能成功)
     *
     * @param values 默认值对象
     * @return 本参数项
     */
    public CommandItem defaultTo(Number... values) {
        if (values == null) {
            this.defaultValue = this.converter.getDefaultValue();
            this.defaultValueOrigin = null;
            return this;
        }

        int index = 0;
        String[] arrays = new String[values.length];
        for (Number value : values) {
            if (value == null) {
                throw new CommandParserException("syntax error: defaultValue cannot be null");
            }

            arrays[index++] = value.toString();
        }
        return defaultTo(arrays);
    }

    /**
     * 设置默认值 (该方法尝试进行格式转换为字符串, 但不一定能成功)
     *
     * @param values 默认值对象
     * @return 本参数项
     */
    public CommandItem defaultTo(File... values) {
        if (values == null) {
            this.defaultValue = this.converter.getDefaultValue();
            this.defaultValueOrigin = null;
            return this;
        }

        int index = 0;
        String[] arrays = new String[values.length];
        for (File value : values) {
            if (value == null) {
                throw new CommandParserException("syntax error: defaultValue cannot be null");
            }

            arrays[index++] = value.toString();
        }
        return defaultTo(arrays);
    }

    /**
     * 参数验证器
     *
     * @param validator 从类型获得的验证器
     * @return 本参数项
     */
    public CommandItem validateWith(IValidator validator) {
        if (validator == null) {
            this.validator = null;
            return this;
        }

        if (!validator.getBaseValueType().equals(this.converter.getBaseValueType())) {
            throw new CommandParserException("syntax error: validator and converter based on different data type (" + validator.getBaseValueType() + " and " + this.converter.getBaseValueType() + ")");
        }

        IValidator oldValidator = this.validator;
        this.validator = validator;

        if (this.defaultValueOrigin != null) {
            try {
                this.defaultValue = parseValue(this.defaultValueOrigin.split(" "));
            } catch (ParameterException e) {
                this.validator = oldValidator;
                throw new CommandParserException("illegal validator: the current validator cannot validate the default value, please change the default value or the validator");
            }
        }

        return this;
    }

    /**
     * 设置描述文档
     *
     * @param description 描述信息
     * @return 本参数项
     */
    public CommandItem setDescription(String description) {
        if (description == null || description.length() == 0) {
            this.description = "";
        } else {
            if (description.contains("\t") || description.contains("\n")) {
                throw new CommandParserException("syntax error: format contains invalid characters (\\t or \\n)");
            }

            this.description = description;
        }
        return this;
    }

    /**
     * 设置参数描述
     *
     * @param format 输入格式
     * @return 本参数项
     */
    public CommandItem setFormat(String format) {
        if (format == null || format.length() == 0) {
            this.format = "";
        } else {
            if (format.contains("\t") || format.contains("\n")) {
                throw new CommandParserException("syntax error: format contains invalid characters (\\t or \\n)");
            }

            this.format = format;
        }

        // 只要调用了该语句, 格式就被修改了
        return this;
    }

    /**
     * 获取第一个参数名
     *
     * @return 主参数名
     */
    public String getCommandName() {
        return this.commandNames[0];
    }

    /**
     * 获取参数的名字列表
     *
     * @return 参数名列表
     */
    public String[] getCommandNames() {
        return Arrays.copyOfRange(this.commandNames, 0, this.commandNames.length);
    }

    /**
     * 获取参数的名字列表, 并使用特定符号包装和连接
     *
     * @param separator 分隔符号
     * @param wrapper   参数名左右包装器
     * @return 连接的参数名信息
     */
    public String linkCommandNamesBy(String separator, String wrapper) {
        if (wrapper == null) {
            return StringArray.wrap(this.commandNames).join(separator);
        } else {
            return ((StringArray) StringArray.wrap(this.commandNames).apply(s -> wrapper + s + wrapper)).join(separator);
        }
    }

    /**
     * 获取参数的名字列表, 并使用特定符号连接
     *
     * @param separator 分隔符号
     * @return 连接的参数名信息
     */
    public String linkCommandNamesBy(String separator) {
        return linkCommandNamesBy(separator, null);
    }

    /**
     * 获取转换器
     *
     * @return 参数格式类型
     */
    public IType getConverter() {
        return this.converter;
    }

    /**
     * 获取参数权限表
     *
     * @return 参数权限信息
     */
    public Set<String> getOptions() {
        return Collections.unmodifiableSet(this.options);
    }

    /**
     * 是否为 help 模式
     *
     * @return 是否为 help 模式
     */
    public boolean isHelp() {
        return this.options.contains(HELP);
    }

    /**
     * 是否为隐藏参数
     *
     * @return 是否为隐藏参数
     */
    public boolean isHide() {
        return this.options.contains(HIDDEN);
    }

    /**
     * 是否为必备参数
     *
     * @return 是否为必备参数
     */
    public boolean isRequest() {
        return this.options.contains(REQUEST);
    }

    /**
     * 是否为调试参数
     *
     * @return 是否为调试参数
     */
    public boolean isDebug() {
        return this.options.contains(DEBUG);
    }

    /**
     * 获取验证器
     *
     * @return 参数验证器
     */
    public IValidator getValidator() {
        return this.validator;
    }

    /**
     * 获取参数描述
     *
     * @return 参数描述信息
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * 获取参数描述
     *
     * @return 参数描述信息
     */
    public String getFormat() {
        return this.format;
    }

    /**
     * 获取默认的参数描述
     *
     * @return 默认的参数描述信息
     */
    String getDefaultFormat() {
        return this.arity == 0 ? "" : commandNames[0] + " " + converter.getDefaultFormat();
    }

    /**
     * 获取参数长度
     *
     * @return 参数长度
     */
    public int getArity() {
        return this.arity;
    }

    /**
     * 获取默认值
     *
     * @return 获取默认值, 类型为 Obj 类型
     */
    public Object getDefaultValue() {
        return this.defaultValue;
    }

    /**
     * 获取默认值
     *
     * @return 获取默认值的字符串格式
     */
    public String getDefaultValueOriginFormat() {
        return this.defaultValueOrigin;
    }

    /**
     * 解析参数值
     *
     * @param params 该参数项的捕获值 (字符串)
     * @return 解析值结果, 经过值转换器和验证器校验
     */
    Object parseValue(String... params) {
        if (params == null || (this.arity != -1 && this.arity != params.length)) {
            throw new ParameterException("commandItem(" + this.commandNames[0] + ") takes " + this.arity + " positional argument (" + params.length + " given)");
        }

        for (String param : params) {
            if (param == null || param.length() == 0 || param.contains("\t") || param.contains("\n")) {
                throw new ParameterException(param + " is not a legal parameter (is empty, or contains a blank value)");
            }
        }

        try {
            Object value = this.converter.convert(params);

            if (this.validator != null) {
                value = this.validator.validate(commandNames[0], value);
            }

            return value;
        } catch (ParameterException e) {
            throw new ParameterException("commandItem(" + this.commandNames[0] + ") parsed value error: " + e.getMessage());
        }
    }

    /**
     * 检查参数名是否合法
     *
     * @param commandName 参数名
     * @return 是否为合法参数名
     */
    private static boolean checkCommandName(String commandName) {
        return commandName != null && commandName.length() != 0 && COMMAND_NAME_RULE.matcher(commandName).matches();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CommandItem that = (CommandItem) o;
        return arity == that.arity && Arrays.equals(commandNames, that.commandNames) && converter.equals(that.converter) && options.equals(that.options) && validator.equals(that.validator) && defaultValue.equals(that.defaultValue) && defaultValueOrigin.equals(that.defaultValueOrigin) && description.equals(that.description) && format.equals(that.format);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(converter, arity, options, validator, defaultValue, defaultValueOrigin, description, format);
        result = 31 * result + Arrays.hashCode(commandNames);
        return result;
    }

    @Override
    public CommandItem clone() {
        CommandItem item = new CommandItem(this.converter, this.commandNames);
        item.validator = this.validator;
        item.arity = this.arity;
        item.options.addAll(this.options);
        item.defaultValue = this.defaultValue;
        item.defaultValueOrigin = this.defaultValueOrigin;
        item.description = this.description;
        item.format = this.format;
        return item;
    }

    @Override
    public Iterator<String> iterator() {
        return new Iterator<String>() {
            int pointer;

            @Override
            public boolean hasNext() {
                return pointer < commandNames.length;
            }

            @Override
            public String next() {
                return commandNames[pointer++];
            }
        };
    }

    @Override
    public String toString() {
        return "name=" + ((StringArray) StringArray.wrap(this.commandNames).apply(s -> "\"" + s + "\"")).join(",") + "; " +
                "arity=" + this.arity + "; " +
                "type=" + this.converter + "; " +
                (this.validator == null ? "" : "validator=\"" + this.validator + "\"; ") +
                (this.arity == 0 ? "" : "format=\"" + this.format + "\"; ") +
                (this.options.size() == 0 ? "" : "option=" + StringArray.wrap(this.options.toArray(new String[0])).join(",") + "\"; ");
    }
}

