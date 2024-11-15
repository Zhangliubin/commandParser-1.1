package edu.sysu.pmglab.commandParser.types;

import edu.sysu.pmglab.commandParser.exception.CommandParserException;
import edu.sysu.pmglab.commandParser.exception.ParameterException;
import edu.sysu.pmglab.container.Interval;
import edu.sysu.pmglab.container.array.StringArray;

import java.util.*;
import java.util.function.Function;
import java.util.regex.Pattern;

/**
 * string 及其扩展类型
 */

public enum STRING implements IType {
    /**
     * 值转换器
     * <p>
     * 输入格式: &lt;String&gt;
     * <p>
     * 转换格式: String
     */
    VALUE((Function<String[], String>) strings -> convertToString(strings[0]), null, 1, "<string>"),

    /**
     * array 值转换器
     * <p>
     * 输入格式: &lt;String&gt; &lt;String&gt; ...
     * <p>
     * 转换格式: String[]
     */
    ARRAY((Function<String[], String[]>) strings -> {
        int index = 0;
        String[] values = new String[strings.length];
        for (String string : strings) {
            values[index++] = convertToString(string);
        }
        return values;
    }, null, -1, "<string> <string> ..."),

    /**
     * array 值转换器
     * <p>
     * 输入格式: &lt;String&gt;,&lt;String&gt;,...
     * <p>
     * 转换格式: String[]
     */
    ARRAY_COMMA((Function<String[], String[]>) strings -> (String[]) ARRAY.convert(strings[0].split(",")), null, 1, "<string>,<string>,..."),

    /**
     * array 值转换器
     * <p>
     * 输入格式: &lt;String&gt;;&lt;String&gt;;...
     * <p>
     * 转换格式: String[]
     */
    ARRAY_SEMICOLON((Function<String[], String[]>) strings -> (String[]) ARRAY.convert(strings[0].split(";")), null, 1, "<string>;<string>;..."),

    /**
     * set 值转换器
     * <p>
     * 输入格式: &lt;String&gt; &lt;String&gt; ...
     * <p>
     * 转换格式: Set&lt;String&gt;
     */
    SET((Function<String[], Set<String>>) strings -> {
        HashSet<String> values = new LinkedHashSet<>(2);
        for (String string : strings) {
            values.add(convertToString(string));
        }
        return Collections.unmodifiableSet(values);
    }, null, -1, "<string> <string> ..."),

    /**
     * set 值转换器
     * <p>
     * 输入格式: &lt;String&gt;,&lt;String&gt;,...
     * <p>
     * 转换格式: Set&lt;String&gt;
     */
    @SuppressWarnings("unchecked")
    SET_COMMA((Function<String[], Set<String>>) strings -> (Set<String>) SET.convert(strings[0].split(",")), null, 1, "<string>,<string>,..."),

    /**
     * set 值转换器
     * <p>
     * 输入格式: &lt;String&gt;;&lt;String&gt;;...
     * <p>
     * 转换格式: Set&lt;String&gt;
     */
    @SuppressWarnings("unchecked")
    SET_SEMICOLON((Function<String[], Set<String>>) strings -> (Set<String>) SET.convert(strings[0].split(";")), null, 1, "<string>;<string>;..."),

    /**
     * map 值转换器
     * <p>
     * 输入格式: &lt;string&gt;=&lt;String&gt; &lt;string&gt;=&lt;String&gt; ...
     * <p>
     * 转换格式: Map&lt;String, String&gt;
     */
    MAP((Function<String[], Map<String, String>>) strings -> {
        Map<String, String> maps = new LinkedHashMap<>(strings.length);
        for (String string : strings) {
            if (string.length() > 0) {
                int index = string.indexOf("=");
                if (index == -1) {
                    // K, 则 V 默认为 null
                    maps.put(string, null);
                } else {
                    String key = string.substring(0, index);
                    if (maps.containsKey(key)) {
                        throw new ParameterException("key " + key + " is set repeatedly");
                    }

                    maps.put(key, convertToString(string.substring(index + 1)));
                }
            }
        }
        return Collections.unmodifiableMap(maps);
    }, null, -1, "<string>=<string> <string>=<string> ..."),

