package edu.sysu.pmglab.commandParser.types;

import edu.sysu.pmglab.commandParser.exception.CommandParserException;
import edu.sysu.pmglab.commandParser.exception.ParameterException;
import edu.sysu.pmglab.easytools.ArrayUtils;

import java.util.*;
import java.util.function.Function;

/**
 * long 及其扩展类型
 */

public enum LONG implements IType {
    /**
     * 值转换器
     * <p>
     * 输入格式: &lt;long&gt;
     * <p>
     * 转换格式: Long
     */
    VALUE((Function<String[], Long>) strings -> convertToLong(strings[0]), (long) 0, 1, "<long>"),

    /**
     * array 值转换器
     * <p>
     * 输入格式: &lt;long&gt; &lt;long&gt; ...
     * <p>
     * 转换格式: long[]
     */
    ARRAY((Function<String[], long[]>) strings -> {
        int index = 0;
        long[] values = new long[strings.length];
        for (String string : strings) {
            values[index++] = convertToLong(string);
        }
        return values;
    }, null, -1, "<long> <long> ..."),

    /**
     * array 值转换器
     * <p>
     * 输入格式: &lt;long&gt;,&lt;long&gt;,...
     * <p>
     * 转换格式: long[]
     */
    ARRAY_COMMA((Function<String[], long[]>) strings -> (long[]) ARRAY.convert(strings[0].split(",")), null, 1, "<long>,<long>,..."),

    /**
     * array 值转换器
     * <p>
     * 输入格式: &lt;long&gt;;&lt;long&gt;;...
     * <p>
     * 转换格式: long[]
     */
    ARRAY_SEMICOLON((Function<String[], long[]>) strings -> (long[]) ARRAY.convert(strings[0].split(";")), null, 1, "<long>;<long>;..."),

    /**
     * set 值转换器
     * <p>
     * 输入格式: &lt;long&gt; &lt;long&gt; ...
     * <p>
     * 转换格式: Set&lt;Long&gt;
     */
    SET((Function<String[], Set<Long>>) strings -> {
        Set<Long> values = new LinkedHashSet<>(2);
        for (String string : strings) {
            values.add(convertToLong(string));
        }
        return Collections.unmodifiableSet(values);
    }, null, -1, "<long> <long> ..."),

    /**
     * set 值转换器
     * <p>
     * 输入格式: &lt;long&gt;,&lt;long&gt;,...
     * <p>
     * 转换格式: Set&lt;Long&gt;
     */
    SET_COMMA((Function<String[], Set<Long>>) strings -> (Set<Long>) SET.convert(strings[0].split(",")), null, 1, "<long>,<long>,..."),

    /**
     * set 值转换器
     * <p>
     * 输入格式: &lt;long&gt;;&lt;long&gt;;...
     * <p>
     * 转换格式: Set&lt;Long&gt;
     */
    SET_SEMICOLON((Function<String[], Set<Long>>) strings -> (Set<Long>) SET.convert(strings[0].split(";")), null, 1, "<long>;<long>;..."),

    /**
     * map 值转换器
     * <p>
     * 输入格式: &lt;string&gt;=&lt;long&gt; &lt;string&gt;=&lt;long&gt; ...
     * <p>
     * 转换格式: Map&lt;String, Long&gt;
     */
    MAP((Function<String[], Map<String, Long>>) strings -> {
        Map<String, Long> maps = new LinkedHashMap<>(strings.length);
        for (String string : strings) {
            if (string.length() > 0) {
                String[] groups = string.split("=", -1);

                if (groups.length == 2) {
                    // K=V 形式
                    if (maps.containsKey(groups[0])) {
                        throw new ParameterException("key " + groups[0] + " is set repeatedly");
                    }

                    maps.put(groups[0], convertToLong(groups[1]));
                } else {
                    throw new ParameterException(string + " not in <string>=<long> format");
                }
            }
        }
        return Collections.unmodifiableMap(maps);
    }, null, -1, "<string>=<long> <string>=<long> ..."),

