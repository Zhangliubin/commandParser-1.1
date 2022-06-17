package edu.sysu.pmglab.commandParser.types;

import edu.sysu.pmglab.commandParser.exception.ParameterException;

import java.util.*;
import java.util.function.Function;

/**
 * @author suranyi
 */

public enum BOOLEAN implements IType {
    /**
     * 值转换器
     * <p>
     * 输入格式: [true/false]
     * <p>
     * 转换格式: Boolean
     */
    VALUE((Function<String[], Boolean>) strings -> convertToBoolean(strings[0]), false, 1, "[true/false]"),

    /**
     * array 值转换器
     * <p>
     * 输入格式: &lt;bool&gt; &lt;bool&gt; ...
     * <p>
     * 转换格式: boolean[]
     */
    ARRAY((Function<String[], boolean[]>) strings -> {
        int index = 0;
        boolean[] values = new boolean[strings.length];
        for (String string : strings) {
            values[index++] = convertToBoolean(string);
        }
        return values;
    }, null, -1, "<bool> <bool> ..."),

    /**
     * array 值转换器
     * <p>
     * 输入格式: &lt;bool&gt;,&lt;bool&gt; ...
     * <p>
     * 转换格式: boolean[]
     */
    ARRAY_COMMA((Function<String[], boolean[]>) strings -> (boolean[]) ARRAY.convert(strings[0].split(",")), null, 1, "<bool>,<bool>,..."),

    /**
     * array 值转换器
     * <p>
     * 输入格式: &lt;bool&gt;;&lt;bool&gt;;...
     * <p>
     * 转换格式: boolean[]
     */
    ARRAY_SEMICOLON((Function<String[], boolean[]>) strings -> (boolean[]) ARRAY.convert(strings[0].split(";")), null, 1, "<bool>;<bool>;..."),

    /**
     * set 值转换器
     * <p>
     * 输入格式: &lt;bool&gt; &lt;bool&gt; ...
     * <p>
     * 转换格式: Set&lt;Boolean&gt;
     */
    SET((Function<String[], Set<Boolean>>) strings -> {
        Set<Boolean> values = new HashSet<>(2);
        for (String string : strings) {
            values.add(convertToBoolean(string));
        }
        return Collections.unmodifiableSet(values);
    }, null, -1, "<bool> <bool> ..."),

    /**
     * set 值转换器
     * <p>
     * 输入格式: &lt;bool&gt;,&lt;bool&gt; ...
     * <p>
     * 转换格式: Set&lt;Boolean&gt;
     */
    SET_COMMA((Function<String[], Set<Boolean>>) strings -> (Set<Boolean>) SET.convert(strings[0].split(",")), null, 1, "<bool>,<bool>,..."),

    /**
     * set 值转换器
     * <p>
     * 输入格式: &lt;bool&gt;;&lt;bool&gt;;...
     * <p>
     * 转换格式: Set&lt;Boolean&gt;
     */
    SET_SEMICOLON((Function<String[], Set<Boolean>>) strings -> (Set<Boolean>) SET.convert(strings[0].split(";")), null, 1, "<bool>;<bool>;..."),

    /**
     * map 值转换器
     * <p>
     * 输入格式: &lt;string&gt;=&lt;bool&gt; &lt;string&gt;=&lt;bool&gt; ...
     * <p>
     * 转换格式: Map&lt;String, Boolean&gt;
     */
    MAP((Function<String[], Map<String, Boolean>>) strings -> {
        Map<String, Boolean> maps = new HashMap<>(strings.length);
        for (String string : strings) {
            if (string.length() > 0) {
                String[] groups = string.split("=", -1);

                if (groups.length == 2) {
                    // K=V 形式
                    if (maps.containsKey(groups[0])) {
                        throw new ParameterException("key " + groups[0] + " is set repeatedly");
                    }
                    maps.put(groups[0], convertToBoolean(groups[1]));
                } else {
                    throw new ParameterException(string + " not in <string>=<bool> format");
                }
            }
        }
        return Collections.unmodifiableMap(maps);
    }, null, -1, "<string>=<bool> <string>=<bool> ..."),