    /**
     * map 值转换器
     * <p>
     * 输入格式: &lt;string&gt;=&lt;String&gt;,&lt;string&gt;=&lt;String&gt;,...
     * <p>
     * 转换格式: Map&lt;String, String&gt;
     */
    @SuppressWarnings("unchecked")
    MAP_COMMA((Function<String[], Map<String, String>>) strings -> (Map<String, String>) MAP.convert(strings[0].split(",")), null, 1, "<string>=<string>,<string>=<string>,..."),

    /**
     * map 值转换器
     * <p>
     * 输入格式: &lt;string&gt;=&lt;String&gt;;&lt;string&gt;=&lt;String&gt;;...
     * <p>
     * 转换格式: Map&lt;String, String&gt;
     */
    @SuppressWarnings("unchecked")
    MAP_SEMICOLON((Function<String[], Map<String, String>>) strings -> (Map<String, String>) MAP.convert(strings[0].split(";")), null, 1, "<string>=<string>;<string>=<string>;..."),

    /**
     * range 值转换器
     * <p>
     * 输入格式: &lt;String&gt;-&lt;String&gt;
     * <p>
     * 转换格式: Interval&lt;Short&gt;
     */
    RANGE((Function<String[], Interval<String>>) strings -> {
        String[] parsed = strings[0].split("-", -1);

        if (parsed.length != 2) {
            throw new ParameterException(strings[0] + " not in <string>-<string> format");
        }
        return new Interval<>(parsed[0], parsed[1]);
    }, null, 1, "<string>-<string>"),

    /**
     * label-range 值转换器
     * <p>
     * 输入格式: &lt;string&gt;:&lt;String&gt;-&lt;String&gt; &lt;string&gt;:&lt;String&gt;-&lt;String&gt; ...
     * <p>
     * 转换格式: Map&lt;String, Interval&lt;Short&gt;&gt;
     */
    @SuppressWarnings("unchecked")
    LABEL_RANGE((Function<String[], Map<String, Interval<String>>>) strings -> {
        Map<String, Interval<String>> values = new LinkedHashMap<>(strings.length);

        for (String string : strings) {
            String[] groups = string.split(":", -1);

            if (groups.length != 2) {
                throw new ParameterException(string + " not in <string>:<string>-<string> format");
            }

            if (values.containsKey(groups[0])) {
                throw new ParameterException("key " + groups[0] + " is set repeatedly");
            }
            values.put(groups[0], (Interval<String>) RANGE.convert(groups[1]));
        }
        return Collections.unmodifiableMap(values);
    }, null, -1, "<string>:<string>-<string> <string>:<string>-<string> ..."),

    /**
     * label-range 值转换器
     * <p>
     * 输入格式: &lt;string&gt;:&lt;String&gt;-&lt;String&gt;,&lt;string&gt;:&lt;String&gt;-&lt;String&gt;,...
     * <p>
     * 转换格式: Map&lt;String, Interval&lt;Short&gt;&gt;
     */
    @SuppressWarnings("unchecked")
    LABEL_RANGE_COMMA((Function<String[], Map<String, Interval<String>>>) strings -> (Map<String, Interval<String>>) LABEL_RANGE.convert(strings[0].split(",")), null, 1, "<string>:<string>-<string>,<string>:<string>-<string>,..."),

    /**
     * label-range 值转换器
     * <p>
     * 输入格式: &lt;string&gt;:&lt;String&gt;-&lt;String&gt;;&lt;string&gt;:&lt;String&gt;-&lt;String&gt;;...
     * <p>
     * 转换格式: Map&lt;String, Interval&lt;Short&gt;&gt;
     */
    @SuppressWarnings("unchecked")
    LABEL_RANGE_SEMICOLON((Function<String[], Map<String, Interval<String>>>) strings -> (Map<String, Interval<String>>) LABEL_RANGE.convert(strings[0].split(";")), null, 1, "<string>:<string>-<string>;<string>:<string>-<string>;..."),

