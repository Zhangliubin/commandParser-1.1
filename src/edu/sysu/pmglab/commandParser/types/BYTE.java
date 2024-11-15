package edu.sysu.pmglab.commandParser.types;

import edu.sysu.pmglab.commandParser.exception.CommandParserException;
import edu.sysu.pmglab.commandParser.exception.ParameterException;
import edu.sysu.pmglab.container.Interval;
import edu.sysu.pmglab.easytools.ArrayUtils;
import edu.sysu.pmglab.easytools.Assert;

import java.util.*;
import java.util.function.Function;

/**
 * byte 及其扩展类型
 */

public enum BYTE implements IType {
    /**
     * 值转换器
     * <p>
     * 输入格式: &lt;byte&gt;
     * <p>
     * 转换格式: Byte
     */
    VALUE((Function<String[], Byte>) strings -> convertToByte(strings[0]), (byte) 0, 1, "<byte>"),

    /**
     * array 值转换器
     * <p>
     * 输入格式: &lt;byte&gt; &lt;byte&gt; ...
     * <p>
     * 转换格式: byte[]
     */
    ARRAY((Function<String[], byte[]>) strings -> {
        int index = 0;
        byte[] values = new byte[strings.length];
        for (String string : strings) {
            values[index++] = convertToByte(string);
        }
        return values;
    }, null, -1, "<byte> <byte> ..."),

    /**
     * array 值转换器
     * <p>
     * 输入格式: &lt;byte&gt;,&lt;byte&gt;,...
     * <p>
     * 转换格式: byte[]
     */
    ARRAY_COMMA((Function<String[], byte[]>) strings -> (byte[]) ARRAY.convert(strings[0].split(",")), null, 1, "<byte>,<byte>,..."),

    /**
     * array 值转换器
     * <p>
     * 输入格式: &lt;byte&gt;;&lt;byte&gt;;...
     * <p>
     * 转换格式: byte[]
     */
    ARRAY_SEMICOLON((Function<String[], byte[]>) strings -> (byte[]) ARRAY.convert(strings[0].split(";")), null, 1, "<byte>;<byte>;..."),

    /**
     * set 值转换器
     * <p>
     * 输入格式: &lt;byte&gt; &lt;byte&gt; ...
     * <p>
     * 转换格式: Set&lt;Byte&gt;
     */
    SET((Function<String[], Set<Byte>>) strings -> {
        Set<Byte> values = new LinkedHashSet<>(2);
        for (String string : strings) {
            values.add(convertToByte(string));
        }
        return Collections.unmodifiableSet(values);
    }, null, -1, "<byte> <byte> ..."),

    /**
     * set 值转换器
     * <p>
     * 输入格式: &lt;byte&gt;,&lt;byte&gt;,...
     * <p>
     * 转换格式: Set&lt;Byte&gt;
     */
    @SuppressWarnings("unchecked")
    SET_COMMA((Function<String[], Set<Byte>>) strings -> (Set<Byte>) SET.convert(strings[0].split(",")), null, 1, "<byte>,<byte>,..."),

    /**
     * set 值转换器
     * <p>
     * 输入格式: &lt;byte&gt;;&lt;byte&gt;;...
     * <p>
     * 转换格式: Set&lt;Byte&gt;
     */
    @SuppressWarnings("unchecked")
    SET_SEMICOLON((Function<String[], Set<Byte>>) strings -> (Set<Byte>) SET.convert(strings[0].split(";")), null, 1, "<byte>;<byte>;..."),

    /**
     * map 值转换器
     * <p>
     * 输入格式: &lt;string&gt;=&lt;byte&gt; &lt;string&gt;=&lt;byte&gt; ...
     * <p>
     * 转换格式: Map&lt;String, Byte&gt;
     */
    MAP((Function<String[], Map<String, Byte>>) strings -> {
        Map<String, Byte> maps = new LinkedHashMap<>(strings.length);
        for (String string : strings) {
            if (string.length() > 0) {
                String[] groups = string.split("=", -1);

                if (groups.length == 2) {
                    // K=V 形式
                    if (maps.containsKey(groups[0])) {
                        throw new ParameterException("key " + groups[0] + " is set repeatedly");
                    }

                    maps.put(groups[0], convertToByte(groups[1]));
                } else {
                    throw new ParameterException(string + " not in <string>=<byte> format");
                }
            }
        }
        return Collections.unmodifiableMap(maps);
    }, null, -1, "<string>=<byte> <string>=<byte> ..."),

