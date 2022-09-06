package edu.sysu.pmglab.commandParser.usage;

import edu.sysu.pmglab.commandParser.CommandGroup;

/**
 * 文本格式化接口
 */

public interface IUsage {
    /**
     * 格式化文本
     *
     * @param group 参数组
     * @param debug 是否为 debug 模式
     * @return 格式化文档信息
     */
    String formatGroup(CommandGroup group, boolean debug);

    /**
     * 格式化文本
     *
     * @param group 参数组
     * @return 格式化文档信息
     */
    default String formatGroup(CommandGroup group) {
        return formatGroup(group, false);
    }

    /**
     * 格式化标题行
     *
     * @param programName 程序名
     * @return 标题行格式化结果
     */
    String formatHeader(String programName);
}