    /**
     * label-array 值转换器
     * <p>
     * 输入格式: &lt;string&gt;:&lt;String&gt;,&lt;String&gt;,... &lt;string&gt;:&lt;String&gt;,&lt;String&gt;,... ...
     * <p>
     * 转换格式: Map&lt;String, String[]&gt;
     */
    LABEL_ARRAY((Function<String[], Map<String, String[]>>) strings -> {
        Map<String, String[]> values = new LinkedHashMap<>(strings.length);

        for (String string : strings) {
            String[] groups = string.split(":", -1);
            if (groups.length != 2) {
                throw new ParameterException(string + " not in <string>:<string>,<string>,... format");
            }

            if (values.containsKey(groups[0])) {
                throw new ParameterException("key " + groups[0] + " is set repeatedly");
            }

            if (groups[1].length() == 0) {
                values.put(groups[0], new String[0]);
            } else {
                values.put(groups[0], (String[]) ARRAY_COMMA.convert(groups[1]));
            }
        }
        return Collections.unmodifiableMap(values);
    }, null, -1, "<string>:<string>,<string>,... <string>:<string>,<string>,... ..."),

    /**
     * label-array 值转换器
     * <p>
     * 输入格式: &lt;string&gt;:&lt;String&gt;,&lt;String&gt;,...;&lt;string&gt;:&lt;String&gt;,&lt;String&gt;,...;...
     * <p>
     * 转换格式: Map&lt;String, String[]&gt;
     */
    @SuppressWarnings("unchecked")
    LABEL_ARRAY_SEMICOLON((Function<String[], Map<String, String[]>>) strings -> (Map<String, String[]>) LABEL_ARRAY.convert(strings[0].split(";")), null, 1, "<string>:<string>,<string>,...;<string>:<string>,<string>,...;...");

    private final Function<String[], ?> converter;
    private final int defaultArity;
    private final Object defaultValue;
    private final String defaultFormat;

    STRING(Function<String[], ?> converter, Object defaultValue, int defaultArity, String defaultFormat) {
        this.converter = converter;
        this.defaultArity = defaultArity;
        this.defaultValue = defaultValue;
        this.defaultFormat = defaultFormat;
    }

    /**
     * 将值转为 String 类型
     */
    private static String convertToString(String value) {
        return value;
    }

    @Override
    public Object convert(String... args) {
        return this.converter.apply(args);
    }

    @Override
    public STRING getBaseValueType() {
        return STRING.VALUE;
    }

    @Override
    public Object getDefaultValue() {
        return this.defaultValue;
    }

    @Override
    public int getDefaultArity() {
        return this.defaultArity;
    }

    @Override
    public String getDefaultFormat() {
        return this.defaultFormat;

    }

    private static final Pattern elementRule = Pattern.compile("^[a-zA-Z0-9+_./]+$");

    /**
     * 值验证器
     *
     * @param elements 支持的元素值
     * @return 可取值验证器
     */
    public static IValidator validateWith(String... elements) {
        return validateWith(true, true, elements);
    }

