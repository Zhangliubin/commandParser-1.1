# 初始化解析器 {#初始化解析器}

CommandParser 有 4 种构造器，参数 `boolean init` 代表是否创建初始的 `help` 指令 (默认为 `true`)；参数 `String programName` 代表解析器的程序名 (默认为 `<main class>`):

- CommandParser parser = new CommandParser()
- CommandParser parser = new CommandParser(boolean init)
- CommandParser parser = new CommandParser(String programName)
- CommandParser parser = new CommandParser(boolean init, String programName)

```java
CommandParser parser = new CommandParser(false);
```

# 设置解析器的全局属性 {#设置解析器的全局属性}

CommandParser 有 7 个全局属性，分别如下:

- parser.setProgramName(String programName)

  设置程序名 (默认值: `<main class>`)。

- parser.offset(int length)

  设置偏移量 (默认值: `0`)。

  ```bash
  # offset = 3 时，传入的下列参数将跳过前 3 个参数，解析 "--level 5 -t 4 -o ~/test.gz"
  bgzip compress <file> --level 5 -t 4 -o ~/test.gz
  ```

- parser.debug(boolean enable)

  是否为调试模式 (默认值: `false`)。

- parser.usingAt(boolean enable)

  是否将 `@` 识别为取地址符 (地址对应的文件内容作为参数传入) (默认值: `true`)。

- parser.setMaxMatchedNum(int length)

  设置最大匹配参数项个数 (默认值: `-1`)。传入的参数项达到最大个数时，后续的参数不再解析，而是作为最后一个匹配的参数项的值。

  ```bash
  # maxMatchedItems = 1 时，下列参数只匹配 "bgzip"，剩下的参数 "compress <file> decompress <file>" 则作为 "bgzip" 参数项的值
   bgzip compress <file> decompress <file>
  ```
  
- parser.setAutoHelp(boolean enable)

  当没有指令被传入时，是否自动添加 help 指令 (默认值: `false`)。
  
- parser.setUsageStyle(IUsage usageStyle)

  设置文档格式 (默认值: `DefaultStyleUsage.UNIX_TYPE_1`)。
  
  ```java
  // DefaultStyleUsage 构造器:
  public DefaultStyleUsage(String before, String after, String subTitle, int indent1, int indent2, int maxLength, boolean newLineAfterCommandName, String requestMark, String debugMark)
  ```
  
  

```java
parser.setProgramName("<mode>");
parser.offset(0);
parser.debug(false);
parser.usingAt(true);
parser.setMaxMatchedNum(-1);
parser.setAutoHelp(true);
parser.setUsageStyle(DefaultStyleUsage.UNIX_TYPE_1);
```

# 添加参数组 {#添加参数组}

CommandParser 通过以下两种方式添加参数组，并返回添加的参数组 (解析器中的同名参数组或新参数组):

- CommandGroup group = parser.addCommandGroup(String groupName)

  添加组名为 `groupName` 的参数组。若该解析器中已存在同名参数组，则返回同名参数组，否则返回新参数组。

- CommandGroup group = parser.addCommandGroup(CommandGroup group)

  若该解析器中已存在同名参数组，则将该参数组的所有参数注册到同名参数组中，否则将该参数组添加到解析器中。

```java
CommandGroup group001 = parser.addCommandGroup("Mode");
CommandGroup group002 = parser.addCommandGroup("Options");
```

# 添加参数项 {#添加参数项}

参数项可以通过参数组注入解析器:

- CommandItem item = group.register(IType type, String... commandNames)

  主要方法。设置参数类型和参数名，注册参数项。

- CommandItem item = group.register(Class<?> tClass, String... commandNames)

  设置参数类型和参数名，注册参数项。该方法的参数类型只能识别 VALUE 类型 (如: Integer.class) 和 ARRAY 类型 (如: int[].class)。

- CommandItem item = group.register(CommandItem commandItem)

  若该解析器中已存在同名参数组，则抛出 `CommandParserException` 异常，否则将该参数项添加到解析器中。

直接通过解析器进行注册时，则向最后一次添加的参数组注册该参数项:

