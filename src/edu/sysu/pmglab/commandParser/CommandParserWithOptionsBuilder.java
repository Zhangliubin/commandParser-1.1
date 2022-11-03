package edu.sysu.pmglab.commandParser;

import edu.sysu.pmglab.commandParser.exception.CommandParserException;
import edu.sysu.pmglab.commandParser.types.*;
import edu.sysu.pmglab.commandParser.usage.DefaultStyleUsage;
import edu.sysu.pmglab.container.array.StringArray;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

/**
 * 带有参数构造的命令行解析器生成文件构造器
 */

public class CommandParserWithOptionsBuilder {
    final CommandParser parser;
    final String packagePath;
    final String className;

    private final static Pattern CLASS_NAME_RULE = Pattern.compile("(^[a-zA-Z]+[0-9a-zA-Z_]*(\\.[a-zA-Z]+[0-9a-zA-Z_]*)*\\.[a-zA-Z]+[0-9a-zA-Z_]*$)");
    private final static HashMap<IType, String> formatter = new LinkedHashMap<>();

    static {
        // 强制格式转换器
        formatter.put(IType.NONE, "?");

        formatter.put(BOOLEAN.VALUE, "Boolean");
        formatter.put(BOOLEAN.ARRAY, "boolean[]");
        formatter.put(BOOLEAN.ARRAY_COMMA, "boolean[]");
        formatter.put(BOOLEAN.ARRAY_SEMICOLON, "boolean[]");
        formatter.put(BOOLEAN.SET, "Set<Boolean>");
        formatter.put(BOOLEAN.SET_COMMA, "Set<Boolean>");
        formatter.put(BOOLEAN.SET_SEMICOLON, "Set<Boolean>");
        formatter.put(BOOLEAN.MAP, "Map<String, Boolean>");
        formatter.put(BOOLEAN.MAP_COMMA, "Map<String, Boolean>");
        formatter.put(BOOLEAN.MAP_SEMICOLON, "Map<String, Boolean>");
        formatter.put(BOOLEAN.LABEL_ARRAY, "Map<String, boolean[]>");
        formatter.put(BOOLEAN.LABEL_ARRAY_SEMICOLON, "Map<String, boolean[]>");

        formatter.put(BYTE.VALUE, "Short");
        formatter.put(BYTE.ARRAY, "short[]");
        formatter.put(BYTE.ARRAY_COMMA, "short[]");
        formatter.put(BYTE.ARRAY_SEMICOLON, "short[]");
        formatter.put(BYTE.SET, "Set<Short>");
        formatter.put(BYTE.SET_COMMA, "Set<Short>");
        formatter.put(BYTE.SET_SEMICOLON, "Set<Short>");
        formatter.put(BYTE.MAP, "Map<String, Short>");
        formatter.put(BYTE.MAP_COMMA, "Map<String, Short>");
        formatter.put(BYTE.MAP_SEMICOLON, "Map<String, Short>");
        formatter.put(BYTE.RANGE, "Interval<Byte>");
        formatter.put(BYTE.LABEL_RANGE, "Map<String, Interval<Byte>>");
        formatter.put(BYTE.LABEL_RANGE_COMMA, "Map<String, Interval<Byte>>");
        formatter.put(BYTE.LABEL_RANGE_SEMICOLON, "Map<String, Interval<Byte>>");
        formatter.put(BYTE.LABEL_ARRAY, "Map<String, short[]>");
        formatter.put(BYTE.LABEL_ARRAY_SEMICOLON, "Map<String, short[]>");

        formatter.put(SHORT.VALUE, "Short");
        formatter.put(SHORT.ARRAY, "short[]");
        formatter.put(SHORT.ARRAY_COMMA, "short[]");
        formatter.put(SHORT.ARRAY_SEMICOLON, "short[]");
        formatter.put(SHORT.SET, "Set<Short>");
        formatter.put(SHORT.SET_COMMA, "Set<Short>");
        formatter.put(SHORT.SET_SEMICOLON, "Set<Short>");
        formatter.put(SHORT.MAP, "Map<String, Short>");
        formatter.put(SHORT.MAP_COMMA, "Map<String, Short>");
        formatter.put(SHORT.MAP_SEMICOLON, "Map<String, Short>");
        formatter.put(SHORT.RANGE, "Interval<Short>");
        formatter.put(SHORT.LABEL_RANGE, "Map<String, Interval<Short>>");
        formatter.put(SHORT.LABEL_RANGE_COMMA, "Map<String, Interval<Short>>");
        formatter.put(SHORT.LABEL_RANGE_SEMICOLON, "Map<String, Interval<Short>>");
        formatter.put(SHORT.LABEL_ARRAY, "Map<String, short[]>");
        formatter.put(SHORT.LABEL_ARRAY_SEMICOLON, "Map<String, short[]>");

        formatter.put(INTEGER.VALUE, "Integer");
        formatter.put(INTEGER.ARRAY, "int[]");
        formatter.put(INTEGER.ARRAY_COMMA, "int[]");
        formatter.put(INTEGER.ARRAY_SEMICOLON, "int[]");
        formatter.put(INTEGER.SET, "Set<Integer>");
        formatter.put(INTEGER.SET_COMMA, "Set<Integer>");
        formatter.put(INTEGER.SET_SEMICOLON, "Set<Integer>");
        formatter.put(INTEGER.MAP, "Map<String, Integer>");
        formatter.put(INTEGER.MAP_COMMA, "Map<String, Integer>");
        formatter.put(INTEGER.MAP_SEMICOLON, "Map<String, Integer>");
        formatter.put(INTEGER.RANGE, "Interval<Integer>");
        formatter.put(INTEGER.LABEL_RANGE, "Map<String, Interval<Integer>>");
        formatter.put(INTEGER.LABEL_RANGE_COMMA, "Map<String, Interval<Integer>>");
        formatter.put(INTEGER.LABEL_RANGE_SEMICOLON, "Map<String, Interval<Integer>>");
        formatter.put(INTEGER.LABEL_ARRAY, "Map<String, int[]>");
        formatter.put(INTEGER.LABEL_ARRAY_SEMICOLON, "Map<String, int[]>");

        formatter.put(LONG.VALUE, "Long");
        formatter.put(LONG.ARRAY, "long[]");
        formatter.put(LONG.ARRAY_COMMA, "long[]");
        formatter.put(LONG.ARRAY_SEMICOLON, "long[]");
        formatter.put(LONG.SET, "Set<Long>");
        formatter.put(LONG.SET_COMMA, "Set<Long>");
        formatter.put(LONG.SET_SEMICOLON, "Set<Long>");
        formatter.put(LONG.MAP, "Map<String, Long>");
        formatter.put(LONG.MAP_COMMA, "Map<String, Long>");
        formatter.put(LONG.MAP_SEMICOLON, "Map<String, Long>");
        formatter.put(LONG.RANGE, "Interval<Long>");
        formatter.put(LONG.LABEL_RANGE, "Map<String, Interval<Long>>");
        formatter.put(LONG.LABEL_RANGE_COMMA, "Map<String, Interval<Float>>");
        formatter.put(LONG.LABEL_RANGE_SEMICOLON, "Map<String, Interval<Float>>");
        formatter.put(LONG.LABEL_ARRAY, "Map<String, long[]>");
        formatter.put(LONG.LABEL_ARRAY_SEMICOLON, "Map<String, long[]>");

        formatter.put(FLOAT.VALUE, "Float");
        formatter.put(FLOAT.ARRAY, "float[]");
        formatter.put(FLOAT.ARRAY_COMMA, "float[]");
        formatter.put(FLOAT.ARRAY_SEMICOLON, "float[]");
        formatter.put(FLOAT.SET, "Set<Float>");
        formatter.put(FLOAT.SET_COMMA, "Set<Float>");
        formatter.put(FLOAT.SET_SEMICOLON, "Set<Float>");
        formatter.put(FLOAT.MAP, "Map<String, Float>");
        formatter.put(FLOAT.MAP_COMMA, "Map<String, Float>");
        formatter.put(FLOAT.MAP_SEMICOLON, "Map<String, Float>");
        formatter.put(FLOAT.RANGE, "Interval<Float>");
        formatter.put(FLOAT.LABEL_RANGE, "Map<String, Interval<Float>>");
        formatter.put(FLOAT.LABEL_RANGE_COMMA, "Map<String, Interval<Float>>");
        formatter.put(FLOAT.LABEL_RANGE_SEMICOLON, "Map<String, Interval<Float>>");
        formatter.put(FLOAT.LABEL_ARRAY, "Map<String, float[]>");
        formatter.put(FLOAT.LABEL_ARRAY_SEMICOLON, "Map<String, float[]>");

        formatter.put(DOUBLE.VALUE, "Double");
        formatter.put(DOUBLE.ARRAY, "double[]");
        formatter.put(DOUBLE.ARRAY_COMMA, "double[]");
        formatter.put(DOUBLE.ARRAY_SEMICOLON, "double[]");
        formatter.put(DOUBLE.SET, "Set<Double>");
        formatter.put(DOUBLE.SET_COMMA, "Set<Double>");
        formatter.put(DOUBLE.SET_SEMICOLON, "Set<Double>");
        formatter.put(DOUBLE.MAP, "Map<String, Double>");
        formatter.put(DOUBLE.MAP_COMMA, "Map<String, Double>");
        formatter.put(DOUBLE.MAP_SEMICOLON, "Map<String, Double>");
        formatter.put(DOUBLE.RANGE, "Interval<Double>");
        formatter.put(DOUBLE.LABEL_RANGE, "Map<String, Interval<Double>>");
        formatter.put(DOUBLE.LABEL_RANGE_COMMA, "Map<String, Interval<Double>>");
        formatter.put(DOUBLE.LABEL_RANGE_SEMICOLON, "Map<String, Interval<Double>>");
        formatter.put(DOUBLE.LABEL_ARRAY, "Map<String, double[]>");
        formatter.put(DOUBLE.LABEL_ARRAY_SEMICOLON, "Map<String, double[]>");

        formatter.put(STRING.VALUE, "String");
        formatter.put(STRING.ARRAY, "String[]");
        formatter.put(STRING.ARRAY_COMMA, "String[]");
        formatter.put(STRING.ARRAY_SEMICOLON, "String[]");
        formatter.put(STRING.SET, "Set<String>");
        formatter.put(STRING.SET_COMMA, "Set<String>");
        formatter.put(STRING.SET_SEMICOLON, "Set<String>");
        formatter.put(STRING.MAP, "Map<String, String>");
        formatter.put(STRING.MAP_COMMA, "Map<String, String>");
        formatter.put(STRING.MAP_SEMICOLON, "Map<String, String>");
        formatter.put(STRING.RANGE, "Interval<String>");
        formatter.put(STRING.LABEL_RANGE, "Map<String, Interval<String>>");
        formatter.put(STRING.LABEL_RANGE_COMMA, "Map<String, Interval<String>>");
        formatter.put(STRING.LABEL_RANGE_SEMICOLON, "Map<String, Interval<String>>");
        formatter.put(STRING.LABEL_ARRAY, "Map<String, String[]>");
        formatter.put(STRING.LABEL_ARRAY_SEMICOLON, "Map<String, String[]>");

        formatter.put(FILE.VALUE, "File");
        formatter.put(FILE.ARRAY, "File[]");
        formatter.put(FILE.SET, "Set<File>");
        formatter.put(FILE.MAP, "Map<String, File>");
    }

