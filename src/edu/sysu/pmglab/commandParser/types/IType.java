package edu.sysu.pmglab.commandParser.types;

import edu.sysu.pmglab.commandParser.exception.ParameterException;
import edu.sysu.pmglab.container.array.Array;

/**
 * 类型接口
 */

public interface IType {
    IType NONE = new IType() {
        @Override
        public Object convert(String... args) {
            if (args.length != 0) {
                throw new ParameterException("None type don't accept any values");
            }
            return null;
        }

        @Override
        public Object getDefaultValue() {
            return null;
        }

        @Override
        public int getDefaultArity() {
            return 0;
        }

        @Override
        public String getDefaultFormat() {
            return "";
        }

        @Override
        public IType getBaseValueType() {
            return NONE;
        }

        @Override
        public String toString() {
            return "NONE";
        }

        @Override
        public boolean equals(Object type) {
            return type instanceof IType && type.toString().equals("NONE");
        }
    };

    /**
     * 获取解析实例
     *
     * @param type 类型名
     * @return 返回 IType 类型实例
     */
    static IType of(String type) {
        type = type.toUpperCase();
        int index = type.indexOf("_");
        String mainType = index == -1 ? type : type.substring(0, index);
        String subType = index == -1 ? "VALUE" : type.substring(index + 1);

        // 识别主类型
        switch (mainType) {
            case "NONE":
                return NONE;
            case "BOOLEAN":
                return BOOLEAN.valueOf(subType);
            case "BYTE":
                return BYTE.valueOf(subType);
            case "SHORT":
                return SHORT.valueOf(subType);
            case "INTEGER":
                return INTEGER.valueOf(subType);
            case "LONG":
                return LONG.valueOf(subType);
            case "FLOAT":
                return FLOAT.valueOf(subType);
            case "DOUBLE":
                return DOUBLE.valueOf(subType);
            case "STRING":
                return STRING.valueOf(subType);
            case "FILE":
                return FILE.valueOf(subType);
            default:
                break;
        }

        return null;
    }

    /**
     * 获取解析实例
     *
     * @param type 基本类型名
     * @return 该类型支持的参数名
     */
    static String[] supportedList(String type) {
        type = type.toUpperCase();

        // 识别主类型
        switch (type) {
            case "NONE":
                return new String[]{};
            case "BOOLEAN":
                return Array.wrap(BOOLEAN.values()).apply(subType -> {
                    String subTypeName = subType.toString();
                    int index = subTypeName.indexOf("_");
                    if (index == -1) {
                        return "VALUE";
                    } else {
                        return subTypeName.substring(subTypeName.indexOf("_") + 1);
                    }
                }).toArray(new String[0]);
            case "BYTE":
                return Array.wrap(BYTE.values()).apply(subType -> {
                    String subTypeName = subType.toString();
                    int index = subTypeName.indexOf("_");
                    if (index == -1) {
                        return "VALUE";
                    } else {
                        return subTypeName.substring(subTypeName.indexOf("_") + 1);
                    }
                }).toArray(new String[0]);
            case "SHORT":
                return Array.wrap(SHORT.values()).apply(subType -> {
                    String subTypeName = subType.toString();
                    int index = subTypeName.indexOf("_");
                    if (index == -1) {
                        return "VALUE";
                    } else {
                        return subTypeName.substring(subTypeName.indexOf("_") + 1);
                    }
                }).toArray(new String[0]);
            case "INTEGER":
                return Array.wrap(INTEGER.values()).apply(subType -> {
                    String subTypeName = subType.toString();
                    int index = subTypeName.indexOf("_");
                    if (index == -1) {
                        return "VALUE";
                    } else {
                        return subTypeName.substring(subTypeName.indexOf("_") + 1);
                    }
                }).toArray(new String[0]);
            case "LONG":
                return Array.wrap(LONG.values()).apply(subType -> {
                    String subTypeName = subType.toString();
                    int index = subTypeName.indexOf("_");
                    if (index == -1) {
                        return "VALUE";
                    } else {
                        return subTypeName.substring(subTypeName.indexOf("_") + 1);
                    }
                }).toArray(new String[0]);
            case "FLOAT":
                return Array.wrap(FLOAT.values()).apply(subType -> {
                    String subTypeName = subType.toString();
                    int index = subTypeName.indexOf("_");
                    if (index == -1) {
                        return "VALUE";
                    } else {
                        return subTypeName.substring(subTypeName.indexOf("_") + 1);
                    }
                }).toArray(new String[0]);
            case "DOUBLE":
                return Array.wrap(DOUBLE.values()).apply(subType -> {
                    String subTypeName = subType.toString();
                    int index = subTypeName.indexOf("_");
                    if (index == -1) {
                        return "VALUE";
                    } else {
                        return subTypeName.substring(subTypeName.indexOf("_") + 1);
                    }
                }).toArray(new String[0]);
            case "STRING":
                return Array.wrap(STRING.values()).apply(subType -> {
                    String subTypeName = subType.toString();
                    int index = subTypeName.indexOf("_");
                    if (index == -1) {
                        return "VALUE";
                    } else {
                        return subTypeName.substring(subTypeName.indexOf("_") + 1);
                    }
                }).toArray(new String[0]);
            case "FILE":
                return Array.wrap(FILE.values()).apply(subType -> {
                    String subTypeName = subType.toString();
                    int index = subTypeName.indexOf("_");
                    if (index == -1) {
                        return "VALUE";
                    } else {
                        return subTypeName.substring(subTypeName.indexOf("_") + 1);
                    }
                }).toArray(new String[0]);
            default:
                break;
        }

        return null;
    }

    /**
     * 转换值
     *
     * @param args 参数
     * @return 转换结果
     */
    Object convert(String... args);

    /**
     * 获取默认值
     *
     * @return 默认值
     */
    Object getDefaultValue();

    /**
     * 获取默认参数长度
     *
     * @return 默认参数长度
     */
    int getDefaultArity();

    /**
     * 获取默认的数值格式
     *
     * @return 默认数值格式
     */
    String getDefaultFormat();

    /**
     * 获取值类型
     *
     * @return 基本值类型
     */
    IType getBaseValueType();

    /**
     * 检查该类型是否合法
     *
     * @param type 类型
     * @return 是否为合法的类型转换器
     */
    static boolean checkType(IType type) {
        return of(type.toString()) != null;
    }
}