    /**
     * map 值转换器
     * <p>
     * 输入格式: &lt;string&gt;=&lt;byte&gt;,&lt;string&gt;=&lt;byte&gt;,...
     * <p>
     * 转换格式: Map&lt;String, Byte&gt;
     */
    @SuppressWarnings("unchecked")
    MAP_COMMA((Function<String[], Map<String, Byte>>) strings -> (Map<String, Byte>) MAP.convert(strings[0].split(",")), null, 1, "<string>=<byte>,<string>=<byte>,..."),

    /**
     * map 值转换器
     * <p>
     * 输入格式: &lt;string&gt;=&lt;byte&gt;;&lt;string&gt;=&lt;byte&gt;;...
     * <p>
     * 转换格式: Map&lt;String, Byte&gt;
     */
    @SuppressWarnings("unchecked")
    MAP_SEMICOLON((Function<String[], Map<String, Byte>>) strings -> (Map<String, Byte>) MAP.convert(strings[0].split(";")), null, 1, "<string>=<byte>;<string>=<byte>;..."),

    /**
     * range 值转换器
     * <p>
     * 输入格式: &lt;byte&gt;-&lt;byte&gt;
     * <p>
     * 转换格式: Interval&lt;Byte&gt;
     */
    RANGE((Function<String[], Interval<Byte>>) strings -> {
        int count = ArrayUtils.valueCounts(strings[0], '-');
        if (count == 1) {
            // v1-v2 型号
            String[] parsed = strings[0].split("-", -1);
            return new Interval<>(parsed[0].length() == 0 ? null : convertToByte(parsed[0]),
                    parsed[1].length() == 0 ? null : convertToByte(parsed[1]));
        } else if (count == 2) {
            if (strings[0].length() == 2) {
                // --
                throw new ParameterException("unable convert -- to <byte>-<byte>");
            }

            if (strings[0].charAt(0) == '-') {
                if (strings[0].charAt(1) == '-') {
                    // --v1
                    return new Interval<>(null, convertToByte(strings[0].substring(1)));
                } else if (strings[0].charAt(strings[0].length() - 1) == '-') {
                    // -v1-
                    return new Interval<>(convertToByte(strings[0].substring(0, strings[0].length() - 1)), null);
                } else {
                    // -v1-v2
                    int index = strings[0].indexOf('-', 1);
                    return new Interval<>(convertToByte(strings[0].substring(0, index)), convertToByte(strings[0].substring(index + 1)));
                }
            } else {
                // v1--v2
                int index = strings[0].indexOf("--");
                if (index != -1) {
                    return new Interval<>(convertToByte(strings[0].substring(0, index)), convertToByte(strings[0].substring(index + 1)));
                }
            }
        } else if (count == 3 && strings[0].charAt(0) == '-' && strings[0].charAt(1) != '-') {
            // -v1--v2
            int index = strings[0].indexOf("--");
            if (index != -1) {
                return new Interval<>(convertToByte(strings[0].substring(0, index)), convertToByte(strings[0].substring(index + 1)));
            }
        }

        throw new ParameterException("unable convert " + strings[0] + " to <byte>-<byte>");
    }, null, 1, "<byte>-<byte>"),

    /**
     * label-range 值转换器
     * <p>
     * 输入格式: &lt;string&gt;:&lt;byte&gt;-&lt;byte&gt; &lt;string&gt;:&lt;byte&gt;-&lt;byte&gt; ...
     * <p>
     * 转换格式: Map&lt;String, Interval&lt;Byte&gt;&gt;
     */
    @SuppressWarnings("unchecked")
    LABEL_RANGE((Function<String[], Map<String, Interval<Byte>>>) strings -> {
        Map<String, Interval<Byte>> values = new LinkedHashMap<>(strings.length);

        for (String string : strings) {
            String[] groups = string.split(":", -1);

            if (groups.length != 2) {
                throw new ParameterException(string + " not in <string>:<byte>-<byte> format");
            }

            if (values.containsKey(groups[0])) {
                throw new ParameterException("key " + groups[0] + " is set repeatedly");
            }

            values.put(groups[0], (Interval<Byte>) RANGE.convert(groups[1]));
        }
        return Collections.unmodifiableMap(values);
    }, null, -1, "<string>:<byte>-<byte> <string>:<byte>-<byte> ..."),

    /**
     * label-range 值转换器
     * <p>
     * 输入格式: &lt;string&gt;:&lt;byte&gt;-&lt;byte&gt;,&lt;string&gt;:&lt;byte&gt;-&lt;byte&gt;,...
     * <p>
     * 转换格式: Map&lt;String, Interval&lt;Byte&gt;&gt;
     */
    @SuppressWarnings("unchecked")
    LABEL_RANGE_COMMA((Function<String[], Map<String, Interval<Byte>>>) strings -> (Map<String, Interval<Byte>>) LABEL_RANGE.convert(strings[0].split(",")), null, 1, "<string>:<byte>-<byte>,<string>:<byte>-<byte>,..."),

