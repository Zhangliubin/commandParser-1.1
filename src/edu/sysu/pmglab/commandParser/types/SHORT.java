package edu.sysu.pmglab.commandParser.types;

import edu.sysu.pmglab.commandParser.exception.CommandParserException;
import edu.sysu.pmglab.commandParser.exception.ParameterException;
import edu.sysu.pmglab.container.Interval;
import edu.sysu.pmglab.easytools.ArrayUtils;
import edu.sysu.pmglab.easytools.Assert;

import java.util.*;
import java.util.function.Function;

/**
 * short 及其扩展类型
 */

public enum SHORT implements IType {
    /**
     * 值转换器
     * <p>
     * 输入格式: &lt;short&gt;
     * <p>
     * 转换格式: Short
     */
    VALUE((Function<String[], Short>) strings -> convertToShort(strings[0]), (short) 0, 1, "<short>"),

    /**
     * array 值转换器
     * <p>
     * 输入格式: &lt;short&gt; &lt;short&gt; ...
     * <p>
     * 转换格式: short[]
     */
    ARRAY((Function<String[], short[]>) strings -> {
        int index = 0;
        short[] values = new short[strings.length];
        for (String string : strings) {
            values[index++] = convertToShort(string);
        }
        return values;
    }, null, -1, "<short> <short> ..."),

    /**
     * array 值转换器
     * <p>
     * 输入格式: &lt;short&gt;,&lt;short&gt;,...
     * <p>
     * 转换格式: short[]
     */
    ARRAY_COMMA((Function<String[], short[]>) strings -> (short[]) ARRAY.convert(strings[0].split(",")), null, 1, "<short>,<short>,..."),

    /**
     * array 值转换器
     * <p>
     * 输入格式: &lt;short&gt;;&lt;short&gt;;...
     * <p>
     * 转换格式: short[]
     */
    ARRAY_SEMICOLON((Function<String[], short[]>) strings -> (short[]) ARRAY.convert(strings[0].split(";")), null, 1, "<short>;<short>;..."),

    /**
     * set 值转换器
     * <p>
     * 输入格式: &lt;short&gt; &lt;short&gt; ...
     * <p>
     * 转换格式: Set&lt;Short&gt;
     */
    SET((Function<String[], Set<Short>>) strings -> {
        Set<Short> values = new LinkedHashSet<>(2);
        for (String string : strings) {
            values.add(convertToShort(string));
        }
        return Collections.unmodifiableSet(values);
    }, null, -1, "<short> <short> ..."),

    /**
     * set 值转换器
     * <p>
     * 输入格式: &lt;short&gt;,&lt;short&gt;,...
     * <p>
     * 转换格式: Set&lt;Short&gt;
     */
    @SuppressWarnings("unchecked")
    SET_COMMA((Function<String[], Set<Short>>) strings -> (Set<Short>) SET.convert(strings[0].split(",")), null, 1, "<short>,<short>,..."),

    /**
     * set 值转换器
     * <p>
     * 输入格式: &lt;short&gt;;&lt;short&gt;;...
     * <p>
     * 转换格式: Set&lt;Short&gt;
     */
    @SuppressWarnings("unchecked")
    SET_SEMICOLON((Function<String[], Set<Short>>) strings -> (Set<Short>) SET.convert(strings[0].split(";")), null, 1, "<short>;<short>;..."),

    /**
     * map 值转换器
     * <p>
     * 输入格式: &lt;string&gt;=&lt;short&gt; &lt;string&gt;=&lt;short&gt; ...
     * <p>
     * 转换格式: Map&lt;String, Short&gt;
     */
    MAP((Function<String[], Map<String, Short>>) strings -> {
        Map<String, Short> maps = new LinkedHashMap<>(strings.length);
        for (String string : strings) {
            if (string.length() > 0) {
                String[] groups = string.split("=", -1);

                if (groups.length == 2) {
                    // K=V 形式
                    if (maps.containsKey(groups[0])) {
                        throw new ParameterException("key " + groups[0] + " is set repeatedly");
                    }

                    maps.put(groups[0], convertToShort(groups[1]));
                } else {
                    throw new ParameterException(string + " not in <string>=<short> format");
                }
            }
        }
        return Collections.unmodifiableMap(maps);
    }, null, -1, "<string>=<short> <string>=<short> ..."),