    public CommandParserWithOptionsBuilder(CommandParser parser) {
        this.parser = parser;
        this.packagePath = "edu.sysu.pmglab.commandParser";
        this.className = "MainParser";
    }

    public CommandParserWithOptionsBuilder(CommandParser parser, String classPath) {
        if (classPath == null || classPath.length() == 0 || !CLASS_NAME_RULE.matcher(classPath).matches()) {
            throw new CommandParserException("Syntax error: '" + classPath + "' is not a valid classpath name");
        }

        this.parser = parser;
        this.packagePath = classPath.substring(0, classPath.lastIndexOf("."));
        this.className = classPath.substring(classPath.lastIndexOf(".") + 1);
    }

    public CommandParserWithOptionsBuilder(CommandParser parser, String packagePath, String classPath) {
        if (packagePath == null || packagePath.length() == 0) {
            throw new CommandParserException("Syntax error: '" + packagePath + "' is not a valid packagePath name");
        }

        if (classPath == null || classPath.length() == 0) {
            throw new CommandParserException("Syntax error: '" + classPath + "' is not a valid classpath name");
        }

        if (!CLASS_NAME_RULE.matcher(packagePath + "." + classPath).matches()) {
            throw new CommandParserException("Syntax error: '" + packagePath + "." + classPath + "' is not a valid classpath name");
        }

        this.parser = parser;
        this.packagePath = packagePath;
        this.className = classPath;
    }

