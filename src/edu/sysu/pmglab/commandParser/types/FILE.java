package edu.sysu.pmglab.commandParser.types;

import edu.sysu.pmglab.commandParser.exception.CommandParserException;
import edu.sysu.pmglab.commandParser.exception.ParameterException;
import edu.sysu.pmglab.container.File;
import edu.sysu.pmglab.container.array.StringArray;

import java.util.*;
import java.util.function.Function;

/**
 * @author suranyi
 */

public enum FILE implements IType {
    /**
     * 值转换器
     * <p>
     * 输入格式: &lt;File&gt;
     * <p>
     * 转换格式: File
     */
    VALUE((Function<String[], File>) strings -> convertToFile(strings[0]), null, 1, "<file>"),

    /**
     * array 值转换器
     * <p>
     * 输入格式: &lt;File&gt; &lt;File&gt; ...
     * <p>
     * 转换格式: File[]
     */
    ARRAY((Function<String[], File[]>) File::of, null, -1, "<file> <file> ..."),

    /**
     * set 值转换器
     * <p>
     * 输入格式: &lt;File&gt; &lt;File&gt; ...
     * <p>
     * 转换格式: Set&lt;File&gt;
     */
    SET((Function<String[], Set<File>>) strings -> {
        Set<File> values = new HashSet<>();
        for (String string : strings) {
            values.add(convertToFile(string));
        }
        return Collections.unmodifiableSet(values);
    }, null, -1, "<file> <file> ..."),

    /**
     * map 值转换器
     * <p>
     * 输入格式: &lt;string&gt;=&lt;File&gt; &lt;string&gt;=&lt;File&gt; ...
     * <p>
     * 转换格式: Map&lt;String, File&gt;
     */
    MAP((Function<String[], Map<String, File>>) strings -> {
        Map<String, File> maps = new HashMap<>(strings.length);
        for (String string : strings) {
            if (string.length() > 0) {
                int index = string.indexOf("=");
                if (index == -1) {
                    // K, 则 V 默认为 null
                    throw new ParameterException(string + " not in <string>=<file> format");
                } else {
                    String key = string.substring(0, index);
                    if (maps.containsKey(key)) {
                        throw new ParameterException("key " + key + " is set repeatedly");
                    }

                    maps.put(key, convertToFile(string.substring(index + 1)));
                }
            }
        }
        return Collections.unmodifiableMap(maps);
    }, null, -1, "<string>=<file> <string>=<file> ...");

    private final Function<String[], ?> converter;
    private final int defaultArity;
    private final Object defaultValue;
    private final String defaultFormat;

    FILE(Function<String[], ?> converter, Object defaultValue, int defaultArity, String defaultFormat) {
        this.converter = converter;
        this.defaultArity = defaultArity;
        this.defaultValue = defaultValue;
        this.defaultFormat = defaultFormat;
    }

    /**
     * 将值转为 File 类型
     */
    private static File convertToFile(String value) {
        return new File(value);
    }

    @Override
    public Object convert(String... args) {
        return this.converter.apply(args);
    }