- CommandItem item = parser.register(IType type, String... commandNames)

  设置参数类型和参数名，注册参数项。

- CommandItem item = parser.register(IType type, String... commandNames)

  设置参数类型和参数名，注册参数项。该方法的参数类型只能识别 VALUE 类型 (如: Integer.class) 和 ARRAY 类型 (如: int[].class)。

- CommandItem item = parser.register(CommandItem commandItem)

  若该解析器中已存在同名参数组，则抛出 `CommandParserException` 异常，否则将该参数项添加到解析器中。

> [!NOTE|label:参数类型`主参数类型.派生参数类型`]
>
> 主参数类型包括 IType.NONE (无类型，仅验证是否被传入), BOOLEAN, BYTE, SHORT, INTEGER, LONG, FLOAT, DOUBLE, STRING, FILE。派生参数类型包括 [参数类型输入格式](../ui/commanditems.md#参数格式) 列出的 16 种类型。

# 设置参数项属性 {#设置参数项属性}

参数项的属性设置都是返回参数项本身，可以通过链式调用进行设置。属性的含义见[编辑参数项](../ui/commanditems.md#编辑参数项):

- CommandItem item = item.addOptions(String... options)

  添加参数 HIDDEN, HELP, REQUEST, DEBUG。

- CommandItem item = item.arity(int length)

  设置参数长度 只有不定长参数可以主动调用参数长度设置方法。

- CommandItem item = item.defaultTo(String... defaultValue)

  设置默认值 (使用字符串作为输入, 并按照格式转换器转为对应的值)。

- CommandItem item = item.validateWith(IValidator validator)

  设置参数验证器，不同的参数类型适用不同的验证器方法。

| Command Type                                            | Validator 支持的类型                                         |
| :------------------------------------------------------ | :----------------------------------------------------------- |
| None, BOOLEAN                                           | 不支持使用验证器。                                           |
| BYTE, SHORT, INTEGER, LONG, FLOAT, DOUBLE<br />数值类型 | 类型.validateWith(最小值, 最大值)<br />类型.validateWith(最小值) |
| STRING<br />字符串类型                                  | STRING.validateWith(String... elements)<br />STRING.validateWith(boolean ignoreCase, final boolean indexAccess,  String... elements)<br /><br />允许设置多个限定值，Ignore Case 忽略大小写，Index Access 允许使用索引访问 (0 代表第一个限定值…)。 |
| FILE<br />文件类型                                      | FILE.validateWith(boolean checkIsExists, boolean checkIsFile, boolean checkIsDirectory, boolean checkInnerResource)<br /><br />checkIsExists 文件路径必须存在;<br />checkIsFile 文件路径不能指向文件夹;<br />checkIsDirectory 文件路径必须指向文件夹;<br />checkInnerResource 优先识别当前运行环境资源 (允许访问 jar 包内部文件)。 |

- CommandItem item = item.setFormat(String format)

  设置参数格式描述 (参数格式默认值见[参数类型输入格式](../ui/commanditems.md#参数格式)，默认为 `主参数名 参数格式`)

- CommandItem item = item.setDescription(java.lang.String description)

  设置描述文档

```java
group001.register(FILE.VALUE, "--compress", "-c")
        .validateWith(FILE.validateWith(true, true))
        .setDescription("Compression using parallel-bgzip (supported by CLM algorithm).");
group001.register(FILE.VALUE, "--decompress", "-d")
        .validateWith(FILE.validateWith(true, true))
        .setDescription("Decompress or recompress partial (or full) bgzip file.");
group001.register(FILE.ARRAY, "--concat")
        .arity(-1)
        .validateWith(FILE.validateWith(true, true))
        .setDescription("Concatenate multiple files.");
group001.register(FILE.VALUE, "--md5")
        .validateWith(FILE.validateWith(true, true))
        .setDescription("Calculate a message-digest fingerprint (checksum) for input file.");
group001.register(FILE.VALUE, "--md5-decompress", "--md5-d")
        .validateWith(FILE.validateWith(true, true))
        .setDescription("Calculate a message-digest fingerprint (checksum) for decompressed file.");
        
group002.register(IType.NONE, "--help", "-help", "-h")
        .addOptions(HELP, HIDDEN);
group002.register(FILE.VALUE, "--output", "-o")
        .setDescription("Set the output file.");
group002.register(LONG.RANGE, "--range", "-r")
        .validateWith(LONG.validateWith(0L))
        .setDescription("Set the range of the file pointer.");
group002.register(INTEGER.VALUE, "--level", "-l")
        .defaultTo("5")
        .validateWith(INTEGER.validateWith(0, 9))
        .setDescription("Compression level to use for bgzip compression.");
group002.register(INTEGER.VALUE, "--threads", "-t")
        .defaultTo("4")
        .validateWith(INTEGER.validateWith(1))
        .setDescription("Set the number of threads for parallel-bgzip compression.");
```

# 设置参数规则 {#设置参数规则}

CommandParser 通过以下三种方式添加参数规则，规则的含义见[参数规则类型](../ui/commandrules.md#参数规则类型):

- parser.addRule(String ruleType, String... commands)

  添加参数规则，可选类型为 SYMBIOSIS 或 PRECONDITION。

- parser.addRule(String ruleType, int conditionalValue, String... commands)

  添加参数规则，可选类型为 AT_MOST、AT_LEAST、EQUAL、MUTUAL_EXCLUSION。

- parser.addRule(CommandRule rule)

  添加参数规则。

```java
parser.addRule(EQUAL, 1, "--md5-decompress", "--md5", "--compress", "--decompress", "--concat");
parser.addRule(MUTUAL_EXCLUSION, 1, "--md5", "--output", "--level", "--range", "--threads");
parser.addRule(MUTUAL_EXCLUSION, 1, "--md5-decompress", "--output", "--range", "--level", "--threads");
parser.addRule(MUTUAL_EXCLUSION, 1, "--concat", "--range", "--level", "--threads");
```

# 格式化解析器 {#格式化解析器}

按照标准模版格式化解析器后，该解析器可以直接拖入图形设计界面进行编辑、管理。图形设计界面可读要求: 

- 类文件完整，可独立编译；
- 保留 `package ${路径};` 字段；
- 类访问修饰符为 `public`；
- 具有静态方法 `public static CommandParser getParser()`。

CommandParserDesigner 将解析器源代码文件动态编译为 class 文件, 并通过 .getParser() 获取解析器对象。最后, 设计器图形界面根据解析器对象的成员信息复现解析器。

```java
package edu.sysu.pmglab.bgztools;

import edu.sysu.pmglab.commandParser.CommandGroup;
import edu.sysu.pmglab.commandParser.CommandOption;
import edu.sysu.pmglab.commandParser.CommandOptions;
import edu.sysu.pmglab.commandParser.CommandParser;
import edu.sysu.pmglab.commandParser.types.FILE;
import edu.sysu.pmglab.commandParser.types.INTEGER;
import edu.sysu.pmglab.commandParser.types.IType;
import edu.sysu.pmglab.commandParser.types.LONG;
import edu.sysu.pmglab.commandParser.usage.DefaultStyleUsage;
import edu.sysu.pmglab.container.File;

import java.io.IOException;

import static edu.sysu.pmglab.commandParser.CommandItem.HELP;
import static edu.sysu.pmglab.commandParser.CommandItem.HIDDEN;
import static edu.sysu.pmglab.commandParser.CommandRule.EQUAL;
import static edu.sysu.pmglab.commandParser.CommandRule.MUTUAL_EXCLUSION;

public class BGZToolkitParser {
    /**
     * build by: CommandParser-1.1
     * time: 2022-05-31 18:06:01
     */
    private static final CommandParser PARSER = new CommandParser(false);

    private final CommandOptions options;
    public final CommandOption<File> compress;
    public final CommandOption<File> decompress;
    public final CommandOption<File[]> concat;
    public final CommandOption<File> md5;
    public final CommandOption<File> md5Decompress;
    public final CommandOption<?> help;
    public final CommandOption<File> output;
    public final CommandOption<long[]> range;
    public final CommandOption<Integer> level;
    public final CommandOption<Integer> threads;

    BGZToolkitParser(String... args) {
        this.options = PARSER.parse(args);
        this.compress = new CommandOption<>("--compress", this.options);
        this.decompress = new CommandOption<>("--decompress", this.options);
        this.concat = new CommandOption<>("--concat", this.options);
        this.md5 = new CommandOption<>("--md5", this.options);
        this.md5Decompress = new CommandOption<>("--md5-decompress", this.options);
        this.help = new CommandOption<>("--help", this.options);
        this.output = new CommandOption<>("--output", this.options);
        this.range = new CommandOption<>("--range", this.options);
        this.level = new CommandOption<>("--level", this.options);
        this.threads = new CommandOption<>("--threads", this.options);
    }

    public static BGZToolkitParser parse(String... args) {
        return new BGZToolkitParser(args);
    }

    public static BGZToolkitParser parse(File argsFile) throws IOException {
        return new BGZToolkitParser(CommandParser.readFromFile(argsFile));
    }

    /**
     * Get CommandParser
     */
    public static CommandParser getParser() {
        return PARSER;
    }

    /**
     * Get the usage of CommandParser
     */
    public static String usage() {
        return PARSER.toString();
    }

    /**
     * Get CommandOptions
     */
    public CommandOptions getOptions() {
        return this.options;
    }

    static {
        PARSER.setProgramName("<mode>");
        PARSER.offset(0);
        PARSER.debug(false);
        PARSER.usingAt(true);
        PARSER.setMaxMatchedNum(-1);
        PARSER.setAutoHelp(true);
        PARSER.setUsageStyle(DefaultStyleUsage.UNIX_TYPE_1);

        CommandGroup group001 = PARSER.addCommandGroup("Mode");
        group001.register(FILE.VALUE, "--compress", "-c")
                .validateWith(FILE.validateWith(true, true))
                .setDescription("Compression using parallel-bgzip (supported by CLM algorithm).");
        group001.register(FILE.VALUE, "--decompress", "-d")
                .validateWith(FILE.validateWith(true, true))
                .setDescription("Decompress or recompress partial (or full) bgzip file.");
        group001.register(FILE.ARRAY, "--concat")
                .arity(-1)
                .validateWith(FILE.validateWith(true, true))
                .setDescription("Concatenate multiple files.");
        group001.register(FILE.VALUE, "--md5")
                .validateWith(FILE.validateWith(true, true))
                .setDescription("Calculate a message-digest fingerprint (checksum) for input file.");
        group001.register(FILE.VALUE, "--md5-decompress", "--md5-d")
                .validateWith(FILE.validateWith(true, true))
                .setDescription("Calculate a message-digest fingerprint (checksum) for decompressed file.");

        CommandGroup group002 = PARSER.addCommandGroup("Options");
        group002.register(IType.NONE, "--help", "-help", "-h")
                .addOptions(HELP, HIDDEN);
        group002.register(FILE.VALUE, "--output", "-o")
                .setDescription("Set the output file.");
        group002.register(LONG.RANGE, "--range", "-r")
                .validateWith(LONG.validateWith(0L))
                .setDescription("Set the range of the file pointer.");
        group002.register(INTEGER.VALUE, "--level", "-l")
                .defaultTo("5")
                .validateWith(INTEGER.validateWith(0, 9))
                .setDescription("Compression level to use for bgzip compression.");
        group002.register(INTEGER.VALUE, "--threads", "-t")
                .defaultTo("4")
                .validateWith(INTEGER.validateWith(1))
                .setDescription("Set the number of threads for parallel-bgzip compression.");

        PARSER.addRule(EQUAL, 1, "--md5-decompress", "--md5", "--compress", "--decompress", "--concat");
        PARSER.addRule(MUTUAL_EXCLUSION, 1, "--md5", "--output", "--level", "--range", "--threads");
        PARSER.addRule(MUTUAL_EXCLUSION, 1, "--md5-decompress", "--output", "--range", "--level", "--threads");
        PARSER.addRule(MUTUAL_EXCLUSION, 1, "--concat", "--range", "--level", "--threads");
    }
}
```