    public String toJavaScript() {
        StringBuilder builder = new StringBuilder(10240);
        importPackage(builder);
        generateClass(builder);

        return builder.toString();
    }

    /**
     * 导入模块
     */
    void importPackage(StringBuilder builder) {
        // 写入包地址
        builder.append("package " + this.packagePath + ";\n\n");

        // 导入基础包
        builder.append("import edu.sysu.pmglab.commandParser.CommandGroup;\n");
        builder.append("import edu.sysu.pmglab.commandParser.CommandOption;\n");
        builder.append("import edu.sysu.pmglab.commandParser.CommandOptions;\n");
        builder.append("import edu.sysu.pmglab.commandParser.CommandParser;\n");
        builder.append("import edu.sysu.pmglab.commandParser.usage.DefaultStyleUsage;\n");
        builder.append("import edu.sysu.pmglab.container.File;\n");
        builder.append("import edu.sysu.pmglab.container.Interval;\n");

        // 导入类型包
        HashSet<String> types = new LinkedHashSet<>();
        for (CommandItem item : this.parser) {
            // 扫描所有类型
            IType type = item.getConverter().getBaseValueType();
            if (type.equals(IType.NONE)) {
                types.add("IType");
            } else {
                types.add(type.getBaseValueType().toString());
            }
        }

        builder.append("import edu.sysu.pmglab.commandParser.types.*;\n");

        // 导入异常包
        builder.append("\n");
        builder.append("import java.io.IOException;\n");
        builder.append("import java.util.*;\n");
        builder.append("\n");

        // 导入静态变量包
        builder.append("import static edu.sysu.pmglab.commandParser.CommandRule.*;\n");
        builder.append("import static edu.sysu.pmglab.commandParser.CommandItem.*;");
    }

