package edu.sysu.pmglab.commandParser.types;

import edu.sysu.pmglab.commandParser.exception.CommandParserException;
import edu.sysu.pmglab.commandParser.exception.ParameterException;
import edu.sysu.pmglab.easytools.ArrayUtils;

import java.util.*;
import java.util.function.Function;

/**
 * @author suranyi
 */

public enum INTEGER implements IType {
    /**
     * 值转换器
     * <p>
     * 输入格式: &lt;int&gt;
     * <p>
     * 转换格式: Integer
     */
    VALUE((Function<String[], Integer>) strings -> convertToInteger(strings[0]), 0, 1, "<int>"),

    /**
     * array 值转换器
     * <p>
     * 输入格式: &lt;int&gt; &lt;int&gt; ...
     * <p>
     * 转换格式: int[]
     */
    ARRAY((Function<String[], int[]>) strings -> {
        int index = 0;
        int[] values = new int[strings.length];
        for (String string : strings) {
            values[index++] = convertToInteger(string);
        }
        return values;
    }, null, -1, "<int> <int> ..."),

    /**
     * array 值转换器
     * <p>
     * 输入格式: &lt;int&gt;,&lt;int&gt;,...
     * <p>
     * 转换格式: int[]
     */
    ARRAY_COMMA((Function<String[], int[]>) strings -> (int[]) ARRAY.convert(strings[0].split(",")), null, 1, "<int>,<int>,..."),

    /**
     * array 值转换器
     * <p>
     * 输入格式: &lt;int&gt;;&lt;int&gt;;...
     * <p>
     * 转换格式: int[]
     */
    ARRAY_SEMICOLON((Function<String[], int[]>) strings -> (int[]) ARRAY.convert(strings[0].split(";")), null, 1, "<int>;<int>;..."),

    /**
     * set 值转换器
     * <p>
     * 输入格式: &lt;int&gt; &lt;int&gt; ...
     * <p>
     * 转换格式: Set&lt;Integer&gt;
     */
    SET((Function<String[], Set<Integer>>) strings -> {
        Set<Integer> values = new HashSet<>(2);
        for (String string : strings) {
            values.add(convertToInteger(string));
        }
        return Collections.unmodifiableSet(values);
    }, null, -1, "<int> <int> ..."),

    /**
     * set 值转换器
     * <p>
     * 输入格式: &lt;int&gt;,&lt;int&gt;,...
     * <p>
     * 转换格式: Set&lt;Integer&gt;
     */
    SET_COMMA((Function<String[], Set<Integer>>) strings -> (Set<Integer>) SET.convert(strings[0].split(",")), null, 1, "<int>,<int>,..."),

    /**
     * set 值转换器
     * <p>
     * 输入格式: &lt;int&gt;;&lt;int&gt;;...
     * <p>
     * 转换格式: Set&lt;Integer&gt;
     */
    SET_SEMICOLON((Function<String[], Set<Integer>>) strings -> (Set<Integer>) SET.convert(strings[0].split(";")), null, 1, "<int>;<int>;..."),

    /**
     * map 值转换器
     * <p>
     * 输入格式: &lt;string&gt;=&lt;int&gt; &lt;string&gt;=&lt;int&gt; ...
     * <p>
     * 转换格式: Map&lt;String, Integer&gt;
     */
    MAP((Function<String[], Map<String, Integer>>) strings -> {
        Map<String, Integer> maps = new HashMap<>(strings.length);
        for (String string : strings) {
            if (string.length() > 0) {
                String[] groups = string.split("=", -1);

                if (groups.length == 2) {
                    // K=V 形式
                    if (maps.containsKey(groups[0])) {
                        throw new ParameterException("key " + groups[0] + " is set repeatedly");
                    }

                    maps.put(groups[0], convertToInteger(groups[1]));
                } else {
                    throw new ParameterException(string + " not in <string>=<int> format");
                }
            }
        }
        return Collections.unmodifiableMap(maps);
    }, null, -1, "<string>=<int> <string>=<int> ..."),

