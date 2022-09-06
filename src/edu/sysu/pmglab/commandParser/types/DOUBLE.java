package edu.sysu.pmglab.commandParser.types;

import edu.sysu.pmglab.commandParser.exception.CommandParserException;
import edu.sysu.pmglab.commandParser.exception.ParameterException;
import edu.sysu.pmglab.easytools.ArrayUtils;

import java.util.*;
import java.util.function.Function;

/**
 * double 及其扩展类型
 */

public enum DOUBLE implements IType {
    /**
     * 值转换器
     * <p>
     * 输入格式: &lt;double&gt;
     * <p>
     * 转换格式: Double
     */
    VALUE((Function<String[], Double>) strings -> convertToDouble(strings[0]), Double.NaN, 1, "<double>"),

    /**
     * array 值转换器
     * <p>
     * 输入格式: &lt;double&gt; &lt;double&gt; ...
     * <p>
     * 转换格式: double[]
     */
    ARRAY((Function<String[], double[]>) strings -> {
        int index = 0;
        double[] values = new double[strings.length];
        for (String string : strings) {
            values[index++] = convertToDouble(string);
        }
        return values;
    }, null, -1, "<double> <double> ..."),

    /**
     * array 值转换器
     * <p>
     * 输入格式: &lt;double&gt;,&lt;double&gt;,...
     * <p>
     * 转换格式: double[]
     */
    ARRAY_COMMA((Function<String[], double[]>) strings -> (double[]) ARRAY.convert(strings[0].split(",")), null, 1, "<double>,<double>,..."),

    /**
     * array 值转换器
     * <p>
     * 输入格式: &lt;double&gt;;&lt;double&gt;;...
     * <p>
     * 转换格式: double[]
     */
    ARRAY_SEMICOLON((Function<String[], double[]>) strings -> (double[]) ARRAY.convert(strings[0].split(";")), null, 1, "<double>;<double>;..."),

    /**
     * set 值转换器
     * <p>
     * 输入格式: &lt;double&gt; &lt;double&gt; ...
     * <p>
     * 转换格式: Set&lt;Double&gt;
     */
    SET((Function<String[], Set<Double>>) strings -> {
        Set<Double> values = new LinkedHashSet<>(2);
        for (String string : strings) {
            values.add(convertToDouble(string));
        }
        return Collections.unmodifiableSet(values);
    }, null, -1, "<double> <double> ..."),

    /**
     * set 值转换器
     * <p>
     * 输入格式: &lt;double&gt;,&lt;double&gt;,...
     * <p>
     * 转换格式: Set&lt;Double&gt;
     */
    SET_COMMA((Function<String[], Set<Double>>) strings -> (Set<Double>) SET.convert(strings[0].split(",")), null, 1, "<double>,<double>,..."),

    /**
     * set 值转换器
     * <p>
     * 输入格式: &lt;double&gt;;&lt;double&gt;;...
     * <p>
     * 转换格式: Set&lt;Double&gt;
     */
    SET_SEMICOLON((Function<String[], Set<Double>>) strings -> (Set<Double>) SET.convert(strings[0].split(";")), null, 1, "<double>;<double>;..."),

    /**
     * map 值转换器
     * <p>
     * 输入格式: &lt;string&gt;=&lt;double&gt; &lt;string&gt;=&lt;double&gt; ...
     * <p>
     * 转换格式: Map&lt;String, Double&gt;
     */
    MAP((Function<String[], Map<String, Double>>) strings -> {
        Map<String, Double> maps = new LinkedHashMap<>(strings.length);
        for (String string : strings) {
            if (string.length() > 0) {
                String[] groups = string.split("=", -1);

                if (groups.length == 2) {
                    // K=V 形式
                    if (maps.containsKey(groups[0])) {
                        throw new ParameterException("key " + groups[0] + " is set repeatedly");
                    }

                    maps.put(groups[0], convertToDouble(groups[1]));
                } else {
                    throw new ParameterException(string + " not in <string>=<double> format");
                }
            }
        }
        return Collections.unmodifiableMap(maps);
    }, null, -1, "<string>=<double> <string>=<double> ..."),

