# Command Rule Management {#管理参数规则}

Add command rules between command items in the `Command Rules & Global Parameter` tab. Click the right mouse button in the blank area of the command rule panel or select the command rule to expand the management menu. The management menu contains the following six operations:

- **New:** Create a new command rule (shortcut: Ctrl + N).
- **Insert:** Insert a new command rule at the current location.
- **Edit:** Edit the specified command rule (shortcut: Double-click the command rule).
- **Delete:** Delete the specified command rule (shortcut: Ctrl + Delete).
- **Up:** Move up the specified command rule (shortcut: Ctrl + U).
- **Down:** Move down the specified command rule (shortcut: Ctrl + D).

In the menu bar below, "+" corresponds to New, "-" corresponds to Delete, "↑" corresponds to Up, and "↓" corresponds to Down.

![commandrules-managercommandrules](../../../image/commandrules-managercommandrules.png)

# Search Command Rule {#搜索参数规则}

In the search box (Ctrl + F) at the lower part of the command rule panel, users can set the search attribute (check box) and content (text box), and then press Enter to search the matched command rule.

![commandrules-searchrules](../../../image/commandrules-searchrules.png)

# Edit Command Rule {#编辑参数规则}

When creating or editing a command rule, the command rule sub-panel will pop up. Set rules for command items including "Help", "Request" is not allowed.

![commandrules-editrule](../../../image/commandrules-editrule.png)

# Command Rule Type {#参数规则类型}

When the command items are specified from $$\{p_1,p_2,\cdots,p_n\}$$ , the meanings of different command rule types are showing as follows:

| Command Rule Type  | Conditional Number | Description                               |
| :--------------------- | :--------: | :----------------------------------------------------- |
| AT_MOST                | $$k$$      | $$p_1,p_2,\cdots,p_n$$ can be specified with a maximum of $$k$$ items. |
| AT_LEAST               | $$k$$      | $$p_1,p_2,\cdots,p_n$$  should be specified with at least $$k$$ items. |
| EQUAL                  | $$k$$      | $$p_1,p_2,\cdots,p_n$$  should be specified with $$k$$ items. |
| MUTUAL_EXCLUSION | $$k$$      | $$p_1,p_2,\cdots,p_k$$  and $$p_{k+1},p_{k+2},\cdots,p_n$$ are not allowed to be used together. |
| SYMBIOSIS        | /    | $$p_1,p_2,\cdots,p_n$$  should be specified concurrently or not at all. |
| PRECONDITION | /    | When  $$p_j$$ is passed in，$$p_i (i<j)$$ should be passed in concurrently. |

