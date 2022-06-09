package edu.sysu.pmglab.commandParser.usage;

import edu.sysu.pmglab.commandParser.CommandGroup;
import edu.sysu.pmglab.commandParser.CommandItem;
import edu.sysu.pmglab.container.array.Array;
import edu.sysu.pmglab.container.array.BaseArray;
import edu.sysu.pmglab.container.array.StringArray;

import java.io.Serializable;
import java.util.Objects;

/**
 * @author suranyi
 */

public class DefaultStyleUsage implements IUsage {
    /**
     * 默认样式 Unix 类型样式
     */
    public final static DefaultStyleUsage UNIX_TYPE_1 = new DefaultStyleUsage("Usage: ", " [options]", "", 2, 2, 80, false, "*", "^");
    public final static DefaultStyleUsage UNIX_TYPE_2 = new DefaultStyleUsage("Usage: ", " [options]", "", 2, 2, 120, false, "*", "^");
    public final static DefaultStyleUsage UNIX_TYPE_3 = new DefaultStyleUsage("Usage: ", " [options]", "", 2, 4, 80, true, "*", "^");
    public final static DefaultStyleUsage UNIX_TYPE_4 = new DefaultStyleUsage("Usage: ", " [options]", "", 2, 4, 120, true, "*", "^");

    /**
     * 标题样式
     */
    String before;
    String after;
    String subTitle;

    /**
     * 统一缩进
     * <indent1>^*--help, -help, -h<indent2>
     */
    final int indent1;
    final int indent2;
    final int maxLength;
    final boolean newLineAfterCommandName;

    /**
     * 类型标记
     */
    final String requestMark;
    final String debugMark;

    public DefaultStyleUsage(String before, String after, String subTitle, int indent1, int indent2, int maxLength, boolean newLineAfterCommandName, String requestMark, String debugMark) {
        this.before = before == null ? "" : before;
        this.after = after == null ? "" : after;
        this.subTitle = subTitle == null ? "" : subTitle;
        this.indent1 = indent1;
        this.indent2 = indent2;
        this.maxLength = maxLength;
        this.newLineAfterCommandName = newLineAfterCommandName;
        this.requestMark = requestMark == null ? "" : requestMark;
        this.debugMark = debugMark == null ? "" : debugMark;
    }

    @Override
    public String formatGroup(CommandGroup group, boolean debug) {
        // 按照 debug 和 hide 过滤参数
        BaseArray<CommandItem> commandItems = new Array<>(CommandItem[].class);

        for (CommandItem commandItem : group) {
            if (!commandItem.isHide()) {
                if (!debug && commandItem.isDebug()) {
                    continue;
                }

                commandItems.add(commandItem);
            }
        }

        if (commandItems.size() == 0) {
            return "";
        }

        // 开始格式化文档
        StringBuilder builder = new StringBuilder();
        builder.append(group.getGroupName() + ":");

        // 左侧统一缩进
        int commandNamesMaxLength = 0;
        for (CommandItem item : commandItems) {
            int currentItemLength = StringArray.wrap(item.getCommandNames()).join(",").length();
            if (item.isRequest()) {
                currentItemLength += requestMark.length();
            }
            if (item.isDebug()) {
                currentItemLength += debugMark.length();
            }
            if (commandNamesMaxLength < currentItemLength) {
                commandNamesMaxLength = currentItemLength;
            }
        }

        // 参数名总长度
        int descriptionPrefixLength;
        if (newLineAfterCommandName) {
            descriptionPrefixLength = indent2;
        } else {
            descriptionPrefixLength = commandNamesMaxLength + indent1 + indent2;
        }

        for (CommandItem commandItem : commandItems) {
            builder.append("\n");
            builder.append(generateSpaces(indent1));
            String commandLinked = StringArray.wrap(commandItem.getCommandNames()).join(",");
            int markLength = 0;
            if (commandItem.isDebug()) {
                builder.append(debugMark);
                markLength += debugMark.length();
            }
            if (commandItem.isRequest()) {
                builder.append(requestMark);
                markLength += debugMark.length();
            }
            builder.append(commandLinked);

            // 获取描述文档
            String description = commandItem.getDescription();

            if (commandItem.getDefaultValueOriginFormat() != null) {
                if (description.length() == 0) {
                    description += "default: " + commandItem.getDefaultValueOriginFormat();
                } else {
                    description += "\n" + "default: " + commandItem.getDefaultValueOriginFormat();
                }
            }

            boolean containFormat = false;
            if (!"".equals(commandItem.getFormat())) {
                if (description.length() == 0) {
                    description += "format: " + commandItem.getFormat();
                } else {
                    description += "\n" + "format: " + commandItem.getFormat();
                }
                containFormat = true;
            }

            if (commandItem.getValidator() != null) {
                if (containFormat) {
                    description += " (" + commandItem.getValidator() + ")";
                } else {
                    if (description.length() == 0) {
                        description += "validate: " + commandItem.getValidator();
                    } else {
                        description += "\n" + "validate: " + commandItem.getValidator();
                    }
                }
            }

            if (description.length() == 0) {
            } else {
                if (newLineAfterCommandName) {
                    builder.append("\n");
                    builder.append(generateSpaces(indent2));
                } else {
                    builder.append(generateSpaces(commandNamesMaxLength - commandLinked.length() - markLength + indent2));
                }

                // 包装描述信息
                wrapDescription(builder, descriptionPrefixLength, description);
            }
        }

        return builder.toString();
    }