    /**
     * map 值转换器
     * <p>
     * 输入格式: &lt;string&gt;=&lt;long&gt;,&lt;string&gt;=&lt;long&gt;,...
     * <p>
     * 转换格式: Map&lt;String, Long&gt;
     */
    MAP_COMMA((Function<String[], Map<String, Long>>) strings -> (Map<String, Long>) MAP.convert(strings[0].split(",")), null, 1, "<string>=<long>,<string>=<long>,..."),

    /**
     * map 值转换器
     * <p>
     * 输入格式: &lt;string&gt;=&lt;long&gt;;&lt;string&gt;=&lt;long&gt;;...
     * <p>
     * 转换格式: Map&lt;String, Long&gt;
     */
    MAP_SEMICOLON((Function<String[], Map<String, Long>>) strings -> (Map<String, Long>) MAP.convert(strings[0].split(";")), null, 1, "<string>=<long>;<string>=<long>;..."),

    /**
     * range 值转换器
     * <p>
     * 输入格式: &lt;long&gt;-&lt;long&gt;
     * <p>
     * 转换格式: long[]
     */
    RANGE((Function<String[], long[]>) strings -> {
        int count = ArrayUtils.valueCounts(strings[0], '-');
        if (count == 1) {
            // v1-v2 型号
            String[] parsed = strings[0].split("-", -1);
            return new long[]{parsed[0].length() == 0 ? Long.MIN_VALUE : convertToLong(parsed[0]),
                    parsed[1].length() == 0 ? Long.MAX_VALUE : convertToLong(parsed[1])};
        } else if (count == 2) {
            if (strings[0].length() == 2) {
                // --
                throw new ParameterException("unable convert -- to Long-Long");
            }

            if (strings[0].charAt(0) == '-') {
                if (strings[0].charAt(1) == '-') {
                    // --v1
                    return new long[]{Long.MIN_VALUE, convertToLong(strings[0].substring(1))};
                } else if (strings[0].charAt(strings[0].length() - 1) == '-') {
                    // -v1-
                    return new long[]{convertToLong(strings[0].substring(0, strings[0].length() - 1)), Long.MAX_VALUE};
                } else {
                    // -v1-v2
                    int index = strings[0].indexOf('-', 1);
                    return new long[]{convertToLong(strings[0].substring(0, index)), convertToLong(strings[0].substring(index + 1))};
                }
            } else {
                // v1--v2
                int index = strings[0].indexOf("--");
                if (index != -1) {
                    return new long[]{convertToLong(strings[0].substring(0, index)), convertToLong(strings[0].substring(index + 1))};
                }
            }
        } else if (count == 3 && strings[0].charAt(0) == '-' && strings[0].charAt(1) != '-') {
            // -v1--v2
            int index = strings[0].indexOf("--");
            if (index != -1) {
                return new long[]{convertToLong(strings[0].substring(0, index)), convertToLong(strings[0].substring(index + 1))};
            }
        }

        throw new ParameterException("unable convert " + strings[0] + " to Long-Long");
    }, null, 1, "<long>-<long>"),

    /**
     * label-array 值转换器
     * <p>
     * 输入格式: &lt;string&gt;:&lt;long&gt;-&lt;long&gt; &lt;string&gt;:&lt;long&gt;-&lt;long&gt; ...
     * <p>
     * 转换格式: Map&lt;String, long[]&gt;
     */
    LABEL_RANGE((Function<String[], Map<String, long[]>>) strings -> {
        Map<String, long[]> values = new LinkedHashMap<>(strings.length);

        for (String string : strings) {
            String[] groups = string.split(":", -1);

            if (groups.length != 2) {
                throw new ParameterException(string + " not in <string>:<long>-<long> format");
            }

            if (values.containsKey(groups[0])) {
                throw new ParameterException("key " + groups[0] + " is set repeatedly");
            }
            values.put(groups[0], (long[]) RANGE.convert(groups[1]));
        }
        return Collections.unmodifiableMap(values);
    }, null, -1, "<string>:<long>-<long> <string>:<long>-<long> ..."),