    /**
     * map 值转换器
     * <p>
     * 输入格式: &lt;string&gt;=&lt;bool&gt;,&lt;string&gt;=&lt;bool&gt;,...
     * <p>
     * 转换格式: Map&lt;String, Boolean&gt;
     */
    MAP_COMMA((Function<String[], Map<String, Boolean>>) strings -> (Map<String, Boolean>) MAP.convert(strings[0].split(",")), null, 1, "<string>=<bool>,<string>=<bool>,..."),

    /**
     * map 值转换器
     * <p>
     * 输入格式: &lt;string&gt;=&lt;bool&gt;;&lt;string&gt;=&lt;bool&gt;;...
     * <p>
     * 转换格式: Map&lt;String, Boolean&gt;
     */
    MAP_SEMICOLON((Function<String[], Map<String, Boolean>>) strings -> (Map<String, Boolean>) MAP.convert(strings[0].split(";")), null, 1, "<string>=<bool>;<string>=<bool>;..."),

    /**
     * label-array 值转换器
     * <p>
     * 输入格式: &lt;string&gt;:&lt;bool&gt;,&lt;bool&gt;,... &lt;string&gt;:&lt;bool&gt;,&lt;bool&gt;,... ...
     * <p>
     * 转换格式: Map&lt;String, boolean[]&gt;
     */
    LABEL_ARRAY((Function<String[], Map<String, boolean[]>>) strings -> {
        Map<String, boolean[]> values = new HashMap<>(strings.length);

        for (String string : strings) {
            String[] groups = string.split(":", -1);
            if (groups.length != 2) {
                throw new ParameterException(string + " not in <string>:<bool>,<bool>,... format");
            }

            if (values.containsKey(groups[0])) {
                throw new ParameterException("key " + groups[0] + " is set repeatedly");
            }

            if (groups[1].length() == 0) {
                values.put(groups[0], new boolean[0]);
            } else {
                values.put(groups[0], (boolean[]) ARRAY_COMMA.convert(groups[1]));
            }
        }
        return Collections.unmodifiableMap(values);
    }, null, -1, "<string>:<bool>,<bool>,... <string>:<bool>,<bool>,... ..."),

    /**
     * label-array 值转换器
     * <p>
     * 输入格式: &lt;string&gt;:&lt;bool&gt;,&lt;bool&gt;,...;&lt;string&gt;:&lt;bool&gt;,&lt;bool&gt;,...;...
     * <p>
     * 转换格式: Map&lt;String, boolean[]&gt;
     */
    LABEL_ARRAY_SEMICOLON((Function<String[], Map<String, boolean[]>>) strings -> (Map<String, boolean[]>) LABEL_ARRAY.convert(strings[0].split(";")), null, 1, "<string>:<bool>,<bool>,...;<string>:<bool>,<bool>,...;...");

    private final Function<String[], ?> converter;
    private final int defaultArity;
    private final Object defaultValue;
    private final String defaultFormat;

    BOOLEAN(Function<String[], ?> converter, Object defaultValue, int defaultArity, String defaultFormat) {
        this.converter = converter;
        this.defaultArity = defaultArity;
        this.defaultValue = defaultValue;
        this.defaultFormat = defaultFormat;
    }

    /**
     * 将值转为 boolean 类型
     */
    private static boolean convertToBoolean(String value) {
        if ("true".equalsIgnoreCase(value)) {
            return true;
        } else if ("false".equalsIgnoreCase(value)) {
            return false;
        } else {
            throw new ParameterException("unable convert " + value + " to a boolean value");
        }
    }

    @Override
    public Object convert(String... args) {
        return this.converter.apply(args);
    }

    @Override
    public BOOLEAN getBaseValueType() {
        return BOOLEAN.VALUE;
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

    @Override
    public String toString() {
        if (this.equals(BOOLEAN.VALUE)) {
            return "BOOLEAN";
        } else {
            return getDeclaringClass().getSimpleName() + "_" + super.toString();
        }
    }
}
