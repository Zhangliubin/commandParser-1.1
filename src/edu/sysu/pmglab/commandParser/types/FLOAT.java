package edu.sysu.pmglab.commandParser.types;

import edu.sysu.pmglab.commandParser.exception.CommandParserException;
import edu.sysu.pmglab.commandParser.exception.ParameterException;
import edu.sysu.pmglab.container.Interval;
import edu.sysu.pmglab.easytools.ArrayUtils;
import edu.sysu.pmglab.easytools.Assert;

import java.util.*;
import java.util.function.Function;

/**
 * float 及其扩展类型
 */

public enum FLOAT implements IType {
    /**
     * 值转换器
     * <p>
     * 输入格式: &lt;float&gt;
     * <p>
     * 转换格式: Float
     */
    VALUE((Function<String[], Float>) strings -> convertToFloat(strings[0]), Float.NaN, 1, "<float>"),

    /**
     * array 值转换器
     * <p>
     * 输入格式: &lt;float&gt; &lt;float&gt; ...
     * <p>
     * 转换格式: float[]
     */
    ARRAY((Function<String[], float[]>) strings -> {
        int index = 0;
        float[] values = new float[strings.length];
        for (String string : strings) {
            values[index++] = convertToFloat(string);
        }
        return values;
    }, null, -1, "<float> <float> ..."),

    /**
     * array 值转换器
     * <p>
     * 输入格式: &lt;float&gt;,&lt;float&gt;,...
     * <p>
     * 转换格式: float[]
     */
    ARRAY_COMMA((Function<String[], float[]>) strings -> (float[]) ARRAY.convert(strings[0].split(",")), null, 1, "<float>,<float>,..."),

    /**
     * array 值转换器
     * <p>
     * 输入格式: &lt;float&gt;;&lt;float&gt;;...
     * <p>
     * 转换格式: float[]
     */
    ARRAY_SEMICOLON((Function<String[], float[]>) strings -> (float[]) ARRAY.convert(strings[0].split(";")), null, 1, "<float>;<float>;..."),

    /**
     * set 值转换器
     * <p>
     * 输入格式: &lt;float&gt; &lt;float&gt; ...
     * <p>
     * 转换格式: Set&lt;Float&gt;
     */
    SET((Function<String[], Set<Float>>) strings -> {
        Set<Float> values = new LinkedHashSet<>(2);
        for (String string : strings) {
            values.add(convertToFloat(string));
        }
        return Collections.unmodifiableSet(values);
    }, null, -1, "<float> <float> ..."),

    /**
     * set 值转换器
     * <p>
     * 输入格式: &lt;float&gt;,&lt;float&gt;,...
     * <p>
     * 转换格式: Set&lt;Float&gt;
     */
    @SuppressWarnings("unchecked")
    SET_COMMA((Function<String[], Set<Float>>) strings -> (Set<Float>) SET.convert(strings[0].split(",")), null, 1, "<float>,<float>,..."),

    /**
     * set 值转换器
     * <p>
     * 输入格式: &lt;float&gt;;&lt;float&gt;;...
     * <p>
     * 转换格式: Set&lt;Float&gt;
     */
    @SuppressWarnings("unchecked")
    SET_SEMICOLON((Function<String[], Set<Float>>) strings -> (Set<Float>) SET.convert(strings[0].split(";")), null, 1, "<float>;<float>;..."),

    /**
     * map 值转换器
     * <p>
     * 输入格式: &lt;string&gt;=&lt;float&gt; &lt;string&gt;=&lt;float&gt; ...
     * <p>
     * 转换格式: Map&lt;String, Float&gt;
     */
    MAP((Function<String[], Map<String, Float>>) strings -> {
        Map<String, Float> maps = new LinkedHashMap<>(strings.length);
        for (String string : strings) {
            if (string.length() > 0) {
                String[] groups = string.split("=", -1);

                if (groups.length == 2) {
                    // K=V 形式
                    if (maps.containsKey(groups[0])) {
                        throw new ParameterException("key " + groups[0] + " is set repeatedly");
                    }

                    maps.put(groups[0], convertToFloat(groups[1]));
                } else {
                    throw new ParameterException(string + " not in <string>=<float> format");
                }
            }
        }
        return Collections.unmodifiableMap(maps);
    }, null, -1, "<string>=<float> <string>=<float> ..."),