    void generateClass(StringBuilder builder) {
        builder.append("\n\n");
        builder.append("public class " + this.className + " {\n");
        builder.append("    /**\n");
        builder.append("     * build by: CommandParser-" + CommandParser.VERSION + "\n");
        builder.append("     * time: " + getTime() + "\n");
        builder.append("     */\n");
        builder.append("    private static final CommandParser PARSER = new CommandParser(false);\n");
        generateConstructor(builder);
        generateOtherMethods(builder);
        builder.append("\n");

        builder.append("    static {\n");
        builder.append("        /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *\n" +
                "         *                                          Initialize Command Parser\n" +
                "         * program name    : Program name shown in the User Guide.\n" +
                "         *                   default: <main class>\n" +
                "         * offset          : When the input parameter list has mandatory fields, the 'offset' can be used to skip these\n" +
                "         *                   fields.\n" +
                "         *                   e.g., when offset=2, \"bgzip compress --input ...\" will start parsing from '--input ...'\n" +
                "         *                   default: 0\n" +
                "         * debug           : In debug mode, the commandParser's work log will be printed to the terminal and the stack\n" +
                "         *                   ERROR will be output in detail to help developers troubleshoot errors. In addition,\n" +
                "         *                   command items marked with 'DEBUG' will also be parsed.\n" +
                "         *                   Note that in non-debug mode, command items marked with 'DEBUG' are treated as regular\n" +
                "         *                   parameter values, but not parameter keys. Therefore, the parsing results may be different\n" +
                "         *                   in different modes.\n" +
                "         *                   default: false\n" +
                "         * usingAt         : For parameters starting with @, the program will recognize the content after it as a file\n" +
                "         *                   (i.e., @<file>), and these parameters will be replaced by the text inside the file.\n" +
                "         *                   default: true\n" +
                "         * max matched num : Control the maximum number of the matched command items. The remaining parameters exceeding\n" +
                "         *                   this number will be regarded as the parameters of the last matched command item.\n" +
                "         *                   default: -1 (means no limitation)\n" +
                "         * usage style     : User Guide in Unix-style. The parameters of the 'DefaultStyleUsage' are used to assign the\n" +
                "         *                   display style of the User Guide. The IUsage interface can be inherited to implement\n" +
                "         *                   customized styles.\n" +
                "         * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */\n");
        builder.append("        PARSER.setProgramName(\"" + this.parser.getProgramName() + "\");\n");
        builder.append("        PARSER.offset(" + this.parser.getOffset() + ");\n");
        builder.append("        PARSER.debug(" + this.parser.isDebug() + ");\n");
        builder.append("        PARSER.usingAt(" + this.parser.isUsingAtSyntax() + ");\n");
        builder.append("        PARSER.setMaxMatchedNum(" + this.parser.getMaxMatchedNum() + ");\n");
        builder.append("        PARSER.setAutoHelp(" + this.parser.isAutoHelp() + ");\n");

        // 生成文档
        if (this.parser.getUsage() instanceof DefaultStyleUsage) {
            if (this.parser.getUsage().equals(DefaultStyleUsage.UNIX_TYPE_1)) {
                builder.append("        PARSER.setUsageStyle(DefaultStyleUsage.UNIX_TYPE_1);\n");
            } else if (this.parser.getUsage().equals(DefaultStyleUsage.UNIX_TYPE_2)) {
                builder.append("        PARSER.setUsageStyle(DefaultStyleUsage.UNIX_TYPE_2);\n");
            } else if (this.parser.getUsage().equals(DefaultStyleUsage.UNIX_TYPE_3)) {
                builder.append("        PARSER.setUsageStyle(DefaultStyleUsage.UNIX_TYPE_3);\n");
            } else if (this.parser.getUsage().equals(DefaultStyleUsage.UNIX_TYPE_4)) {
                builder.append("        PARSER.setUsageStyle(DefaultStyleUsage.UNIX_TYPE_4);\n");
            } else {
                DefaultStyleUsage usage = (DefaultStyleUsage) this.parser.getUsage();
                builder.append("        PARSER.setUsageStyle(new DefaultStyleUsage(\"" + usage.getBefore() + "\", \"" + usage.getAfter() + "\", \"" + usage.getSubTitle() + "\", " + usage.getIndent1() + ", " + usage.getIndent2() + ", " + usage.getMaxLength() + ", " + usage.isNewLineAfterCommandName() + ", \"" + usage.getRequestMark() + "\", \"" + usage.getDebugMark() + "\"));\n");
            }
        } else {
            builder.append("        PARSER.setUsageStyle(DefaultStyleUsage.UNIX_TYPE_1);\n");
        }

        addCommandGroup(builder);
        addCommandRule(builder);
        builder.append("    }\n");
        builder.append("}");
    }