    /**
     * map 值转换器
     * <p>
     * 输入格式: &lt;string&gt;=&lt;short&gt;,&lt;string&gt;=&lt;short&gt;,...
     * <p>
     * 转换格式: Map&lt;String, Short&gt;
     */
    @SuppressWarnings("unchecked")
    MAP_COMMA((Function<String[], Map<String, Short>>) strings -> (Map<String, Short>) MAP.convert(strings[0].split(",")), null, 1, "<string>=<short>,<string>=<short>,..."),

    /**
     * map 值转换器
     * <p>
     * 输入格式: &lt;string&gt;=&lt;short&gt;;&lt;string&gt;=&lt;short&gt;;...
     * <p>
     * 转换格式: Map&lt;String, Short&gt;
     */
    @SuppressWarnings("unchecked")
    MAP_SEMICOLON((Function<String[], Map<String, Short>>) strings -> (Map<String, Short>) MAP.convert(strings[0].split(";")), null, 1, "<string>=<short>;<string>=<short>;..."),

    /**
     * range 值转换器
     * <p>
     * 输入格式: &lt;short&gt;-&lt;short&gt;
     * <p>
     * 转换格式: Interval&lt;Short&gt;
     */
    RANGE((Function<String[], Interval<Short>>) strings -> {
        int count = ArrayUtils.valueCounts(strings[0], '-');
        if (count == 1) {
            // v1-v2 型号
            String[] parsed = strings[0].split("-", -1);
            return new Interval<>(parsed[0].length() == 0 ? null : convertToShort(parsed[0]),
                    parsed[1].length() == 0 ? null : convertToShort(parsed[1]));
        } else if (count == 2) {
            if (strings[0].length() == 2) {
                // --
                throw new ParameterException("unable convert -- to <short>-<short>");
            }

            if (strings[0].charAt(0) == '-') {
                if (strings[0].charAt(1) == '-') {
                    // --v1
                    return new Interval<>(null, convertToShort(strings[0].substring(1)));
                } else if (strings[0].charAt(strings[0].length() - 1) == '-') {
                    // -v1-
                    return new Interval<>(convertToShort(strings[0].substring(0, strings[0].length() - 1)), null);
                } else {
                    // -v1-v2
                    int index = strings[0].indexOf('-', 1);
                    return new Interval<>(convertToShort(strings[0].substring(0, index)), convertToShort(strings[0].substring(index + 1)));
                }
            } else {
                // v1--v2
                int index = strings[0].indexOf("--");
                if (index != -1) {
                    return new Interval<>(convertToShort(strings[0].substring(0, index)), convertToShort(strings[0].substring(index + 1)));
                }
            }
        } else if (count == 3 && strings[0].charAt(0) == '-' && strings[0].charAt(1) != '-') {
            // -v1--v2
            int index = strings[0].indexOf("--");
            if (index != -1) {
                return new Interval<>(convertToShort(strings[0].substring(0, index)), convertToShort(strings[0].substring(index + 1)));
            }
        }

        throw new ParameterException("unable convert " + strings[0] + " to <short>-<short>");
    }, null, 1, "<short>-<short>"),

