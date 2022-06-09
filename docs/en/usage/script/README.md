# Initialize Parser {#初始化解析器}

CommandParser has 4 constructions, parameter `boolean init` represents whether to create initial `help` constructor; parameter `String programName`  represents the programmar name of the parser:

- CommandParser parser = new CommandParser()
- CommandParser parser = new CommandParser(boolean init)
- CommandParser parser = new CommandParser(String programName)
- CommandParser parser = new CommandParser(boolean init, String programName)

```java
CommandParser parser = new CommandParser(false);
```

# Set Global Parameters {#设置解析器的全局属性}

CommandParser has 7 globe properties:

- parser.setProgramName(String programName)

  Set program name (default value: `<main class>`).

- parser.offset(int length)

  Set offset value (default value: `0`). Skip the first `offset` parameters of the input parameters.

  ```bash
  # when offset = 3, the following commmands will skip the first three parameter and parse "--level 5 -t 4 -o ~/test.gz"
  bgzip compress <file> --level 5 -t 4 -o ~/test.gz
  ```

- parser.debug(boolean enable)

  Whether is debug mode (default value: `false`).

- parser.usingAt(boolean enable)

  Whether identify `@` as symbol of getting address (file content the address corresponding is as input parameter) (default value: `true`).

- parser.setMaxMatchedNum(int length)

  Set the maximum number of matched command items (default value: `-1`). When the maximum number of input and matched command items is reached, the subsequent parameters are no longer parsed, but the parameter value of the last matched command item.

  ```bash
  # when maxMatchedItems = 1, the following commmands only matched "bgzip" and following parameters, "compress <file> decompress <file>",  will be the value of "bgzip"
  bgzip compress <file> decompress <file>
  ```
  
- parser.setAutoHelp(boolean enable)

  When no parameters are input, whether to add `--help` parameter automatically (default value:  `false`).
  
- parser.setUsageStyle(IUsage usageStyle)

  Set the format of the document (default value: `DefaultStyleUsage.UNIX_TYPE_1`).
  
  ```java
  // DefaultStyleUsage construct:
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

# Add Command Group {#添加参数组}

CommandParser add command group by two ways and return the added group:

- CommandGroup group = parser.addCommandGroup(String groupName)

  Add the command group named `groupName`. If the same command group has existed in the parser, then return the command group, otherwise return new parameter group.

- CommandGroup group = parser.addCommandGroup(CommandGroup group)

  If a command group with the same name already exists in the parser, register all command items of the command group; otherwise, add the command group to the parser.

```java
CommandGroup group001 = parser.addCommandGroup("Mode");
CommandGroup group002 = parser.addCommandGroup("Options");
```

# Add Command Item {#添加参数项}

Command items can be injected into the parser through command groups:

- CommandItem item = group.register(IType type, String... commandNames)

  The main method. Set command type and command names, and then register command item.

- CommandItem item = group.register(Class<?> tClass, String... commandNames)

  Set command type and command name, and then register command item. The command type (`tClass`) of this method can only identify VALUE (e.g. Integer.class) and ARRAY (e.g. int[].class).

- CommandItem item = group.register(CommandItem commandItem)

  Add command item to the parser.

When registered command item directly through the parser, the command item is registered  into the last added parameter group:

- CommandItem item = parser.register(IType type, String... commandNames)

  Set command type and command name, and then register command item.

- CommandItem item = parser.register(IType type, String... commandNames)

  Set command type and command name, and then register command item. The command type (`tClass`) of this method can only identify VALUE (e.g. Integer.class) and ARRAY (e.g. int[].class).

- CommandItem item = parser.register(CommandItem commandItem)

  Add command item to the parser.

> [!NOTE|label:Command Type`MainType.DerivedType`]
>
> Main types includes IType.NONE (only verify that it is passed in), BOOLEAN, BYTE, SHORT, INTEGER, LONG, FLOAT, DOUBLE, STRING, FILE. Derived types include [parameter type input format](../ui/commanditems.md#参数格式) lists the 16 types.

# Set Properties of Command Item {#设置参数项属性}

Propertie settings for command item all return the command item itself, which can be set through chain calls. For the meaning of the propertie, see: [Edit Command Item](../ui/commanditems.md#编辑参数项).

- CommandItem item = item.addOptions(String... options)

  Add Options: HIDDEN, HELP, REQUEST, DEBUG.

- CommandItem item = item.arity(int length)

  Set the arity. -1 indicates an indefinite length (which can be 0 parameters). All parameters up to the next matched command item are regarded as the value of this command item.

- CommandItem item = item.defaultTo(String... defaultValue)

  Set the default value (using string-array as input, converted to the corresponding value according to the converter).

- CommandItem item = item.validateWith(IValidator validator)

  Set validator for command item. Different command types apply different validator methods, see: [Validator](../ui/commanditems.md#参数验证器}).

| Command Type                              | Validator Support Type                                       |
| :---------------------------------------- | :----------------------------------------------------------- |
| None, BOOLEAN                             | Validators are not supported.                                |
| BYTE, SHORT, INTEGER, LONG, FLOAT, DOUBLE | type.validateWith(minValue, maxValue)<br />type.validateWith(minValue) |
| STRING                                    | STRING.validateWith(String... elements)<br />STRING.validateWith(boolean ignoreCase, final boolean indexAccess,  String... elements)<br /><br />Multiple qualifying values are allowed.<br />ignoreCase: whether to ignores case;<br />indexAccess: allow the use of indexes instead of specific values (0 represents the first qualified value...). |
| FILE                                      | FILE.validateWith(boolean checkIsExists, boolean checkIsFile, boolean checkIsDirectory, boolean checkInnerResource)<br /><br />checkIsExists: The file path must exist. <br />checkIsFile: The file path cannot point to folder; <br />checkIsDirectory: The file path must point to a folder; <br />checkInnerResource: Preferentially identifies the current runtime resources (allowing access to internal JAR files). |

- CommandItem item = item.setFormat(String format)

  Set format description of command item.

- CommandItem item = item.setDescription(java.lang.String description)

  Set description of command item.

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

# Add Command Rule {#设置参数规则}

CommandParser adds a command rule in any of the following ways. For the meaning of the command rule type, see: [Command Rule Type](../ui/commandrules.md#参数规则类型).

- parser.addRule(String ruleType, String... commands)

  Add command rule. Available values: SYMBIOSIS or PRECONDITION.

- parser.addRule(String ruleType, int conditionalValue, String... commands)

  Add command rule. Available values: AT_MOST, AT_LEAST, EQUAL, or MUTUAL_EXCLUSION.

- parser.addRule(CommandRule rule)

  Add command rule.

```java
parser.addRule(EQUAL, 1, "--md5-decompress", "--md5", "--compress", "--decompress", "--concat");
parser.addRule(MUTUAL_EXCLUSION, 1, "--md5", "--output", "--level", "--range", "--threads");
parser.addRule(MUTUAL_EXCLUSION, 1, "--md5-decompress", "--output", "--range", "--level", "--threads");
parser.addRule(MUTUAL_EXCLUSION, 1, "--concat", "--range", "--level", "--threads");
```

# Formatted Parser {#格式化解析器}

After formatting the parser according to the standard template, the parser can be dragged directly into the graphical design interface for editing and management. Graphical design interface readable requirements:

- The class file is complete and can compile independently;
- preserve `package ${path};`;
- Class access modifier is `public`;
- Contain static methods `public static CommandParser getParser()`.

CommandParserDesigner dynamically compiles the parser source file to a class file and obtains the parser object via `.getParser()`. Finally, the designer graphical interface reproduces the parser based on the member information of the parser object.

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