    /**
     * map 值转换器
     * <p>
     * 输入格式: &lt;string&gt;=&lt;float&gt;,&lt;string&gt;=&lt;float&gt;,...
     * <p>
     * 转换格式: Map&lt;String, Float&gt;
     */
    @SuppressWarnings("unchecked")
    MAP_COMMA((Function<String[], Map<String, Float>>) strings -> (Map<String, Float>) MAP.convert(strings[0].split(",")), null, 1, "<string>=<float>,<string>=<float>,..."),

    /**
     * map 值转换器
     * <p>
     * 输入格式: &lt;string&gt;=&lt;float&gt;;&lt;string&gt;=&lt;float&gt;;...
     * <p>
     * 转换格式: Map&lt;String, Float&gt;
     */
    @SuppressWarnings("unchecked")
    MAP_SEMICOLON((Function<String[], Map<String, Float>>) strings -> (Map<String, Float>) MAP.convert(strings[0].split(";")), null, 1, "<string>=<float>;<string>=<float>;..."),

    /**
     * range 值转换器
     * <p>
     * 输入格式: &lt;float&gt;-&lt;float&gt;
     * <p>
     * 转换格式: float[]
     */
    RANGE((Function<String[], Interval<Float>>) strings -> {
        if (strings[0].contains("e") || strings[0].contains("E")) {
            // 科学计数法中可能存在 -1e-5-1e-6 这类形式, 需要额外的分支逻辑,
            throw new ParameterException("unable to parse floating point numbers in scientific notation (i.e. containing 'e' or 'E')");
        }

        int count = ArrayUtils.valueCounts(strings[0], '-');
        if (count == 1) {
            // v1-v2 型号
            String[] parsed = strings[0].split("-", -1);
            return new Interval<>(parsed[0].length() == 0 ? null : convertToFloat(parsed[0]),
                    parsed[1].length() == 0 ? null : convertToFloat(parsed[1]));
        } else if (count == 2) {
            if (strings[0].length() == 2) {
                // --
                throw new ParameterException("unable convert -- to <float>-<float>");
            }

            if (strings[0].charAt(0) == '-') {
                if (strings[0].charAt(1) == '-') {
                    // --v1
                    return new Interval<>(null, convertToFloat(strings[0].substring(1)));
                } else if (strings[0].charAt(strings[0].length() - 1) == '-') {
                    // -v1-
                    return new Interval<>(convertToFloat(strings[0].substring(0, strings[0].length() - 1)), null);
                } else {
                    // -v1-v2
                    int index = strings[0].indexOf('-', 1);
                    return new Interval<>(convertToFloat(strings[0].substring(0, index)), convertToFloat(strings[0].substring(index + 1)));
                }
            } else {
                // v1--v2
                int index = strings[0].indexOf("--");
                if (index != -1) {
                    return new Interval<>(convertToFloat(strings[0].substring(0, index)), convertToFloat(strings[0].substring(index + 1)));
                }
            }
        } else if (count == 3 && strings[0].charAt(0) == '-' && strings[0].charAt(1) != '-') {
            // -v1--v2
            int index = strings[0].indexOf("--");
            if (index != -1) {
                return new Interval<>(convertToFloat(strings[0].substring(0, index)), convertToFloat(strings[0].substring(index + 1)));
            }
        }

        throw new ParameterException("unable convert " + strings[0] + " to <float>-<float>");
    }, null, 1, "<float>-<float>"),

