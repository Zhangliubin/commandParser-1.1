package edu.sysu.pmglab.commandParser;

import edu.sysu.pmglab.commandParser.exception.CommandParserException;
import edu.sysu.pmglab.container.array.StringArray;

import java.util.Iterator;
import java.util.Objects;
import java.util.Set;

/**
 * @author suranyi
 */

public class CommandRule implements Iterable<String> {
    private final StringArray commands;
    private final String type;
    private final int number;

    /**
     * 至多 k 个 (s1 + s2 + ... sn &lt;= k)
     */
    public static final String AT_MOST = "AT_MOST";
    /**
     * 至少 k 个 (s1 + s2 + ... sn &gt;= k)
     */
    public static final String AT_LEAST = "AT_LEAST";
    /**
     * 需要 k 个 (s1 + s2 + ... sn == k)
     */
    public static final String EQUAL = "EQUAL";
    /**
     * 互斥 (两组参数集无法同时出现, 但可以同时不出现)
     * 当 "互斥" 与 "至少 k 个" 组合时, 可以出现两组参数出现其中一组的情形
     * k * u &gt;= s1 + s2 + ... + sk &gt;= u
     * (n - k) * v &gt;= s(k+1) + s(k+2) + ... + sn &gt;= v
     * 1 - u &gt;= v
     */
    public static final String MUTUAL_EXCLUSION = "MUTUAL_EXCLUSION";
    /**
     * 依赖链: s1 &gt;= s2 &gt;= ... &gt;= sn
     */
    public static final String PRECONDITION = "PRECONDITION";
    /**
     * 同时出现或同时不出现 (s1 == s2 == ... == sn)
     */
    public static final String SYMBIOSIS = "SYMBIOSIS";

    /**
     * 可取值验证器
     */
    private static final Set<String> NUMBER_VALIDATOR = StringArray.wrap(new String[]{AT_MOST, AT_LEAST, EQUAL, MUTUAL_EXCLUSION}).toSet();
    private static final Set<String> DEPENDENT_VALIDATOR = StringArray.wrap(new String[]{PRECONDITION, SYMBIOSIS}).toSet();

    /**
     * 创建依赖性指令规则
     *
     * @param commands 指令规则
     * @param type     指令规则类型
     */
    public CommandRule(String type, String... commands) {
        if (commands.length <= 1) {
            throw new CommandParserException("syntax error: rule type takes two or more command items together");
        }

        if (DEPENDENT_VALIDATOR.contains(type)) {
            this.type = type;
            this.number = -1;
        } else if (NUMBER_VALIDATOR.contains(type)) {
            this.type = type;
            this.number = 1;
        } else {
            throw new CommandParserException("syntax error: rule type only support one of " + DEPENDENT_VALIDATOR);
        }
        this.commands = new StringArray(commands);
    }

    /**
     * 创建依赖性指令规则
     *
     * @param commands 指令规则
     * @param type     指令规则类型
     */
    public CommandRule(String type, StringArray commands) {
        if (commands.size() <= 1) {
            throw new CommandParserException("syntax error: rule type takes two or more command items together");
        }

        if (DEPENDENT_VALIDATOR.contains(type)) {
            this.type = type;
            this.number = -1;
        } else if (NUMBER_VALIDATOR.contains(type)) {
            this.type = type;
            this.number = 1;
        } else {
            throw new CommandParserException("syntax error: rule type only support one of " + DEPENDENT_VALIDATOR);
        }
        this.commands = commands.clone();
    }

    /**
     * 创建数量指令规则
     *
     * @param type             指令规则类型
     * @param conditionalValue 条件数
     * @param commands         指令规则
     */
    public CommandRule(String type, int conditionalValue, String... commands) {
        if (commands.length <= 1) {
            throw new CommandParserException("syntax error: rule type takes two or more command items together");
        }

        if (NUMBER_VALIDATOR.contains(type)) {
            if (conditionalValue >= 0 && conditionalValue <= commands.length) {
                this.type = type;
                this.number = conditionalValue;
            } else {
                throw new CommandParserException("syntax error: the conditional value of the rule must be non-negative and cannot exceed the number of command items being constrained");
            }
        } else {
            throw new CommandParserException("syntax error: rule type only support one of " + NUMBER_VALIDATOR);
        }
        this.commands = new StringArray(commands);
    }