    /**
     * label-array 值转换器
     * <p>
     * 输入格式: &lt;string&gt;:&lt;long&gt;-&lt;long&gt;,&lt;string&gt;:&lt;long&gt;-&lt;long&gt;,...
     * <p>
     * 转换格式: Map&lt;String, long[]&gt;
     */
    LABEL_RANGE_COMMA((Function<String[], Map<String, long[]>>) strings -> (Map<String, long[]>) LABEL_RANGE.convert(strings[0].split(",")), null, 1, "<string>:<long>-<long>,<string>:<long>-<long>,..."),

    /**
     * label-array 值转换器
     * <p>
     * 输入格式: &lt;string&gt;:&lt;long&gt;-&lt;long&gt;;&lt;string&gt;:&lt;long&gt;-&lt;long&gt;;...
     * <p>
     * 转换格式: Map&lt;String, long[]&gt;
     */
    LABEL_RANGE_SEMICOLON((Function<String[], Map<String, long[]>>) strings -> (Map<String, long[]>) LABEL_RANGE.convert(strings[0].split(";")), null, 1, "<string>:<long>-<long>;<string>:<long>-<long>;..."),

    /**
     * label-array 值转换器
     * <p>
     * 输入格式: &lt;string&gt;:&lt;long&gt;,&lt;long&gt;,... &lt;string&gt;:&lt;long&gt;,&lt;long&gt;,... ...
     * <p>
     * 转换格式: Map&lt;String, long[]&gt;
     */
    LABEL_ARRAY((Function<String[], Map<String, long[]>>) strings -> {
        Map<String, long[]> values = new LinkedHashMap<>(strings.length);

        for (String string : strings) {
            String[] groups = string.split(":", -1);

            if (groups.length != 2) {
                throw new ParameterException(string + " not in <string>:<long>,<long>,... format");
            }

            if (values.containsKey(groups[0])) {
                throw new ParameterException("key " + groups[0] + " is set repeatedly");
            }

            if (groups[1].length() == 0) {
                values.put(groups[0], new long[0]);
            } else {
                values.put(groups[0], (long[]) ARRAY_COMMA.convert(groups[1]));
            }
        }
        return Collections.unmodifiableMap(values);
    }, null, -1, "<string>:<long>,<long>,... <string>:<long>,<long>,... ..."),

    /**
     * label-array 值转换器
     * <p>
     * 输入格式: &lt;string&gt;:&lt;long&gt;,&lt;long&gt;,...;&lt;string&gt;:&lt;long&gt;,&lt;long&gt;,...;...
     * <p>
     * 转换格式: Map&lt;String, long[]&gt;
     */
    LABEL_ARRAY_SEMICOLON((Function<String[], Map<String, long[]>>) strings -> (Map<String, long[]>) LABEL_ARRAY.convert(strings[0].split(";")), null, 1, "<string>:<long>,<long>,...;<string>:<long>,<long>,...;...");

    private final Function<String[], ?> converter;
    private final int defaultArity;
    private final Object defaultValue;
    private final String defaultFormat;

    LONG(Function<String[], ?> converter, Object defaultValue, int defaultArity, String defaultFormat) {
        this.converter = converter;
        this.defaultArity = defaultArity;
        this.defaultValue = defaultValue;
        this.defaultFormat = defaultFormat;
    }