    /**
     * label-range 值转换器
     * <p>
     * 输入格式: &lt;string&gt;:&lt;short&gt;-&lt;short&gt; &lt;string&gt;:&lt;short&gt;-&lt;short&gt; ...
     * <p>
     * 转换格式: Map&lt;String, Interval&lt;Short&gt;&gt;
     */
    @SuppressWarnings("unchecked")
    LABEL_RANGE((Function<String[], Map<String, Interval<Short>>>) strings -> {
        Map<String, Interval<Short>> values = new LinkedHashMap<>(strings.length);

        for (String string : strings) {
            String[] groups = string.split(":", -1);

            if (groups.length != 2) {
                throw new ParameterException(string + " not in <string>:<short>-<short> format");
            }

            if (values.containsKey(groups[0])) {
                throw new ParameterException("key " + groups[0] + " is set repeatedly");
            }

            values.put(groups[0], (Interval<Short>) RANGE.convert(groups[1]));
        }
        return Collections.unmodifiableMap(values);
    }, null, -1, "<string>:<short>-<short> <string>:<short>-<short> ..."),

    /**
     * label-range 值转换器
     * <p>
     * 输入格式: &lt;string&gt;:&lt;short&gt;-&lt;short&gt;,&lt;string&gt;:&lt;short&gt;-&lt;short&gt;,...
     * <p>
     * 转换格式: Map&lt;String, Interval&lt;Short&gt;&gt;
     */
    @SuppressWarnings("unchecked")
    LABEL_RANGE_COMMA((Function<String[], Map<String, Interval<Short>>>) strings -> (Map<String, Interval<Short>>) LABEL_RANGE.convert(strings[0].split(",")), null, 1, "<string>:<short>-<short>,<string>:<short>-<short>,..."),

    /**
     * label-range 值转换器
     * <p>
     * 输入格式: &lt;string&gt;:&lt;short&gt;-&lt;short&gt;;&lt;string&gt;:&lt;short&gt;-&lt;short&gt;;...
     * <p>
     * 转换格式: Map&lt;String, Interval&lt;Short&gt;&gt;
     */
    @SuppressWarnings("unchecked")
    LABEL_RANGE_SEMICOLON((Function<String[], Map<String, Interval<Short>>>) strings -> (Map<String, Interval<Short>>) LABEL_RANGE.convert(strings[0].split(";")), null, 1, "<string>:<short>-<short>;<string>:<short>-<short>;..."),

    /**
     * label-array 值转换器
     * <p>
     * 输入格式: &lt;string&gt;:&lt;short&gt;,&lt;short&gt;,... &lt;string&gt;:&lt;short&gt;,&lt;short&gt;,... ...
     * <p>
     * 转换格式: Map&lt;String, short[]&gt;
     */
    LABEL_ARRAY((Function<String[], Map<String, short[]>>) strings -> {
        Map<String, short[]> values = new LinkedHashMap<>(strings.length);

        for (String string : strings) {
            String[] groups = string.split(":", -1);

            if (groups.length != 2) {
                throw new ParameterException(string + " not in <string>:<short>,<short>,... format");
            }

            if (values.containsKey(groups[0])) {
                throw new ParameterException("key " + groups[0] + " is set repeatedly");
            }
            values.put(groups[0], (short[]) ARRAY_COMMA.convert(groups[1]));
        }
        return Collections.unmodifiableMap(values);
    }, null, -1, "<string>:<short>,<short>,... <string>:<short>,<short>,... ..."),

    /**
     * label-array 值转换器
     * <p>
     * 输入格式: &lt;string&gt;:&lt;short&gt;,&lt;short&gt;,...;&lt;string&gt;:&lt;short&gt;,&lt;short&gt;,...;...
     * <p>
     * 转换格式: Map&lt;String, short[]&gt;
     */
    @SuppressWarnings("unchecked")
    LABEL_ARRAY_SEMICOLON((Function<String[], Map<String, short[]>>) strings -> (Map<String, short[]>) LABEL_ARRAY.convert(strings[0].split(";")), null, 1, "<string>:<short>,<short>,...;<string>:<short>,<short>,...;...");

    private final Function<String[], ?> converter;
    private final int defaultArity;
    private final Object defaultValue;
    private final String defaultFormat;