    @Override
    public FILE getBaseValueType() {
        return FILE.VALUE;
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
     * @param checkIsExists 检查文件是否存在
     * @return 文件验证器
     */
    public static IValidator validateWith(boolean checkIsExists) {
        return validateWith(checkIsExists, false, false, false);
    }

    /**
     * 值验证器
     *
     * @param checkIsExists 检查文件是否存在
     * @param checkIsFile   检查是否为文件路径
     * @return 文件验证器
     */
    public static IValidator validateWith(boolean checkIsExists, boolean checkIsFile) {
        return validateWith(checkIsExists, checkIsFile, false, false);
    }

    /**
     * 值验证器
     *
     * @param checkIsExists    检查文件是否存在
     * @param checkIsFile      检查是否为文件路径
     * @param checkIsDirectory 检查是否为文件夹路径
     * @return 文件验证器
     */
    public static IValidator validateWith(boolean checkIsExists, boolean checkIsFile, boolean checkIsDirectory) {
        return validateWith(checkIsExists, checkIsFile, checkIsDirectory, false);
    }

    /**
     * 值验证器
     *
     * @param checkIsExists      检查文件是否存在
     * @param checkIsFile        检查是否为文件路径
     * @param checkIsDirectory   检查是否为文件夹路径
     * @param checkInnerResource 检查内部资源文件
     * @return 文件验证器
     */
    public static IValidator validateWith(boolean checkIsExists, boolean checkIsFile, boolean checkIsDirectory, boolean checkInnerResource) {
        if (checkIsFile && checkIsDirectory) {
            throw new CommandParserException("illegal validator: checkIsFile and checkIsDirectory cannot both be true");
        }

        return new IValidator() {
            @Override
            public Object validate(String commandKey, Object params) {
                if (params instanceof File) {
                    File value = (File) params;
                    if (checkInnerResource) {
                        File innerFile = new File(value.getFilePath(), true);
                        if (innerFile.isInnerResource()) {
                            // 如果识别为了内部资源, 则使用此内部资源替代文件
                            value = innerFile;

                            // 此外, 无需对内部资源判断类型或存在与否
                            return value;
                        }
                    }

                    if (checkIsExists) {
                        if (!value.isExists()) {
                            throw new ParameterException("no such file or directory (" + value.getFilePath() + ")");
                        }
                    }

                    if (checkIsFile) {
                        if (value.isDirectory()) {
                            throw new ParameterException(value.getFilePath() + " is a directory (single file required)");
                        }
                    }

                    if (checkIsDirectory) {
                        if (value.isExists() && !value.isDirectory()) {
                            throw new ParameterException(value.getFilePath() + " is not a directory");
                        }
                    }
                    return value;
                } else if (params instanceof File[]) {
                    File[] values = (File[]) params;
                    File[] valuesParsed = new File[values.length];
                    int index = 0;
                    for (File value : values) {
                        if (checkInnerResource) {
                            File innerFile = new File(value.getFilePath(), true);
                            if (innerFile.isInnerResource()) {
                                // 如果识别为了内部资源, 则使用此内部资源替代文件
                                valuesParsed[index++] = innerFile;
                                continue;
                            }
                        }

                        if (checkIsExists) {
                            if (!value.isExists()) {
                                throw new ParameterException("no such file or directory (" + value.getFilePath() + ")");
                            }
                        }

                        if (checkIsFile) {
                            if (value.isDirectory()) {
                                throw new ParameterException(value.getFilePath() + " is a directory (single file required)");
                            }
                        }

                        if (checkIsDirectory) {
                            if (value.isExists() && !value.isDirectory()) {
                                throw new ParameterException(value.getFilePath() + " is not a directory");
                            }
                        }

                        valuesParsed[index++] = value;
                    }

                    return valuesParsed;
                } else if (params instanceof Set<?>) {
                    Set<File> values = (Set<File>) params;
                    Set<File> valuesParsed = new HashSet<>(values.size());
                    for (File value : values) {
                        if (checkInnerResource) {
                            File innerFile = new File(value.getFilePath(), true);
                            if (innerFile.isInnerResource()) {
                                // 如果识别为了内部资源, 则使用此内部资源替代文件
                                valuesParsed.add(innerFile);
                                continue;
                            }
                        }

                        if (checkIsExists) {
                            if (!value.isExists()) {
                                throw new ParameterException("no such file or directory (" + value.getFilePath() + ")");
                            }
                        }

                        if (checkIsFile) {
                            if (value.isDirectory()) {
                                throw new ParameterException(value.getFilePath() + " is a directory (single file required)");
                            }
                        }

                        if (checkIsDirectory) {
                            if (value.isExists() && !value.isDirectory()) {
                                throw new ParameterException(value.getFilePath() + " is not a directory");
                            }
                        }

                        valuesParsed.add(value);
                    }

                    return Collections.unmodifiableSet(valuesParsed);
                } else if (params instanceof Map<?, ?>) {
                    Map<String, File> values = (Map<String, File>) params;
                    Map<String, File> valuesParsed = new HashMap<>();
                    for (String key : values.keySet()) {
                        File value = values.get(key);
                        if (checkInnerResource) {
                            File innerFile = new File(value.getFilePath(), true);
                            if (innerFile.isInnerResource()) {
                                // 如果识别为了内部资源, 则使用此内部资源替代文件
                                valuesParsed.put(key, innerFile);
                                continue;
                            }
                        }

                        if (checkIsExists) {
                            if (!value.isExists()) {
                                throw new ParameterException("no such file or directory (" + value.getFilePath() + ")");
                            }
                        }

                        if (checkIsFile) {
                            if (value.isDirectory()) {
                                throw new ParameterException(value.getFilePath() + " is a directory (single file required)");
                            }
                        }

                        if (checkIsDirectory) {
                            if (value.isExists() && !value.isDirectory()) {
                                throw new ParameterException(value.getFilePath() + " is not a directory");
                            }
                        }

                        valuesParsed.put(key, value);
                    }

                    return Collections.unmodifiableMap(valuesParsed);
                } else {
                    throw new ParameterException("unable to infer the value type of " + commandKey);
                }
            }

            @Override
            public Boolean get(String key) {
                if (key.equalsIgnoreCase("checkIsExists")) {
                    return checkIsExists;
                } else if (key.equalsIgnoreCase("checkIsFile")) {
                    return checkIsFile;
                } else if (key.equalsIgnoreCase("checkIsDirectory")) {
                    return checkIsDirectory;
                } else if (key.equalsIgnoreCase("checkInnerResource")) {
                    return checkInnerResource;
                } else {
                    throw new CommandParserException("attribute " + key + " not found (support: checkIsExists, checkIsFile, checkIsDirectory, checkInnerResource)");
                }
            }

            @Override
            public FILE getBaseValueType() {
                return FILE.VALUE;
            }

            @Override
            public String toString() {
                StringArray options = new StringArray();
                if (checkIsExists) {
                    options.add("Exists");
                }
                if (checkIsFile) {
                    options.add("File");
                }
                if (checkIsDirectory) {
                    options.add("Directory");
                }
                if (checkInnerResource) {
                    options.add("Inner");
                }
                return options.join(",");
            }
        };
    }

    @Override
    public String toString() {
        if (this.equals(FILE.VALUE)) {
            return "FILE";
        } else {
            return getDeclaringClass().getSimpleName() + "_" + super.toString();
        }
    }
}