    /**
     * map 值转换器
     * <p>
     * 输入格式: &lt;string&gt;=&lt;int&gt;,&lt;string&gt;=&lt;int&gt;,...
     * <p>
     * 转换格式: Map&lt;String, Integer&gt;
     */
    MAP_COMMA((Function<String[], Map<String, Integer>>) strings -> (Map<String, Integer>) MAP.convert(strings[0].split(",")), null, 1, "<string>=<int>,<string>=<int>,..."),

    /**
     * map 值转换器
     * <p>
     * 输入格式: &lt;string&gt;=&lt;int&gt;;&lt;string&gt;=&lt;int&gt;;...
     * <p>
     * 转换格式: Map&lt;String, Integer&gt;
     */
    MAP_SEMICOLON((Function<String[], Map<String, Integer>>) strings -> (Map<String, Integer>) MAP.convert(strings[0].split(";")), null, 1, "<string>=<int>;<string>=<int>;..."),

    /**
     * range 值转换器
     * <p>
     * 输入格式: &lt;int&gt;-&lt;int&gt;
     * <p>
     * 转换格式: int[]
     */
    RANGE((Function<String[], int[]>) strings -> {
        int count = ArrayUtils.valueCounts(strings[0], '-');
        if (count == 1) {
            // v1-v2 型号
            String[] parsed = strings[0].split("-", -1);
            return new int[]{parsed[0].length() == 0 ? Integer.MIN_VALUE : convertToInteger(parsed[0]),
                    parsed[1].length() == 0 ? Integer.MAX_VALUE : convertToInteger(parsed[1])};
        } else if (count == 2) {
            if (strings[0].length() == 2) {
                // --
                throw new ParameterException("unable convert -- to Integer-Integer");
            }

            if (strings[0].charAt(0) == '-') {
                if (strings[0].charAt(1) == '-') {
                    // --v1
                    return new int[]{Integer.MIN_VALUE, convertToInteger(strings[0].substring(1))};
                } else if (strings[0].charAt(strings[0].length() - 1) == '-') {
                    // -v1-
                    return new int[]{convertToInteger(strings[0].substring(0, strings[0].length() - 1)), Integer.MAX_VALUE};
                } else {
                    // -v1-v2
                    int index = strings[0].indexOf('-', 1);
                    return new int[]{convertToInteger(strings[0].substring(0, index)), convertToInteger(strings[0].substring(index + 1))};
                }
            } else {
                // v1--v2
                int index = strings[0].indexOf("--");
                if (index != -1) {
                    return new int[]{convertToInteger(strings[0].substring(0, index)), convertToInteger(strings[0].substring(index + 1))};
                }
            }
        } else if (count == 3 && strings[0].charAt(0) == '-' && strings[0].charAt(1) != '-') {
            // -v1--v2
            int index = strings[0].indexOf("--");
            if (index != -1) {
                return new int[]{convertToInteger(strings[0].substring(0, index)), convertToInteger(strings[0].substring(index + 1))};
            }
        }

        throw new ParameterException("unable convert " + strings[0] + " to Integer-Integer");
    }, null, 1, "<int>-<int>"),

    /**
     * label-array 值转换器
     * <p>
     * 输入格式: &lt;string&gt;:&lt;int&gt;-&lt;int&gt; &lt;string&gt;:&lt;int&gt;-&lt;int&gt; ...
     * <p>
     * 转换格式: Map&lt;String, int[]&gt;
     */
    LABEL_RANGE((Function<String[], Map<String, int[]>>) strings -> {
        Map<String, int[]> values = new HashMap<>(strings.length);

        for (String string : strings) {
            String[] groups = string.split(":", -1);

            if (groups.length != 2) {
                throw new ParameterException(string + " not in <string>:<int>-<int> format");
            }

            if (values.containsKey(groups[0])) {
                throw new ParameterException("key " + groups[0] + " is set repeatedly");
            }
            values.put(groups[0], (int[]) RANGE.convert(groups[1]));
        }
        return Collections.unmodifiableMap(values);
    }, null, -1, "<string>:<int>-<int> <string>:<int>-<int> ..."),