    /**
     * label-range 值转换器
     * <p>
     * 输入格式: &lt;string&gt;:&lt;byte&gt;-&lt;byte&gt;;&lt;string&gt;:&lt;byte&gt;-&lt;byte&gt;;...
     * <p>
     * 转换格式: Map&lt;String, Interval&lt;Byte&gt;&gt;
     */
    @SuppressWarnings("unchecked")
    LABEL_RANGE_SEMICOLON((Function<String[], Map<String, Interval<Byte>>>) strings -> (Map<String, Interval<Byte>>) LABEL_RANGE.convert(strings[0].split(";")), null, 1, "<string>:<byte>-<byte>;<string>:<byte>-<byte>;..."),

    /**
     * label-array 值转换器
     * <p>
     * 输入格式: &lt;string&gt;:&lt;byte&gt;,&lt;byte&gt;,... &lt;string&gt;:&lt;byte&gt;,&lt;byte&gt;,... ...
     * <p>
     * 转换格式: Map&lt;String, byte[]&gt;
     */
    LABEL_ARRAY((Function<String[], Map<String, byte[]>>) strings -> {
        Map<String, byte[]> values = new LinkedHashMap<>(strings.length);

        for (String string : strings) {
            String[] groups = string.split(":", -1);

            if (groups.length != 2) {
                throw new ParameterException(string + " not in <string>:<byte>,<byte>,... format");
            }

            if (values.containsKey(groups[0])) {
                throw new ParameterException("key " + groups[0] + " is set repeatedly");
            }

            if (groups[1].length() == 0) {
                values.put(groups[0], new byte[0]);
            } else {
                values.put(groups[0], (byte[]) ARRAY_COMMA.convert(groups[1]));
            }
        }
        return Collections.unmodifiableMap(values);
    }, null, -1, "<string>:<byte>,<byte>,... <string>:<byte>,<byte>,... ..."),

    /**
     * label-array 值转换器
     * <p>
     * 输入格式: &lt;string&gt;:&lt;byte&gt;,&lt;byte&gt;,...;&lt;string&gt;:&lt;byte&gt;,&lt;byte&gt;,...;...
     * <p>
     * 转换格式: Map&lt;String, byte[]&gt;
     */
    @SuppressWarnings("unchecked")
    LABEL_ARRAY_SEMICOLON((Function<String[], Map<String, byte[]>>) strings -> (Map<String, byte[]>) LABEL_ARRAY.convert(strings[0].split(";")), null, 1, "<string>:<byte>,<byte>,...;<string>:<byte>,<byte>,...;...");

    private final Function<String[], ?> converter;
    private final int defaultArity;
    private final Object defaultValue;
    private final String defaultFormat;

    BYTE(Function<String[], ?> converter, Object defaultValue, int defaultArity, String defaultFormat) {
        this.converter = converter;
        this.defaultArity = defaultArity;
        this.defaultValue = defaultValue;
        this.defaultFormat = defaultFormat;
    }

    /**
     * 将值转为 byte 类型
     */
    private static byte convertToByte(String value) {
        try {
            return Byte.parseByte(value);
        } catch (NumberFormatException e) {
            throw new ParameterException("unable convert " + value + " to a byte value");
        }
    }

    @Override
    public Object convert(String... args) {
        return this.converter.apply(args);
    }