    /**
     * map 值转换器
     * <p>
     * 输入格式: &lt;string&gt;=&lt;double&gt;,&lt;string&gt;=&lt;double&gt;,...
     * <p>
     * 转换格式: Map&lt;String, Double&gt;
     */
    MAP_COMMA((Function<String[], Map<String, Double>>) strings -> (Map<String, Double>) MAP.convert(strings[0].split(",")), null, 1, "<string>=<double>,<string>=<double>,..."),

    /**
     * map 值转换器
     * <p>
     * 输入格式: &lt;string&gt;=&lt;double&gt;;&lt;string&gt;=&lt;double&gt;;...
     * <p>
     * 转换格式: Map&lt;String, Double&gt;
     */
    MAP_SEMICOLON((Function<String[], Map<String, Double>>) strings -> (Map<String, Double>) MAP.convert(strings[0].split(";")), null, 1, "<string>=<double>;<string>=<double>;..."),

    /**
     * range 值转换器
     * <p>
     * 输入格式: &lt;double&gt;-&lt;double&gt;
     * <p>
     * 转换格式: double[]
     */
    RANGE((Function<String[], double[]>) strings -> {
        if (strings[0].contains("e") || strings[0].contains("E")) {
            // 科学计数法中可能存在 -1e-5-1e-6 这类形式, 需要额外的分支逻辑,
            throw new ParameterException("unable to parse floating point numbers in scientific notation (i.e. containing 'e' or 'E')");
        }

        int count = ArrayUtils.valueCounts(strings[0], '-');
        if (count == 1) {
            // v1-v2 型号
            String[] parsed = strings[0].split("-", -1);
            return new double[]{parsed[0].length() == 0 ? Double.NaN : convertToDouble(parsed[0]),
                    parsed[1].length() == 0 ? Double.NaN : convertToDouble(parsed[1])};
        } else if (count == 2) {
            if (strings[0].length() == 2) {
                // --
                throw new ParameterException("unable convert -- to Double-Double");
            }

            if (strings[0].charAt(0) == '-') {
                if (strings[0].charAt(1) == '-') {
                    // --v1
                    return new double[]{Double.NaN, convertToDouble(strings[0].substring(1))};
                } else if (strings[0].charAt(strings[0].length() - 1) == '-') {
                    // -v1-
                    return new double[]{convertToDouble(strings[0].substring(0, strings[0].length() - 1)), Double.NaN};
                } else {
                    // -v1-v2
                    int index = strings[0].indexOf('-', 1);
                    return new double[]{convertToDouble(strings[0].substring(0, index)), convertToDouble(strings[0].substring(index + 1))};
                }
            } else {
                // v1--v2
                int index = strings[0].indexOf("--");
                if (index != -1) {
                    return new double[]{convertToDouble(strings[0].substring(0, index)), convertToDouble(strings[0].substring(index + 1))};
                }
            }
        } else if (count == 3 && strings[0].charAt(0) == '-' && strings[0].charAt(1) != '-') {
            // -v1--v2
            int index = strings[0].indexOf("--");
            if (index != -1) {
                return new double[]{convertToDouble(strings[0].substring(0, index)), convertToDouble(strings[0].substring(index + 1))};
            }
        }

        throw new ParameterException("unable convert " + strings[0] + " to Double-Double");
    }, null, 1, "<double>-<double>"),