    /**
     * 创建数量指令规则
     *
     * @param type             指令规则类型
     * @param conditionalValue 条件数
     * @param commands         指令规则
     */
    public CommandRule(String type, int conditionalValue, StringArray commands) {
        if (commands.size() <= 1) {
            throw new CommandParserException("syntax error: rule type takes two or more command items together");
        }

        if (NUMBER_VALIDATOR.contains(type)) {
            if (conditionalValue >= 0 && conditionalValue <= commands.size()) {
                this.type = type;
                this.number = conditionalValue;
            } else {
                throw new CommandParserException("syntax error: the conditional value of the rule must be non-negative and cannot exceed the number of command items being constrained");
            }
        } else {
            throw new CommandParserException("syntax error: rule type only support one of " + NUMBER_VALIDATOR);
        }
        this.commands = commands.clone();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CommandRule)) {
            return false;
        }
        CommandRule that = (CommandRule) o;
        return this.commands.equals(that.commands) && this.type.equals(that.getRuleType());
    }

    /**
     * 获取参数名 (内部方法)
     *
     * @return 参数名
     */
    public StringArray getCommands() {
        return this.commands.clone();
    }

    /**
     * 获取参数规则
     *
     * @return 参数规则
     */
    public String getRuleType() {
        return this.type;
    }

    /**
     * 数量规则
     *
     * @return 是否为数量规则
     */
    public boolean isNumberedRule() {
        return this.number >= 0;
    }

    /**
     * 获取数量规则值
     *
     * @return 数量规则的条件数
     */
    public int getNumber() {
        return this.number;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.commands, this.type);
    }

    /**
     * 验证参数是否符合规则
     *
     * @param options 参数集
     * @return 是否符合该参数规则
     */
    public boolean check(CommandOptions options) {
        if (this.number >= 0) {
            // 数量验证器
            int nums;
            switch (this.type) {

                case AT_MOST:
                    nums = 0;
                    for (String commandName : this.commands) {
                        nums += options.isPassedIn(commandName) ? 1 : 0;
                    }

                    // s1 + s2 + ... sn <= k
                    return nums <= this.number;
                case AT_LEAST:
                    nums = 0;
                    for (String commandName : this.commands) {
                        nums += options.isPassedIn(commandName) ? 1 : 0;
                    }

                    // s1 + s2 + ... sn >= k
                    return nums >= this.number;
                case EQUAL:
                    nums = 0;
                    for (String commandName : this.commands) {
                        nums += options.isPassedIn(commandName) ? 1 : 0;
                    }

                    // s1 + s2 + ... sn == k
                    return nums == this.number;
                case MUTUAL_EXCLUSION:
                    nums = 0;
                    for (int i = 0; i < this.number; i++) {
                        nums += options.isPassedIn(this.commands.get(i)) ? 1 : 0;
                    }

                    if (nums > 0) {
                        // 前 k 个参数被传入, 则后面的参数不允许被传入
                        for (int i = this.number, l = this.commands.size(); i < l; i++) {
                            if (options.isPassedIn(this.commands.get(i))) {
                                return false;
                            }
                        }
                    }

                    return true;
                default:
                    return false;
            }
        } else {
            // 依赖验证器
            switch (this.type) {
                case SYMBIOSIS:
                    // s1 == s2 == ... == sn
                    int nums = 0;
                    for (String commandName : this.commands) {
                        nums += options.isPassedIn(commandName) ? 1 : 0;
                    }
                    return nums == this.commands.size() || nums == 0;
                case PRECONDITION:
                    // s1 >= s2 >= ... >= sn
                    int markStatus = 0;

                    // 反向迭代器
                    for (Iterator<String> it = this.commands.lastIterator(); it.hasNext(); ) {
                        String commandName = it.next();
                        int currentStatus = options.isPassedIn(commandName) ? 1 : 0;
                        if (currentStatus >= markStatus) {
                            markStatus = currentStatus;
                        } else {
                            return false;
                        }
                    }
                    return true;
                default:
                    return false;
            }
        }
    }

    /**
     * 获取描述
     *
     * @return 获取该参数的描述信息 (数学语言)
     */
    public String getMathDescription() {
        switch (this.type) {
            case AT_MOST:
                return ((StringArray) this.commands.apply(s -> "'" + s + "'")).join(" + ") + " <= " + this.number;
            case AT_LEAST:
                return ((StringArray) this.commands.apply(s -> "'" + s + "'")).join(" + ") + " >= " + this.number;
            case EQUAL:
                return ((StringArray) this.commands.apply(s -> "'" + s + "'")).join(" + ") + " == " + this.number;
            case MUTUAL_EXCLUSION:
                if (this.number > 0 && this.number < this.commands.size()) {
                    String condition1 = this.number + " * u >= " + ((StringArray) this.commands.get(0, this.number).apply(s -> "'" + s + "'")).join(" + ") + " >= u";
                    String condition2 = (this.commands.size() - this.number) + " * v >= " + ((StringArray) this.commands.get(this.number, this.commands.size() - this.number).apply(s -> "'" + s + "'")).join(" + ") + " >= v";
                    String condition3 = "1 - u >= v";
                    String condition4 = "u, v in {0, 1}";
                    return condition1 + " && " + condition2 + " && " + condition3 + " && " + condition4;
                } else {
                    // 否则为无效规则
                    return "(Not Available)";
                }
            case PRECONDITION:
                return ((StringArray) this.commands.apply(s -> "'" + s + "'")).join(" >= ");
            case SYMBIOSIS:
                return ((StringArray) this.commands.apply(s -> "'" + s + "'")).join(" == ");
            default:
                return null;
        }
    }

    @Override
    public String toString() {
        switch (this.type) {
            case AT_MOST:
                return "{" + this.commands.join(", ") + "} can be specified with a maximum of " + this.number + " items";
            case AT_LEAST:
                return "{" + this.commands.join(", ") + "} should be specified with at least " + this.number + " items";
            case EQUAL:
                return "{" + this.commands.join(", ") + "} should be specified with " + this.number + " items";
            case MUTUAL_EXCLUSION:
                return "{" + this.commands.get(0, this.number).join(", ") + "} and {" + this.commands.get(this.number, this.commands.size() - this.number).join(", ") + "} are not allowed to be used together";
            case SYMBIOSIS:
                return "{" + this.commands.join(", ") + "} should be specified concurrently or not at all";
            case PRECONDITION:
                return "when the i-th command item in {" + this.commands.join(", ") + "} is specified, all the command items before it (i.e., index < i) should be specified concurrently";
            default:
                return null;
        }
    }

    @Override
    public Iterator<String> iterator() {
        return this.commands.iterator();
    }
}