    @Override
    public BYTE getBaseValueType() {
        return BYTE.VALUE;
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

    /**
     * 值验证器
     *
     * @param minValue 最小值
     * @param maxValue 最大值
     * @return 数值范围验证器
     */
    public static IValidator validateWith(byte minValue, byte maxValue) {
        Assert.that(minValue <= maxValue);

        return new IValidator() {
            @Override
            @SuppressWarnings("unchecked")
            public Object validate(String commandKey, Object params) {
                if (params instanceof Byte) {
                    byte value = (byte) params;
                    if (value < minValue || value > maxValue) {
                        throw new ParameterException(commandKey + " is out of range [" + minValue + ", " + maxValue + "]");
                    }
                    return value;
                } else if (params instanceof byte[]) {
                    byte[] values = (byte[]) params;
                    for (byte value : values) {
                        if (value < minValue || value > maxValue) {
                            throw new ParameterException(commandKey + " is out of range [" + minValue + ", " + maxValue + "]");
                        }
                    }
                    return values;
                } else if (params instanceof Set<?>) {
                    Set<Byte> values = (Set<Byte>) params;
                    for (byte value : values) {
                        if (value < minValue || value > maxValue) {
                            throw new ParameterException(commandKey + " is out of range [" + minValue + ", " + maxValue + "]");
                        }
                    }
                    return values;
                } else if (params instanceof Interval<?>) {
                    Interval<Byte> values = (Interval<Byte>) params;
                    if (values.nullity()) {
                        throw new ParameterException("the interval " + values + " is invalid");
                    }

                    if (values.start() == null && values.end() == null) {
                        values = new Interval<>(minValue, maxValue);
                    } else if (values.start() == null) {
                        if (values.end() > maxValue) {
                            throw new ParameterException(commandKey + " is out of range [" + minValue + ", " + maxValue + "]");
                        }

                        values = new Interval<>(minValue, values.end());
                    } else if (values.end() == null) {
                        if (values.start() < minValue) {
                            throw new ParameterException(commandKey + " is out of range [" + minValue + ", " + maxValue + "]");
                        }

                        values = new Interval<>(values.start(), maxValue);
                    } else {
                        if (values.start() < minValue || values.end() > maxValue) {
                            throw new ParameterException(commandKey + " is out of range [" + minValue + ", " + maxValue + "]");
                        }
                    }
                    return values;
                } else if (params instanceof Map<?, ?>) {
                    Map<String, ?> values = (Map<String, ?>) params;
                    Map<String, Object> parsed = new LinkedHashMap<>();
                    for (String key : values.keySet()) {
                        parsed.put(key, validate(commandKey, values.get(key)));
                    }
                    return parsed;
                } else {
                    throw new ParameterException("unable to infer the value type of " + commandKey);
                }
            }

            @Override
            public Byte get(String key) {
                if (key.equalsIgnoreCase("min") || key.equalsIgnoreCase("minValue")) {
                    return minValue;
                } else if (key.equalsIgnoreCase("max") || key.equalsIgnoreCase("maxValue")) {
                    return maxValue;
                } else {
                    throw new CommandParserException("attribute " + key + " not found (support: min, max, minValue, maxValue)");
                }
            }

            @Override
            public BYTE getBaseValueType() {
                return BYTE.VALUE;
            }

            @Override
            public String toString() {
                return minValue + " ~ " + maxValue;
            }
        };
    }

    /**
     * 值验证器
     *
     * @param minValue 最小值
     * @return 数值范围验证器
     */
    public static IValidator validateWith(byte minValue) {
        return new IValidator() {
            @Override
            @SuppressWarnings("unchecked")
            public Object validate(String commandKey, Object params) {
                if (params instanceof Byte) {
                    byte value = (byte) params;
                    if (value < minValue) {
                        throw new ParameterException(commandKey + " less than " + minValue);
                    }
                    return value;
                } else if (params instanceof byte[]) {
                    byte[] values = (byte[]) params;
                    for (byte value : values) {
                        if (value < minValue) {
                            throw new ParameterException(commandKey + " less than " + minValue);
                        }
                    }
                    return values;
                } else if (params instanceof Set<?>) {
                    Set<Byte> values = (Set<Byte>) params;
                    for (byte value : values) {
                        if (value < minValue) {
                            throw new ParameterException(commandKey + " less than " + minValue);
                        }
                    }
                    return values;
                } else if (params instanceof Interval<?>) {
                    Interval<Byte> values = (Interval<Byte>) params;
                    if (values.nullity()) {
                        throw new ParameterException("the interval " + values + " is invalid");
                    }

                    if (values.start() == null) {
                        values = new Interval<>(minValue, values.end());
                    } else {
                        if (values.start() < minValue) {
                            throw new ParameterException(commandKey + " < " + minValue);
                        }
                    }
                    return values;
                } else if (params instanceof Map<?, ?>) {
                    Map<String, ?> values = (Map<String, ?>) params;
                    Map<String, Object> parsed = new LinkedHashMap<>();
                    for (String key : values.keySet()) {
                        parsed.put(key, validate(commandKey, values.get(key)));
                    }
                    return parsed;
                } else {
                    throw new ParameterException("unable to infer the value type of " + commandKey);
                }
            }

            @Override
            public Byte get(String key) {
                if (key.equalsIgnoreCase("min") || key.equalsIgnoreCase("minValue")) {
                    return minValue;
                } else {
                    throw new CommandParserException("attribute " + key + " not found (support: min, minValue)");
                }
            }

            @Override
            public BYTE getBaseValueType() {
                return BYTE.VALUE;
            }

            @Override
            public String toString() {
                return ">= " + minValue;
            }
        };
    }

    @Override
    public String toString() {
        if (this.equals(BYTE.VALUE)) {
            return "BYTE";
        } else {
            return getDeclaringClass().getSimpleName() + "_" + super.toString();
        }
    }
}