    /**
     * label-array 值转换器
     * <p>
     * 输入格式: &lt;string&gt;:&lt;double&gt;-&lt;double&gt; &lt;string&gt;:&lt;double&gt;-&lt;double&gt; ...
     * <p>
     * 转换格式: Map&lt;String, double[]&gt;
     */
    LABEL_RANGE((Function<String[], Map<String, double[]>>) strings -> {
        Map<String, double[]> values = new LinkedHashMap<>(strings.length);

        for (String string : strings) {
            String[] groups = string.split(":", -1);

            if (groups.length != 2) {
                throw new ParameterException(string + " not in <string>:<double>-<double> format");
            }

            if (values.containsKey(groups[0])) {
                throw new ParameterException("key " + groups[0] + " is set repeatedly");
            }
            values.put(groups[0], (double[]) RANGE.convert(groups[1]));
        }
        return Collections.unmodifiableMap(values);
    }, null, -1, "<string>:<double>-<double> <string>:<double>-<double> ..."),

    /**
     * label-array 值转换器
     * <p>
     * 输入格式: &lt;string&gt;:&lt;double&gt;-&lt;double&gt;,&lt;string&gt;:&lt;double&gt;-&lt;double&gt;,...
     * <p>
     * 转换格式: Map&lt;String, double[]&gt;
     */
    LABEL_RANGE_COMMA((Function<String[], Map<String, double[]>>) strings -> (Map<String, double[]>) LABEL_RANGE.convert(strings[0].split(",")), null, 1, "<string>:<double>-<double>,<string>:<double>-<double>,..."),

    /**
     * label-array 值转换器
     * <p>
     * 输入格式: &lt;string&gt;:&lt;double&gt;-&lt;double&gt;;&lt;string&gt;:&lt;double&gt;-&lt;double&gt;;...
     * <p>
     * 转换格式: Map&lt;String, double[]&gt;
     */
    LABEL_RANGE_SEMICOLON((Function<String[], Map<String, double[]>>) strings -> (Map<String, double[]>) LABEL_RANGE.convert(strings[0].split(";")), null, 1, "<string>:<double>-<double>;<string>:<double>-<double>;..."),

    /**
     * label-array 值转换器
     * <p>
     * 输入格式: &lt;string&gt;:&lt;double&gt;,&lt;double&gt;,... &lt;string&gt;:&lt;double&gt;,&lt;double&gt;,... ...
     * <p>
     * 转换格式: Map&lt;String, double[]&gt;
     */
    LABEL_ARRAY((Function<String[], Map<String, double[]>>) strings -> {
        Map<String, double[]> values = new LinkedHashMap<>(strings.length);

        for (String string : strings) {
            String[] groups = string.split(":", -1);

            if (groups.length != 2) {
                throw new ParameterException(string + " not in <string>:<double>,<double>,... format");
            }

            if (values.containsKey(groups[0])) {
                throw new ParameterException("key " + groups[0] + " is set repeatedly");
            }

            if (groups[1].length() == 0) {
                values.put(groups[0], new double[0]);
            } else {
                values.put(groups[0], (double[]) ARRAY_COMMA.convert(groups[1]));
            }
        }
        return Collections.unmodifiableMap(values);
    }, null, -1, "<string>:<double>,<double>,... <string>:<double>,<double>,... ..."),

    /**
     * label-array 值转换器
     * <p>
     * 输入格式: &lt;string&gt;:&lt;double&gt;,&lt;double&gt;,...;&lt;string&gt;:&lt;double&gt;,&lt;double&gt;,...;...
     * <p>
     * 转换格式: Map&lt;String, double[]&gt;
     */
    LABEL_ARRAY_SEMICOLON((Function<String[], Map<String, double[]>>) strings -> (Map<String, double[]>) LABEL_ARRAY.convert(strings[0].split(";")), null, 1, "<string>:<double>,<double>,...;<string>:<double>,<double>,...;...");

    private final Function<String[], ?> converter;
    private final int defaultArity;
    private final Object defaultValue;
    private final String defaultFormat;

    DOUBLE(Function<String[], ?> converter, Object defaultValue, int defaultArity, String defaultFormat) {
        this.converter = converter;
        this.defaultArity = defaultArity;
        this.defaultValue = defaultValue;
        this.defaultFormat = defaultFormat;
    }