    SHORT(Function<String[], ?> converter, Object defaultValue, int defaultArity, String defaultFormat) {
        this.converter = converter;
        this.defaultArity = defaultArity;
        this.defaultValue = defaultValue;
        this.defaultFormat = defaultFormat;
    }

    /**
     * 将值转为 short 类型
     */
    private static short convertToShort(String value) {
        try {
            return Short.parseShort(value);
        } catch (NumberFormatException e) {
            throw new ParameterException("unable convert " + value + " to a short value");
        }
    }

    @Override
    public Object convert(String... args) {
        return this.converter.apply(args);
    }

    @Override
    public SHORT getBaseValueType() {
        return SHORT.VALUE;
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
    public static IValidator validateWith(short minValue, short maxValue) {
        Assert.that(minValue <= maxValue);

        return new IValidator() {
            @Override
            @SuppressWarnings("unchecked")
            public Object validate(String commandKey, Object params) {
                if (params instanceof Short) {
                    short value = (short) params;
                    if (value < minValue || value > maxValue) {
                        throw new ParameterException(commandKey + " is out of range [" + minValue + ", " + maxValue + "]");
                    }
                    return value;
                } else if (params instanceof short[]) {
                    short[] values = (short[]) params;
                    for (short value : values) {
                        if (value < minValue || value > maxValue) {
                            throw new ParameterException(commandKey + " is out of range [" + minValue + ", " + maxValue + "]");
                        }
                    }
                    return values;
                } else if (params instanceof Set<?>) {
                    Set<Short> values = (Set<Short>) params;
                    for (short value : values) {
                        if (value < minValue || value > maxValue) {
                            throw new ParameterException(commandKey + " is out of range [" + minValue + ", " + maxValue + "]");
                        }
                    }
                    return values;
                } else if (params instanceof Interval<?>) {
                    Interval<Short> values = (Interval<Short>) params;
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
            public Short get(String key) {
                if (key.equalsIgnoreCase("min") || key.equalsIgnoreCase("minValue")) {
                    return minValue;
                } else if (key.equalsIgnoreCase("max") || key.equalsIgnoreCase("maxValue")) {
                    return maxValue;
                } else {
                    throw new CommandParserException("attribute " + key + " not found (support: min, max, minValue, maxValue)");
                }
            }

            @Override
            public SHORT getBaseValueType() {
                return SHORT.VALUE;
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
    public static IValidator validateWith(short minValue) {
        return new IValidator() {
            @Override
            @SuppressWarnings("unchecked")
            public Object validate(String commandKey, Object params) {
                if (params instanceof Short) {
                    short value = (short) params;
                    if (value < minValue) {
                        throw new ParameterException(commandKey + " less than " + minValue);
                    }
                    return value;
                } else if (params instanceof short[]) {
                    short[] values = (short[]) params;
                    for (short value : values) {
                        if (value < minValue) {
                            throw new ParameterException(commandKey + " less than " + minValue);
                        }
                    }
                    return values;
                } else if (params instanceof Set<?>) {
                    Set<Short> values = (Set<Short>) params;
                    for (short value : values) {
                        if (value < minValue) {
                            throw new ParameterException(commandKey + " less than " + minValue);
                        }
                    }
                    return values;
                } else if (params instanceof Interval<?>) {
                    Interval<Short> values = (Interval<Short>) params;
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
            public Short get(String key) {
                if (key.equalsIgnoreCase("min") || key.equalsIgnoreCase("minValue")) {
                    return minValue;
                } else {
                    throw new CommandParserException("attribute " + key + " not found (support: min, minValue)");
                }
            }

            @Override
            public SHORT getBaseValueType() {
                return SHORT.VALUE;
            }

            @Override
            public String toString() {
                return ">= " + minValue;
            }
        };
    }

    @Override
    public String toString() {
        if (this.equals(SHORT.VALUE)) {
            return "SHORT";
        } else {
            return getDeclaringClass().getSimpleName() + "_" + super.toString();
        }
    }
}