    void addCommandGroup(StringBuilder builder) {
        if (this.parser.numOfCommandGroups() > 0) {
            builder.append("\n\n");

            // 注册项目
            builder.append("        /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *\n" +
                    "         *                                              Add Command Items\n" +
                    "         * CommandParser organizes multiple command items by \"groups\". Command items of the same group have the same\n" +
                    "         * purpose (e.g. input, output, functional, complementary) or other customized types.\n" +
                    "         * commandParser\n" +
                    "         *    -- commandGroup 1\n" +
                    "         *       -- commandItem 1\n" +
                    "         *       -- commandItem 2\n" +
                    "         *       -- ...\n" +
                    "         *    -- commandGroup 2\n" +
                    "         *       -- commandItem 1\n" +
                    "         *       -- commandItem 2\n" +
                    "         *       -- ...\n" +
                    "         *\n" +
                    "         * First, use the 'parser.addCommandGroup($GroupName)' statement to create a command group named $GroupName.\n" +
                    "         * Next, use the 'parser.register' statement to add command item(s) to the most recently registered command\n" +
                    "         * group. We can also use the returned value of the addCommandGroup to add the command item(s) to a specified\n" +
                    "         * command group precisely.\n" +
                    "         *\n" +
                    "         * group.register(IType type, String... commandNames)\n" +
                    "         * type         : Type of the parsed value of the current command item. CommandParser sets 10 basic types of\n" +
                    "         *                parameters, including NONE, BOOLEAN, BYTE, SHORT, INTEGER, LONG, FLOAT, DOUBLE, STRING, FILE.\n" +
                    "         *                On the basis of these basic types, commandParser has deduced other 16 new types.\n" +
                    "         * commandNames : The command name of the corresponding command item. The first name is set as the main name of\n" +
                    "         *                the command item, and the subsequent names are used as alias names.\n" +
                    "         *\n" +
                    "         * The returned value of group.register or parser.register is the command item itself, so users can use the\n" +
                    "         * chain call to set the property of the command. For example:\n" +
                    "         * group.register(FILE.VALUE, \"--build\", \"-b\")\n" +
                    "         *      .arity(1)\n" +
                    "         *      .addOptions(REQUEST)\n" +
                    "         *      .defaultTo(\"./example/assoc.hg19.vcf.gz\")\n" +
                    "         *      .validateWith(FILE.validateWith(true, true, true));\n" +
                    "         * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */\n");
            int groupIndex = 1;
            for (Iterator<CommandGroup> it = this.parser.groupIterator(); it.hasNext(); ) {
                CommandGroup group = it.next();
                String groupMark = "group" + groupIndex;
                builder.append("        CommandGroup " + groupMark + " = PARSER.addCommandGroup(\"" + group.getGroupName() + "\");\n");
                for (CommandItem commandItem : group) {
                    addCommandItem(commandItem, builder, groupMark);
                    builder.append("\n");
                }

                if (it.hasNext()) {
                    builder.append("\n\n");
                }
                groupIndex++;
            }
        }
    }