    void wrapDescription(StringBuilder out, int indent, String description) {
        if (description.contains("\n")) {
            // 包含 \n，此时需要细微处理
            String[] descriptions = description.split("\n");

            if (descriptions.length > 1) {
                wrapDescription(out, indent, descriptions[0]);
                for (int i = 1; i < descriptions.length; i++) {
                    out.append("\n");
                    out.append(generateSpaces(indent));
                    wrapDescription(out, indent, descriptions[i]);
                }
            } else {
                wrapDescription(out, indent, descriptions[0]);
            }
        } else {
            String[] words = description.split(" ");
            int current = indent;

            for (int i = 0; i < words.length; ++i) {
                String word = words[i];
                if (word.length() > 0) {
                    if ((word.length() <= maxLength) && (current + 1 + word.length()) > maxLength) {
                        out.append("\n").append(generateSpaces(indent)).append(word).append(" ");
                        current = indent + word.length() + 1;
                    } else {
                        out.append(word);
                        current += word.length();
                        if (i != words.length - 1) {
                            out.append(" ");
                            ++current;
                        }
                    }
                }
            }
        }
    }

    String generateSpaces(int count) {
        StringBuilder result = new StringBuilder(count);
        for (int i = 0; i < count; ++i) {
            result.append(" ");
        }

        return result.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DefaultStyleUsage usage = (DefaultStyleUsage) o;
        return indent1 == usage.indent1 && indent2 == usage.indent2 && maxLength == usage.maxLength && newLineAfterCommandName == usage.newLineAfterCommandName && before.equals(usage.before) && after.equals(usage.after) && subTitle.equals(usage.subTitle) && requestMark.equals(usage.requestMark) && debugMark.equals(usage.debugMark);
    }

    @Override
    public int hashCode() {
        return Objects.hash(before, after, subTitle, indent1, indent2, maxLength, newLineAfterCommandName, requestMark, debugMark);
    }

    public String getBefore() {
        return before;
    }

    public String getAfter() {
        return after;
    }

    public int getIndent1() {
        return indent1;
    }

    public int getIndent2() {
        return indent2;
    }

    public int getMaxLength() {
        return maxLength;
    }

    public String getDebugMark() {
        return debugMark;
    }

    public String getRequestMark() {
        return requestMark;
    }

    public String getSubTitle() {
        if (subTitle.contains("\n")) {
            return subTitle.replace("\n", "\\n");
        }
        return subTitle;
    }

    public boolean isNewLineAfterCommandName() {
        return newLineAfterCommandName;
    }

    @Override
    public String formatHeader(String programName) {
        if (subTitle.length() == 0) {
            return before + programName + after;
        } else {
            return before + programName + after + "\n" + subTitle;
        }
    }
}