    /**
     * 将值转为 double 类型
     */
    private static double convertToDouble(String value) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            throw new ParameterException("unable convert " + value + " to a double value");
        }
    }

    @Override
    public Object convert(String... args) {
        return this.converter.apply(args);
    }

    @Override
    public DOUBLE getBaseValueType() {
        return DOUBLE.VALUE;
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
    public static IValidator validateWith(double minValue, double maxValue) {
        return new IValidator() {
            @Override
            public Object validate(String commandKey, Object params) {
                if (params instanceof Double) {
                    double value = (double) params;
                    if (value < minValue || value > maxValue) {
                        throw new ParameterException(commandKey + " is out of range [" + minValue + ", " + maxValue + "]");
                    }
                } else if (params instanceof double[]) {
                    double[] values = (double[]) params;
                    for (double value : values) {
                        if (value < minValue || value > maxValue) {
                            throw new ParameterException(commandKey + " is out of range [" + minValue + ", " + maxValue + "]");
                        }
                    }
                } else if (params instanceof Set<?>) {
                    Set<Double> values = (Set<Double>) params;
                    for (double value : values) {
                        if (value < minValue || value > maxValue) {
                            throw new ParameterException(commandKey + " is out of range [" + minValue + ", " + maxValue + "]");
                        }
                    }
                } else if (params instanceof Map<?, ?>) {
                    Map<String, ?> values = (Map<String, ?>) params;
                    for (Object value : values.values()) {
                        if (value instanceof Double) {
                            if ((Double) value < minValue || (Double) value > maxValue) {
                                throw new ParameterException(commandKey + " is out of range [" + minValue + ", " + maxValue + "]");
                            }
                        } else if (value instanceof double[]) {
                            for (double v : (double[]) value) {
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
            public Double get(String key) {
                if (key.equalsIgnoreCase("min") || key.equalsIgnoreCase("minValue")) {
                    return minValue;
                } else if (key.equalsIgnoreCase("max") || key.equalsIgnoreCase("maxValue")) {
                    return maxValue;
                } else {
                    throw new CommandParserException("attribute " + key + " not found (support: min, max, minValue, maxValue)");
                }
            }

            @Override
            public DOUBLE getBaseValueType() {
                return DOUBLE.VALUE;
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
    public static IValidator validateWith(double minValue) {
        return new IValidator() {
            @Override
            public Object validate(String commandKey, Object params) {
                if (params instanceof Double) {
                    double value = (double) params;
                    if (value < minValue) {
                        throw new ParameterException(commandKey + " less than " + minValue);
                    }
                } else if (params instanceof double[]) {
                    double[] values = (double[]) params;
                    for (double value : values) {
                        if (value < minValue) {
                            throw new ParameterException(commandKey + " less than " + minValue);
                        }
                    }
                } else if (params instanceof Set<?>) {
                    Set<Double> values = (Set<Double>) params;
                    for (double value : values) {
                        if (value < minValue) {
                            throw new ParameterException(commandKey + " less than " + minValue);
                        }
                    }
                } else if (params instanceof Map<?, ?>) {
                    Map<String, ?> values = (Map<String, ?>) params;
                    for (Object value : values.values()) {
                        if (value instanceof Double) {
                            if ((Double) value < minValue) {
                                throw new ParameterException(commandKey + " less than " + minValue);
                            }
                        } else if (value instanceof double[]) {
                            for (double v : (double[]) value) {
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
            public Double get(String key) {
                if (key.equalsIgnoreCase("min") || key.equalsIgnoreCase("minValue")) {
                    return minValue;
                } else {
                    throw new CommandParserException("attribute " + key + " not found (support: min, minValue)");
                }
            }

            @Override
            public DOUBLE getBaseValueType() {
                return DOUBLE.VALUE;
            }

            @Override
            public String toString() {
                return ">= " + minValue;
            }
        };
    }

    @Override
    public String toString() {
        if (this.equals(DOUBLE.VALUE)) {
            return "DOUBLE";
        } else {
            return getDeclaringClass().getSimpleName() + "_" + super.toString();
        }
    }
}