    void addCommandItem(CommandItem commandItem, StringBuilder builder, String groupMark) {
        // 设置参数类型
        IType converter = commandItem.getConverter();

        // 连接参数名
        if (converter == converter.getBaseValueType()) {
            String commandNameJoin = commandItem.linkCommandNamesBy(", ", "\"");

            if (converter.equals(IType.NONE)) {
                // None 类型
                builder.append("        " + groupMark + ".register(IType.NONE, " + commandNameJoin + ")");
            } else {
                // 基本数值类型
                builder.append("        " + groupMark + ".register(" + converter + ".VALUE, " + commandNameJoin + ")");
            }
        } else {
            String commandNameJoin = commandItem.linkCommandNamesBy(", ", "\"");
            String converterName = converter.toString();
            int index = converterName.indexOf("_");
            builder.append("        " + groupMark + ".register(" + converterName.substring(0, index) + "." + converterName.substring(index + 1) + ", " + commandNameJoin + ")");
        }

        // 设置参数长度
        if (converter.getDefaultArity() == -1) {
            builder.append("\n                .arity(" + commandItem.getArity() + ")");
        }

        // 设置参数权限
        Set<String> options = commandItem.getOptions();
        if (options.size() != 0) {
            builder.append("\n                .addOptions(" + StringArray.wrap(options.toArray(new String[0])).join(", ") + ")");
        }

        // 设置默认值
        if (commandItem.getDefaultValueOriginFormat() != null) {
            String commandDefaultValueJoin = ((StringArray) StringArray.wrap(commandItem.getDefaultValueOriginFormat().split(" ")).apply(value -> "\"" + value + "\"")).join(", ");
            builder.append("\n                .defaultTo(" + commandDefaultValueJoin + ")");
        }

        // 设置验证器
        if (commandItem.getValidator() != null) {
            IValidator validator = commandItem.getValidator();

            if (converter.getBaseValueType().equals(FILE.VALUE)) {
                 if ((boolean) validator.get("checkIsDirectory")) {
                    builder.append("\n                .validateWith(FILE.validateWith(" + validator.get("checkIsExists") + ", " + validator.get("checkIsFile") + ", " + validator.get("checkIsDirectory") + "))");
                } else if ((boolean) validator.get("checkIsFile")) {
                    builder.append("\n                .validateWith(FILE.validateWith(" + validator.get("checkIsExists") + ", " + validator.get("checkIsFile") + "))");
                } else if ((boolean) validator.get("checkIsExists")) {
                    builder.append("\n                .validateWith(FILE.validateWith(" + validator.get("checkIsExists") + "))");
                }
            } else if (converter.getBaseValueType().equals(STRING.VALUE)) {
                String validatorJoin = ((StringArray) StringArray.wrap((String[]) validator.get("elements")).apply(value -> "\"" + value + "\"")).join(", ");
                if ((Boolean) validator.get("ignoreCase") && (Boolean) validator.get("indexAccess")) {
                    builder.append("\n                .validateWith(STRING.validateWith(" + validatorJoin + "))");
                } else {
                    builder.append("\n                .validateWith(STRING.validateWith(" + validator.get("ignoreCase") + ", " + validator.get("indexAccess") + ", " + validatorJoin + "))");
                }
            } else {
                // 数值型
                IType baseValueType = converter.getBaseValueType();

                if (BYTE.VALUE.equals(baseValueType)) {
                    if (validator.toString().contains("~")) {
                        builder.append("\n                .validateWith(BYTE.validateWith((short) " + validator.get("min") + ", (short) " + validator.get("max") + "))");
                    } else {
                        builder.append("\n                .validateWith(BYTE.validateWith((short) " + validator.get("min") + "))");
                    }
                } else if (SHORT.VALUE.equals(baseValueType)) {
                    if (validator.toString().contains("~")) {
                        builder.append("\n                .validateWith(SHORT.validateWith((short) " + validator.get("min") + ", (short) " + validator.get("max") + "))");
                    } else {
                        builder.append("\n                .validateWith(SHORT.validateWith((short) " + validator.get("min") + "))");
                    }
                } else if (INTEGER.VALUE.equals(baseValueType)) {
                    if (validator.toString().contains("~")) {
                        builder.append("\n                .validateWith(INTEGER.validateWith(" + validator.get("min") + ", " + validator.get("max") + "))");
                    } else {
                        builder.append("\n                .validateWith(INTEGER.validateWith(" + validator.get("min") + "))");
                    }
                } else if (LONG.VALUE.equals(baseValueType)) {
                    if (validator.toString().contains("~")) {
                        builder.append("\n                .validateWith(LONG.validateWith(" + validator.get("min") + "L, " + validator.get("max") + "L))");
                    } else {
                        builder.append("\n                .validateWith(LONG.validateWith(" + validator.get("min") + "L))");
                    }
                } else if (FLOAT.VALUE.equals(baseValueType)) {
                    if (validator.toString().contains("~")) {
                        builder.append("\n                .validateWith(FLOAT.validateWith(" + validator.get("min") + "f, " + validator.get("max") + "f))");
                    } else {
                        builder.append("\n                .validateWith(FLOAT.validateWith(" + validator.get("min") + "f))");
                    }
                } else if (DOUBLE.VALUE.equals(baseValueType)) {
                    if (validator.toString().contains("~")) {
                        builder.append("\n                .validateWith(DOUBLE.validateWith(" + validator.get("min") + "d, " + validator.get("max") + "d))");
                    } else {
                        builder.append("\n                .validateWith(DOUBLE.validateWith(" + validator.get("min") + "d))");
                    }
                }
            }
        }

        // 设置数据格式
        if (!commandItem.getDefaultFormat().equals(commandItem.getFormat())) {
            builder.append("\n                .setFormat(\"" + commandItem.getFormat() + "\")");
        }

        if (commandItem.getDescription().length() != 0) {
            builder.append("\n                .setDescription(\"" + commandItem.getDescription() + "\")");
        }

        // 拼接完成
        builder.append(";");
    }