    /**
     * 值验证器
     *
     * @param ignoreCase  忽略大小写
     * @param indexAccess 允许通过索引进行参数访问
     * @param elements    支持的元素值
     * @return 可取值验证器
     */
    @SuppressWarnings("unchecked")
    public static IValidator validateWith(boolean ignoreCase, boolean indexAccess, String... elements) {
        if (elements == null || elements.length == 0) {
            throw new CommandParserException("illegal validator parameters (elements is empty)");
        }

        String[] indexes = new String[elements.length];
        for (int i = 0; i < elements.length; i++) {
            if (elements[i] == null || elements[i].length() == 0 || !elementRule.matcher(elements[i]).matches()) {
                throw new CommandParserException("invalid syntax: elements (" + elements[i] + ") contain invalid characters");
            }

            indexes[i] = String.valueOf(i);
        }

        return new IValidator() {
            @Override
            public Object validate(String commandKey, Object params) {
                if (params instanceof String) {
                    return convertValue(commandKey, (String) params);
                } else if (params instanceof String[]) {
                    int index = 0;
                    String[] parsed = new String[((String[]) params).length];
                    for (String param : (String[]) params) {
                        parsed[index++] = convertValue(commandKey, param);
                    }
                    return parsed;
                } else if (params instanceof Set<?>) {
                    HashSet<String> parsed = new LinkedHashSet<>();
                    for (String param : (Set<String>) params) {
                        parsed.add(convertValue(commandKey, param));
                    }
                    return Collections.unmodifiableSet(parsed);
                } else if (params instanceof Map<?, ?>) {
                    Map<String, ?> values = (Map<String, ?>) params;
                    if (values.size() == 0) {
                        return params;
                    } else {
                        // 根据第一个值推断类型
                        boolean array = false;
                        for (String key : values.keySet()) {
                            if (values.get(key) instanceof String[]) {
                                array = true;
                            }
                            break;
                        }

                        if (array) {
                            HashMap<String, String[]> parsed = new LinkedHashMap<>();
                            for (String key : values.keySet()) {
                                int index = 0;
                                String[] newStringArray = new String[((String[]) values.get(key)).length];

                                for (String value : (String[]) values.get(key)) {
                                    String parsedValue = convertValue(commandKey, value);
                                    newStringArray[index++] = parsedValue;
                                }

                                parsed.put(key, newStringArray);
                            }
                            return Collections.unmodifiableMap(parsed);
                        } else {
                            HashMap<String, String> parsed = new LinkedHashMap<>();
                            for (String key : values.keySet()) {
                                String parsedValue = convertValue(commandKey, (String) values.get(key));
                                parsed.put(key, parsedValue);
                            }
                            return Collections.unmodifiableMap(parsed);
                        }
                    }
                } else {
                    throw new ParameterException("unable to infer the value type of " + commandKey);
                }
            }

            private String convertValue(String commandKey, String value) {
                // 首先解析字符串
                if (ignoreCase) {
                    for (String element : elements) {
                        if (element.equalsIgnoreCase(value)) {
                            return element;
                        }
                    }
                } else {
                    for (String element : elements) {
                        if (element.equals(value)) {
                            return element;
                        }
                    }
                }

                // 如果都没有找到, 则作为索引值
                if (indexAccess) {
                    for (int i = 0; i < elements.length; i++) {
                        if (indexes[i].equals(value)) {
                            return elements[i];
                        }
                    }

                    throw new ParameterException(commandKey + ": one (or more) of the following values/indexes are supported: " + Arrays.toString(elements));
                } else {
                    throw new ParameterException(commandKey + ": one (or more) of the following values are supported: " + Arrays.toString(elements));
                }

            }

            @Override
            public Object get(String key) {
                if (key.equalsIgnoreCase("ignoreCase")) {
                    return ignoreCase;
                } else if (key.equalsIgnoreCase("indexAccess")) {
                    return indexAccess;
                } else if (key.equalsIgnoreCase("elements")) {
                    return elements.clone();
                } else {
                    throw new CommandParserException("attribute " + key + " not found (support: ignoreCase, indexAccess, elements)");
                }
            }

            @Override
            public STRING getBaseValueType() {
                return STRING.VALUE;
            }

            @Override
            public String toString() {
                String values = StringArray.wrap(elements).join("/");

                if (indexAccess) {
                    String[] range = new String[elements.length];
                    for (int i = 0; i < range.length; i ++) {
                        range[i] = String.valueOf(i);
                    }
                    String indexes = StringArray.wrap(range).join("/");
                    return "[" + values + "] or [" + indexes + "]" + (ignoreCase ? " (ignoreCase)" : "");
                } else {
                    return "[" + values + "]" + (ignoreCase ? " (ignoreCase)" : "");
                }
            }
        };
    }

    @Override
    public String toString() {
        if (this.equals(STRING.VALUE)) {
            return "STRING";
        } else {
            return getDeclaringClass().getSimpleName() + "_" + super.toString();
        }
    }
}