    /**
     * label-array 值转换器
     * <p>
     * 输入格式: &lt;string&gt;:&lt;int&gt;-&lt;int&gt;,&lt;string&gt;:&lt;int&gt;-&lt;int&gt;,...
     * <p>
     * 转换格式: Map&lt;String, int[]&gt;
     */
    LABEL_RANGE_COMMA((Function<String[], Map<String, int[]>>) strings -> (Map<String, int[]>) LABEL_RANGE.convert(strings[0].split(",")), null, 1, "<string>:<int>-<int>,<string>:<int>-<int>,..."),

    /**
     * label-array 值转换器
     * <p>
     * 输入格式: &lt;string&gt;:&lt;int&gt;-&lt;int&gt;;&lt;string&gt;:&lt;int&gt;-&lt;int&gt;;...
     * <p>
     * 转换格式: Map&lt;String, int[]&gt;
     */
    LABEL_RANGE_SEMICOLON((Function<String[], Map<String, int[]>>) strings -> (Map<String, int[]>) LABEL_RANGE.convert(strings[0].split(";")), null, 1, "<string>:<int>-<int>;<string>:<int>-<int>;..."),

    /**
     * label-array 值转换器
     * <p>
     * 输入格式: &lt;string&gt;:&lt;int&gt;,&lt;int&gt;,... &lt;string&gt;:&lt;int&gt;,&lt;int&gt;,... ...
     * <p>
     * 转换格式: Map&lt;String, int[]&gt;
     */
    LABEL_ARRAY((Function<String[], Map<String, int[]>>) strings -> {
        Map<String, int[]> values = new HashMap<>(strings.length);

        for (String string : strings) {
            String[] groups = string.split(":", -1);

            if (groups.length != 2) {
                throw new ParameterException(string + " not in <string>:<int>,<int>,... format");
            }

            if (values.containsKey(groups[0])) {
                throw new ParameterException("key " + groups[0] + " is set repeatedly");
            }

            if (groups[1].length() == 0) {
                values.put(groups[0], new int[0]);
            } else {
                values.put(groups[0], (int[]) ARRAY_COMMA.convert(groups[1]));
            }
        }
        return Collections.unmodifiableMap(values);
    }, null, -1, "<string>:<int>,<int>,... <string>:<int>,<int>,... ..."),

    /**
     * label-array 值转换器
     * <p>
     * 输入格式: &lt;string&gt;:&lt;int&gt;,&lt;int&gt;,...;&lt;string&gt;:&lt;int&gt;,&lt;int&gt;,...;...
     * <p>
     * 转换格式: Map&lt;String, int[]&gt;
     */
    LABEL_ARRAY_SEMICOLON((Function<String[], Map<String, int[]>>) strings -> (Map<String, int[]>) LABEL_ARRAY.convert(strings[0].split(";")), null, 1, "<string>:<int>,<int>,...;<string>:<int>,<int>,...;...");

    private final Function<String[], ?> converter;
    private final int defaultArity;
    private final Object defaultValue;
    private final String defaultFormat;

    INTEGER(Function<String[], ?> converter, Object defaultValue, int defaultArity, String defaultFormat) {
        this.converter = converter;
        this.defaultArity = defaultArity;
        this.defaultValue = defaultValue;
        this.defaultFormat = defaultFormat;
    }

