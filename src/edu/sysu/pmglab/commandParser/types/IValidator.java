package edu.sysu.pmglab.commandParser.types;

/**
 * 验证器接口
 */

public interface IValidator {
    /**
     * 验证
     *
     * @param commandKey 参数类型
     * @param params     验证的参数 (通常是本值, 但字符串验证器可能会被修改值)
     * @return 返回验证后的结果
     */
    Object validate(String commandKey, Object params);

    /**
     * 获取属性
     * @param key 属性的键名
     * @return 属性值
     */
    default Object get(String key) {
        return null;
    }

    /**
     * 获取基础值类型
     * @return 基础值类型，判断是什么值的派生类
     */
    IType getBaseValueType();
}