    void generateConstructor(StringBuilder builder) {
        builder.append("\n");
        builder.append("    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *\n" +
                "     *                                     Parse parameters and initialize variables\n" +
                "     * After calling parser.parse($args) to parse the parameters, the program will return an instance of CommandOptions.\n" +
                "     * CommandOptions has the following three API methods:\n" +
                "     * options.isPassedIn($commandName)          : Whether the command item is passed in (or captured) or not.\n" +
                "     * options.get($commandName)                 : Get the converted value of the passed parameter, please note that the\n" +
                "     *                                             type of the returned value is Object, which needs to be formatted by\n" +
                "     *                                             users.\n" +
                "     * options.getMatchedParameter($commandName) : Get the original string parameter of this command item.\n" +
                "     *\n" +
                "     * CommandOption is a wrapper class for parsing options, it has three properties (isPassedIn, value, matchedParameter),\n" +
                "     * which corresponding to the results of the above three method calls. CommandOption automatically generates variable\n" +
                "     * names with the name of the main command item, and uses the correct format type as a paradigm, and thus no additional\n" +
                "     * format conversion is required by users.\n" +
                "     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */\n");
        builder.append("    private final CommandOptions options;\n");
        for (CommandItem item : this.parser) {
            IType type = item.getConverter();
            builder.append("    public final CommandOption<" + formatter.get(type) + "> " + wrapCommandName(item.getCommandName()) + ";\n");
        }

        builder.append("\n    " + this.className + "(String... args) {\n" +
                "        this.options = PARSER.parse(args);\n");

        for (CommandItem item : this.parser) {
            String commandName = wrapCommandName(item.getCommandName());
            builder.append("        this." + commandName + " = new CommandOption<>(\"" + item.getCommandName() + "\", this.options);\n");
        }

        builder.append("    }\n");

        builder.append("\n    public static " + this.className + " parse(String... args) {\n" +
                "        return new " + this.className + "(args);\n" +
                "    }\n" +
                "\n" +
                "    public static " + this.className + " parse(File argsFile) throws IOException {\n" +
                "        return new " + this.className + "(CommandParser.readFromFile(argsFile));\n" +
                "    }\n");
    }

    String wrapCommandName(String commandName) {
        // 变量命名规则: a-zA-Z0-9+_\\- 先将其他字符转 _, 再切除多余的_
        commandName = commandName.replace("+", "_")
                .replace("-", "_")
                .replaceAll("_+", "_");

        if (commandName.startsWith("_")) {
            commandName = commandName.substring(1);
        }

        StringArray splitNames = (StringArray) StringArray.wrap(commandName.toLowerCase().split("_")).filter(name -> name.length() > 0);
        StringBuilder name = new StringBuilder();
        name.append(splitNames.get(0));
        for (int i = 1; i < splitNames.size(); i++) {
            name.append(splitNames.get(i).substring(0, 1).toUpperCase() + splitNames.get(i).substring(1));
        }

        return name.toString();
    }