    /**
     * 将值转为 int 类型
     */
    private static int convertToInteger(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new ParameterException("unable convert " + value + " to a int value");
        }
    }

    @Override
    public Object convert(String... args) {
        return this.converter.apply(args);
    }

    @Override
    public IType getBaseValueType() {
        return INTEGER.VALUE;
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
    public static IValidator validateWith(int minValue, int maxValue) {
        return new IValidator() {
            @Override
            public Object validate(String commandKey, Object params) {
                if (params instanceof Integer) {
                    int value = (int) params;
                    if (value < minValue || value > maxValue) {
                        throw new ParameterException(commandKey + " is out of range [" + minValue + ", " + maxValue + "]");
                    }
                } else if (params instanceof int[]) {
                    int[] values = (int[]) params;
                    for (int value : values) {
                        if (value < minValue || value > maxValue) {
                            throw new ParameterException(commandKey + " is out of range [" + minValue + ", " + maxValue + "]");
                        }
                    }
                } else if (params instanceof Set<?>) {
                    Set<Integer> values = (Set<Integer>) params;
                    for (int value : values) {
                        if (value < minValue || value > maxValue) {
                            throw new ParameterException(commandKey + " is out of range [" + minValue + ", " + maxValue + "]");
                        }
                    }
                } else if (params instanceof Map<?, ?>) {
                    Map<String, ?> values = (Map<String, ?>) params;
                    for (Object value : values.values()) {
                        if (value instanceof Integer) {
                            if ((Integer) value < minValue || (Integer) value > maxValue) {
                                throw new ParameterException(commandKey + " is out of range [" + minValue + ", " + maxValue + "]");
                            }
                        } else if (value instanceof int[]) {
                            for (int v : (int[]) value) {
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
            public Integer get(String key) {
                if (key.equalsIgnoreCase("min") || key.equalsIgnoreCase("minValue")) {
                    return minValue;
                } else if (key.equalsIgnoreCase("max") || key.equalsIgnoreCase("maxValue")) {
                    return maxValue;
                } else {
                    throw new CommandParserException("attribute " + key + " not found (support: min, max, minValue, maxValue)");
                }
            }

            @Override
            public INTEGER getBaseValueType() {
                return INTEGER.VALUE;
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
    public static IValidator validateWith(int minValue) {
        return new IValidator() {
            @Override
            public Object validate(String commandKey, Object params) {
                if (params instanceof Integer) {
                    int value = (int) params;
                    if (value < minValue) {
                        throw new ParameterException(commandKey + " less than " + minValue);
                    }
                } else if (params instanceof int[]) {
                    int[] values = (int[]) params;
                    for (int value : values) {
                        if (value < minValue) {
                            throw new ParameterException(commandKey + " less than " + minValue);
                        }
                    }
                } else if (params instanceof Set<?>) {
                    Set<Integer> values = (Set<Integer>) params;
                    for (int value : values) {
                        if (value < minValue) {
                            throw new ParameterException(commandKey + " less than " + minValue);
                        }
                    }
                } else if (params instanceof Map<?, ?>) {
                    Map<String, ?> values = (Map<String, ?>) params;
                    for (Object value : values.values()) {
                        if (value instanceof Integer) {
                            if ((Integer) value < minValue) {
                                throw new ParameterException(commandKey + " less than " + minValue);
                            }
                        } else if (value instanceof int[]) {
                            for (int v : (int[]) value) {
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
            public Integer get(String key) {
                if (key.equalsIgnoreCase("min") || key.equalsIgnoreCase("minValue")) {
                    return minValue;
                } else {
                    throw new CommandParserException("attribute " + key + " not found (support: min, minValue)");
                }
            }

            @Override
            public INTEGER getBaseValueType() {
                return INTEGER.VALUE;
            }

            @Override
            public String toString() {
                return ">= " + minValue;
            }
        };
    }

    @Override
    public String toString() {
        if (this.equals(INTEGER.VALUE)) {
            return "INTEGER";
        } else {
            return getDeclaringClass().getSimpleName() + "_" + super.toString();
        }
    }
}