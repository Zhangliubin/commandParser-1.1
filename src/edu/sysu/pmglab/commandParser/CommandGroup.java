package edu.sysu.pmglab.commandParser;

import edu.sysu.pmglab.check.Assert;
import edu.sysu.pmglab.commandParser.exception.CommandParserException;
import edu.sysu.pmglab.commandParser.types.IType;
import edu.sysu.pmglab.container.array.Array;
import edu.sysu.pmglab.container.array.BaseArray;
import edu.sysu.pmglab.container.array.StringArray;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * @author suranyi
 */

public class CommandGroup implements Iterable<CommandItem>, Cloneable {
    /**
     * 参数组内部的所有指令都是不重复的
     */
    final String name;
    final BaseArray<CommandItem> items = new Array<>(CommandItem[].class);
    final HashMap<String, CommandItem> registerCommands = new HashMap<>();

    /**
     * 构造器
     * @param name 参数组名
     */
    public CommandGroup(String name) {
        this.name = name;

        if (name == null || name.length() == 0) {
            throw new CommandParserException("syntax error: groupName is null");
        }
    }

    /**
     * 添加需要捕获的参数信息
     *
     * @param type 参数类型
     * @param commandNames 参数名
     * @return 注册进该参数组的参数项
     */
    public CommandItem register(IType type, String... commandNames) {
        Assert.NotNull(type);
        Assert.NotEmpty(commandNames);

        // 验证参数名是否合法、参数名不可重复
        for (String commandName : commandNames) {
            if (this.registerCommands.containsKey(commandName)) {
                throw new CommandParserException("syntax error: commandItem " + commandName + " already defined in " + this.getGroupName());
            }
        }

        // 校验通过
        CommandItem commandItem = new CommandItem(type, commandNames);
        for (String commandName : commandNames) {
            this.registerCommands.put(commandName, commandItem);
        }
        this.items.add(commandItem);
        return commandItem;
    }

    /**
     * 添加需要捕获的参数信息
     *
     * @param tClass 参数类型
     * @param commandNames 参数名
     * @return 注册进该参数组的参数项
     */
    public CommandItem register(Class<?> tClass, String... commandNames) {
        Assert.NotEmpty(commandNames);

        // 验证参数名是否合法、参数名不可重复
        for (String commandName : commandNames) {
            if (this.registerCommands.containsKey(commandName)) {
                throw new CommandParserException("syntax error: commandItem " + commandName + " already defined in " + this.getGroupName());
            }
        }

        // 校验通过
        CommandItem commandItem = new CommandItem(tClass, commandNames);
        for (String commandName : commandNames) {
            this.registerCommands.put(commandName, commandItem);
        }
        this.items.add(commandItem);
        return commandItem;
    }

    /**
     * 添加需要捕获的参数信息
     * @param commandItem 参数项
     * @return 注册进该参数组的参数项
     */
    public CommandItem register(CommandItem commandItem) {
        Assert.NotNull(commandItem);

        // 验证参数名是否合法、参数名不可重复
        for (String commandName : commandItem) {
            if (this.registerCommands.containsKey(commandName)) {
                throw new CommandParserException("syntax error: commandItem " + commandName + " already defined in " + this.getGroupName());
            }
        }

        // 校验通过
        for (String commandName : commandItem) {
            this.registerCommands.put(commandName, commandItem);
        }
        this.items.add(commandItem);

        return commandItem;
    }

    /**
     * 将另一个参数组的指令注册到本参数组
     * @param group 其他参数组
     * @return 本参数组
     */
    public CommandGroup registerAll(CommandGroup group) {
        Assert.NotNull(group);

        // 验证参数名是否合法、参数名不可重复
        Set<String> intersection = new HashSet<>(group.registerCommands.keySet());
        intersection.retainAll(this.registerCommands.keySet());
        if (intersection.size() != 0) {
            // 说明有重复指令名
            throw new CommandParserException("syntax error: commandItem " + StringArray.wrap(intersection.toArray(new String[0])).join(",") + " already defined in " + getGroupName());
        }

        // 校验通过, 添加指令
        for (CommandItem commandItem : group) {
            for (String commandName : commandItem) {
                this.registerCommands.put(commandName, commandItem);
            }
            this.items.add(commandItem);
        }

        return this;
    }

    /**
     * 获取参数组名
     * @return 获取参数组名
     */
    public String getGroupName() {
        return this.name;
    }

    /**
     * 是否包含参数项
     * @param commandName 参数名，可以是主参数名或副参数名
     * @return 是否包含参数名为 commandName 的参数项
     */
    public boolean contain(String commandName) {
        if (commandName == null) {
            return false;
        }
        return this.registerCommands.containsKey(commandName);
    }

    /**
     * 获取已注册的参数项
     * @param commandName 参数名
     * @return 获取参数项，为 null 说明不在该参数组中
     */
    public CommandItem get(String commandName) {
        return this.registerCommands.get(commandName);
    }

    /**
     * 当前参数组包含的参数项个数
     * @return 参数项个数
     */
    public int size() {
        return this.items.size();
    }

    @Override
    public Iterator<CommandItem> iterator() {
        return this.items.iterator();
    }

    @Override
    public CommandGroup clone() {
        CommandGroup newGroup = new CommandGroup(this.name);
        for (CommandItem commandItem : this) {
            CommandItem commandItemClone = commandItem.clone();
            newGroup.items.add(commandItemClone);
            for (String commandName : commandItemClone) {
                newGroup.registerCommands.put(commandName, commandItemClone);
            }
        }
        return newGroup;
    }
}