    /**
     * 将值转为 long 类型
     */
    private static long convertToLong(String value) {
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            throw new ParameterException("unable convert " + value + " to a long value");
        }
    }

    @Override
    public Object convert(String... args) {
        return this.converter.apply(args);
    }

    @Override
    public LONG getBaseValueType() {
        return LONG.VALUE;
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
    public static IValidator validateWith(long minValue, long maxValue) {
        return new IValidator() {
            @Override
            public Object validate(String commandKey, Object params) {
                if (params instanceof Long) {
                    long value = (long) params;
                    if (value < minValue || value > maxValue) {
                        throw new ParameterException(commandKey + " is out of range [" + minValue + ", " + maxValue + "]");
                    }
                } else if (params instanceof long[]) {
                    long[] values = (long[]) params;
                    for (long value : values) {
                        if (value < minValue || value > maxValue) {
                            throw new ParameterException(commandKey + " is out of range [" + minValue + ", " + maxValue + "]");
                        }
                    }
                } else if (params instanceof Set<?>) {
                    Set<Long> values = (Set<Long>) params;
                    for (long value : values) {
                        if (value < minValue || value > maxValue) {
                            throw new ParameterException(commandKey + " is out of range [" + minValue + ", " + maxValue + "]");
                        }
                    }
                } else if (params instanceof Map<?, ?>) {
                    Map<String, ?> values = (Map<String, ?>) params;
                    for (Object value : values.values()) {
                        if (value instanceof Long) {
                            if ((Long) value < minValue || (Long) value > maxValue) {
                                throw new ParameterException(commandKey + " is out of range [" + minValue + ", " + maxValue + "]");
                            }
                        } else if (value instanceof long[]) {
                            for (long v : (long[]) value) {
                                if (v < minValue || v > maxValue) {
                                    throw new ParameterException(commandKey + " is out of range [" + minValue + ", " + maxValue + "]");
                                }
                            }
                        }
                    }
                } else {
                    throw new ParameterException("unable to infer the value type of " + commandKey);
                }
                return params;
            }

            @Override
            public Long get(String key) {
                if (key.equalsIgnoreCase("min") || key.equalsIgnoreCase("minValue")) {
                    return minValue;
                } else if (key.equalsIgnoreCase("max") || key.equalsIgnoreCase("maxValue")) {
                    return maxValue;
                } else {
                    throw new CommandParserException("attribute " + key + " not found (support: min, max, minValue, maxValue)");
                }
            }

            @Override
            public LONG getBaseValueType() {
                return LONG.VALUE;
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
    public static IValidator validateWith(long minValue) {
        return new IValidator() {
            @Override
            public Object validate(String commandKey, Object params) {
                if (params instanceof Long) {
                    long value = (long) params;
                    if (value < minValue) {
                        throw new ParameterException(commandKey + " less than " + minValue);
                    }
                } else if (params instanceof long[]) {
                    long[] values = (long[]) params;
                    for (long value : values) {
                        if (value < minValue) {
                            throw new ParameterException(commandKey + " less than " + minValue);
                        }
                    }
                } else if (params instanceof Set<?>) {
                    Set<Long> values = (Set<Long>) params;
                    for (long value : values) {
                        if (value < minValue) {
                            throw new ParameterException(commandKey + " less than " + minValue);
                        }
                    }
                } else if (params instanceof Map<?, ?>) {
                    Map<String, ?> values = (Map<String, ?>) params;
                    for (Object value : values.values()) {
                        if (value instanceof Long) {
                            if ((Long) value < minValue) {
                                throw new ParameterException(commandKey + " less than " + minValue);
                            }
                        } else if (value instanceof long[]) {
                            for (long v : (long[]) value) {
                                if (v < minValue) {
                                    throw new ParameterException(commandKey + " less than " + minValue);
                                }
                            }
                        }
                    }
                } else {
                    throw new ParameterException("unable to infer the value type of " + commandKey);
                }
                return params;
            }

            @Override
            public Long get(String key) {
                if (key.equalsIgnoreCase("min") || key.equalsIgnoreCase("minValue")) {
                    return minValue;
                } else {
                    throw new CommandParserException("attribute " + key + " not found (support: min, minValue)");
                }
            }

            @Override
            public LONG getBaseValueType() {
                return LONG.VALUE;
            }

            @Override
            public String toString() {
                return ">= " + minValue;
            }
        };
    }

    @Override
    public String toString() {
        if (this.equals(LONG.VALUE)) {
            return "LONG";
        } else {
            return getDeclaringClass().getSimpleName() + "_" + super.toString();
        }
    }
}