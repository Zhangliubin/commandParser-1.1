package edu.sysu.pmglab.commandParser.types;

/**
 * @author suranyi
 * @description 参数验证器接口
 */

public interface IValidator {
    /**
     * 验证
     *
     * @param commandKey 参数类型
     * @param params     验证的参数
     */
    Object validate(String commandKey, Object params);

    /**
     * 获取属性
     */
    default Object get(String key) {
        return null;
    }

    /**
     * 获取值类型
     */
    IType getBaseValueType();
}