    void generateOtherMethods(StringBuilder builder) {
        builder.append("\n    /**\n" +
                "     * Get CommandParser\n" +
                "     */\n" +
                "    public static CommandParser getParser() {\n" +
                "        return PARSER;\n" +
                "    }\n" +
                "\n" +
                "    /**\n" +
                "     * Get the usage of CommandParser\n" +
                "     */\n" +
                "    public static String usage() {\n" +
                "        return PARSER.toString();\n" +
                "    }\n" +
                "\n" +
                "    /**\n" +
                "     * Get CommandOptions\n" +
                "     */\n" +
                "    public CommandOptions getOptions() {\n" +
                "        return this.options;\n" +
                "    }\n");
    }

    void addCommandRule(StringBuilder builder) {
        if (this.parser.numOfCommandRules() > 0) {
            builder.append("\n\n");
            builder.append("        /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *\n" +
                    "         *                                               Add Command Rules\n" +
                    "         * parser.addRule(String ruleType, int conditionalValue, String... commands)\n" +
                    "         * Add an inter-command item rule with a quantity constraint.\n" +
                    "         * ruleType         : AT_MOST, AT_LEAST, EQUAL or MUTUAL_EXCLUSION\n" +
                    "         * conditionalValue : Number of constraints\n" +
                    "         * commands         : The command name used to apply this rule can be either the main command name or an\n" +
                    "         *                    alias name. The number of command names added must be greater than 1.\n" +
                    "         *\n" +
                    "         * parser.addRule(String ruleType, String... commands)\n" +
                    "         * Add an inter-command item rule with a dependency constraint.\n" +
                    "         * ruleType : SYMBIOSIS, PRECONDITION\n" +
                    "         * commands : The command name used to apply this rule can be either the main command name or an\n" +
                    "         *            alias name. The number of command names added must be greater than 1.\n" +
                    "         *\n" +
                    "         * Assume that the command item p1,p2,...pn are constrained, si = 1 means the parameter pi is passed in, k means\n" +
                    "         * conditionalValue. The examples of the above rules are as following:\n" +
                    "         * AT_MOST          : s1 + s2 + ... sn <= k\n" +
                    "         *                    {s1, s2, ..., sn} can be specified with a maximum of k items.\n" +
                    "         * AT_LEAST         : s1 + s2 + ... sn >= k\n" +
                    "         *                    {s1, s2, ..., sn} should be specified with at least k items.\n" +
                    "         * EQUAL            : s1 + s2 + ... sn == k\n" +
                    "         *                    {s1, s2, ..., sn} should be specified with k items.\n" +
                    "         * MUTUAL_EXCLUSION : k * u >= s1 + s2 + ... + sk >= u\n" +
                    "         *                    (n - k) * v >= s(k+1) + s(k+2) + ... + sn >= v\n" +
                    "         *                    1 - u >= v and u, v in {0, 1}\n" +
                    "         *                    {s1, s2, ..., sk} and {s(k+1), s(k+2), ..., sn} are not allowed to be used together.\n" +
                    "         * SYMBIOSIS        : s1 == s2 == ... == sn\n" +
                    "         *                    {s1, s2, ..., sn} should be specified concurrently or not at all.\n" +
                    "         * PRECONDITION     : s1 >= s2 >= ... >= sn\n" +
                    "         *                    when the i-th command item is specified, all the command items before it (i.e., index < i)\n" +
                    "         *                    should be specified concurrently.\n" +
                    "         * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */");

            for (Iterator<CommandRule> it = this.parser.ruleIterator(); it.hasNext(); ) {
                CommandRule rule = it.next();

                String commandRuleJoin = ((StringArray) rule.getCommands().apply(value -> "\"" + value + "\"")).join(", ");
                if (rule.isNumberedRule()) {
                    builder.append("\n        PARSER.addRule(" + rule.getRuleType() + ", " + rule.getNumber() + ", " + commandRuleJoin + ");");
                } else {
                    builder.append("\n        PARSER.addRule(" + rule.getRuleType() + ", " + commandRuleJoin + ");");
                }
            }

            builder.append("\n");
        }
    }

    String getTime() {
        SimpleDateFormat date = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
        return date.format(new Date());
    }
}