    /**
     * label-range 值转换器
     * <p>
     * 输入格式: &lt;string&gt;:&lt;float&gt;-&lt;float&gt; &lt;string&gt;:&lt;float&gt;-&lt;float&gt; ...
     * <p>
     * 转换格式: Map&lt;String, Interval&lt;Float&gt;&gt;
     */
    @SuppressWarnings("unchecked")
    LABEL_RANGE((Function<String[], Map<String, Interval<Float>>>) strings -> {
        Map<String, Interval<Float>> values = new LinkedHashMap<>(strings.length);

        for (String string : strings) {
            String[] groups = string.split(":", -1);

            if (groups.length != 2) {
                throw new ParameterException(string + " not in <string>:<float>-<float> format");
            }

            if (values.containsKey(groups[0])) {
                throw new ParameterException("key " + groups[0] + " is set repeatedly");
            }
            values.put(groups[0], (Interval<Float>) RANGE.convert(groups[1]));
        }
        return Collections.unmodifiableMap(values);
    }, null, -1, "<string>:<float>-<float> <string>:<float>-<float> ..."),

    /**
     * label-range 值转换器
     * <p>
     * 输入格式: &lt;string&gt;:&lt;float&gt;-&lt;float&gt;,&lt;string&gt;:&lt;float&gt;-&lt;float&gt;,...
     * <p>
     * 转换格式: Map&lt;String, Interval&lt;Float&gt;&gt;
     */
    @SuppressWarnings("unchecked")
    LABEL_RANGE_COMMA((Function<String[], Map<String, Interval<Float>>>) strings -> (Map<String, Interval<Float>>) LABEL_RANGE.convert(strings[0].split(",")), null, 1, "<string>:<float>-<float>,<string>:<float>-<float>,..."),

    /**
     * label-range 值转换器
     * <p>
     * 输入格式: &lt;string&gt;:&lt;float&gt;-&lt;float&gt;;&lt;string&gt;:&lt;float&gt;-&lt;float&gt;;...
     * <p>
     * 转换格式: Map&lt;String, Interval&lt;Float&gt;&gt;
     */
    @SuppressWarnings("unchecked")
    LABEL_RANGE_SEMICOLON((Function<String[], Map<String, Interval<Float>>>) strings -> (Map<String, Interval<Float>>) LABEL_RANGE.convert(strings[0].split(";")), null, 1, "<string>:<float>-<float>;<string>:<float>-<float>;..."),

    /**
     * label-array 值转换器
     * <p>
     * 输入格式: &lt;string&gt;:&lt;float&gt;,&lt;float&gt;,... &lt;string&gt;:&lt;float&gt;,&lt;float&gt;,... ...
     * <p>
     * 转换格式: Map&lt;String, float[]&gt;
     */
    LABEL_ARRAY((Function<String[], Map<String, float[]>>) strings -> {
        Map<String, float[]> values = new LinkedHashMap<>(strings.length);

        for (String string : strings) {
            String[] groups = string.split(":", -1);

            if (groups.length != 2) {
                throw new ParameterException(string + " not in <string>:<float>,<float>,... format");
            }

            if (values.containsKey(groups[0])) {
                throw new ParameterException("key " + groups[0] + " is set repeatedly");
            }

            if (groups[1].length() == 0) {
                values.put(groups[0], new float[0]);
            } else {
                values.put(groups[0], (float[]) ARRAY_COMMA.convert(groups[1]));
            }
        }
        return Collections.unmodifiableMap(values);
    }, null, -1, "<string>:<float>,<float>,... <string>:<float>,<float>,... ..."),

    /**
     * label-array 值转换器
     * <p>
     * 输入格式: &lt;string&gt;:&lt;float&gt;,&lt;float&gt;,...;&lt;string&gt;:&lt;float&gt;,&lt;float&gt;,...;...
     * <p>
     * 转换格式: Map&lt;String, float[]&gt;
     */
    @SuppressWarnings("unchecked")
    LABEL_ARRAY_SEMICOLON((Function<String[], Map<String, float[]>>) strings -> (Map<String, float[]>) LABEL_ARRAY.convert(strings[0].split(";")), null, 1, "<string>:<float>,<float>,...;<string>:<float>,<float>,...;...");

    private final Function<String[], ?> converter;
    private final int defaultArity;
    private final Object defaultValue;
    private final String defaultFormat;

    FLOAT(Function<String[], ?> converter, Object defaultValue, int defaultArity, String defaultFormat) {
        this.converter = converter;
        this.defaultArity = defaultArity;
        this.defaultValue = defaultValue;
        this.defaultFormat = defaultFormat;
    }

    /**
     * 将值转为 float 类型
     */
    private static float convertToFloat(String value) {
        try {
            return Float.parseFloat(value);
        } catch (NumberFormatException e) {
            throw new ParameterException("unable convert " + value + " to a float value");
        }
    }

    @Override
    public Object convert(String... args) {
        return this.converter.apply(args);
    }

    @Override
    public FLOAT getBaseValueType() {
        return FLOAT.VALUE;
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
    public static IValidator validateWith(float minValue, float maxValue) {
        Assert.that(minValue <= maxValue);

        return new IValidator() {
            @Override
            @SuppressWarnings("unchecked")
            public Object validate(String commandKey, Object params) {
                if (params instanceof Float) {
                    float value = (float) params;
                    if (value < minValue || value > maxValue) {
                        throw new ParameterException(commandKey + " is out of range [" + minValue + ", " + maxValue + "]");
                    }
                    return value;
                } else if (params instanceof float[]) {
                    float[] values = (float[]) params;
                    for (float value : values) {
                        if (value < minValue || value > maxValue) {
                            throw new ParameterException(commandKey + " is out of range [" + minValue + ", " + maxValue + "]");
                        }
                    }
                    return values;
                } else if (params instanceof Set<?>) {
                    Set<Float> values = (Set<Float>) params;
                    for (float value : values) {
                        if (value < minValue || value > maxValue) {
                            throw new ParameterException(commandKey + " is out of range [" + minValue + ", " + maxValue + "]");
                        }
                    }
                    return values;
                } else if (params instanceof Interval<?>) {
                    Interval<Float> values = (Interval<Float>) params;
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
            public Float get(String key) {
                if (key.equalsIgnoreCase("min") || key.equalsIgnoreCase("minValue")) {
                    return minValue;
                } else if (key.equalsIgnoreCase("max") || key.equalsIgnoreCase("maxValue")) {
                    return maxValue;
                } else {
                    throw new CommandParserException("attribute " + key + " not found (support: min, max, minValue, maxValue)");
                }
            }

            @Override
            public FLOAT getBaseValueType() {
                return FLOAT.VALUE;
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
    public static IValidator validateWith(float minValue) {
        return new IValidator() {
            @Override
            @SuppressWarnings("unchecked")
            public Object validate(String commandKey, Object params) {
                if (params instanceof Float) {
                    float value = (float) params;
                    if (value < minValue) {
                        throw new ParameterException(commandKey + " less than " + minValue);
                    }
                    return value;
                } else if (params instanceof float[]) {
                    float[] values = (float[]) params;
                    for (float value : values) {
                        if (value < minValue) {
                            throw new ParameterException(commandKey + " less than " + minValue);
                        }
                    }
                    return values;
                } else if (params instanceof Set<?>) {
                    Set<Float> values = (Set<Float>) params;
                    for (float value : values) {
                        if (value < minValue) {
                            throw new ParameterException(commandKey + " less than " + minValue);
                        }
                    }
                    return values;
                } else if (params instanceof Interval<?>) {
                    Interval<Float> values = (Interval<Float>) params;
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
            public Float get(String key) {
                if (key.equalsIgnoreCase("min") || key.equalsIgnoreCase("minValue")) {
                    return minValue;
                } else {
                    throw new CommandParserException("attribute " + key + " not found (support: min, minValue)");
                }
            }

            @Override
            public FLOAT getBaseValueType() {
                return FLOAT.VALUE;
            }

            @Override
            public String toString() {
                return ">= " + minValue;
            }
        };
    }

    @Override
    public String toString() {
        if (this.equals(FLOAT.VALUE)) {
            return "FLOAT";
        } else {
            return getDeclaringClass().getSimpleName() + "_" + super.toString();
        }
    }
}