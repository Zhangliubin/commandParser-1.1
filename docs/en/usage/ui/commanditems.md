# Management Command Group {#管理参数组}

Command items can be roughly divided into several categories according to their functions or attributes. The categories are command groups. In CommandParser, the command group is the basic unit for organizing command items. When the parser is initialized, a command group name `Options` is created by default and the first command item in this command group` --help, -help, -h `is created to call the help document.

On the `Command Items` tab, the command group panel is on the left. Click the right mouse button in the blank area of the command group panel or select the command group to expand the management menu. The management menu contains the following seven operations:

- **New:** Create new command group.
- **Insert:** Insert a new command group at the current location.
- **Delete:** Delete the command group.
- **Merge to:** Move all command items from this command group to another command group.
- **Up:** Move the command group up.
- **Down:** Move the command group down.
- **Rename:** Rename the command group.

![commanditems-managercommandgroup](../../../image/commanditems-managercommandgroup.png)

The order of the command groups affects the order in which an automated document output is displayed:

![commanditems-usage](../../../image/commanditems-usage.png)

# Management Command Item {#管理参数项}

On the `Command Items ` tab, the command items panel is on the right. Right-click a command item in the blank area of the command items panel to expand the management menu. The management menu contains the following seven operations:

- **New:** Create a new command item (shortcut: Ctrl + N).
- **Insert:** Insert a new command item at the current position.
- **Edit:** Edit the command item (shortcut: Double-click the command item).
- **Delete:** Delete this command item (shortcut: Ctrl + Delete).
- **Merge to:** Move this command item to another command group.
- **Up:** Up this command item (shortcut: Ctrl + U).
- **Down:** Down this command item (shortcut: Ctrl + D).

In the lower menu bar, "+" corresponds to New, "-" corresponds to Delete, "↑" corresponds to Up, and "↓" corresponds to Down.

![commanditems-managercommanditems](../../../image/commanditems-managercommanditems.png)

# Search Command Item {#搜索参数项}

In the search box on the lower part of the command items panel (shortcut: Ctrl + F), set the search attribute (check box) and content (text box, ignores case), press Enter to search for matched command item.

![commanditems-searchitem](../../../image/commanditems-searchitem.png)

# Edit Command Item {#编辑参数项}

When creating or editing a command item, the command item subpanel pops up, which has 12 command properties. `Command Name` and `Command Type` define the keyword and data Type of this command item. They are mandatory attributes:

![commanditems-editcommanditems](../../../image/commanditems-editcommanditems.png)

The means of properties are the followings:

| Propertie                  | Description                                       |
| :--------------------------------------- | :----------------------------------------------------------- |
| Command Name                 | 1. When command item have multiple names, separate them with commas, for example `--output,-o`. The first name is the main command name. <br />2. Parameter name format: 0-9A-ZA-Z +-\_; <br />3. After the command name is entered, click the Check button to check whether it conforms to the format and whether it has the repeated name. Only after passing the check, can other properties be set and submitted. |
| [Command Type](#参数格式) | MainType.DerivedType<br />see: [Command Type](#参数格式). |
| [Validator](#参数验证器) | Different types of command item support different validators, see: [Validator](#参数验证器) |
| Default                     | The default value is formatted by Command Type and validated by the Validator. The input format is consistent with the format defined by `Format `and `Arity. |
| Arity                      | Set the arity. -1 indicates an indefinite length (which can be 0 parameters). All parameters up to the next matched command item are regarded as the value of this command item. |
| Format               | The format document of command item. |
| Description          | The description document of command item. |
| Help             | When an command item marked Help is passed in, the parser does not format and validate the matched value. |
| Request              | When no command item for the `Help` flag are passed in, the command item marked `Request` must be passed in. |
| Hidden       | Command item marked `Hidden` will not display in the document. |
| Debug     | When the parser is in `not Debug mode`, the command item marked `Debug` is unavailable, the corresponding command rule is invalidated, and the command item is not displayed in the document. |

# Command Type {#参数格式}

Command Type `MainType.DerivedType`. Main types includes IType.NONE (only verify that it is passed in), BOOLEAN, BYTE, SHORT, INTEGER, LONG, FLOAT, DOUBLE, STRING, FILE. Derived types include the following 16 types. When a type is specified, it is assigned the default format document (i.e., `CommandName Format`). Types with the `... ` tags can be used to control the number of parameters captured using `arity`.

| Command Derived Type | Default Format      |
| :-------------------- | :---------------------------------------- |
| VALUE                 | value                                     |
| ARRAY                 | value value …                             |
| ARRAY_COMMA           | value,value,…                             |
| ARRAY_SEMICOLON       | value;value;…                             |
| SET                   | value value …                             |
| SET_COMMA             | value,value,…                             |
| SET_SEMICOLON         | value;value;…                             |
| MAP                   | key=value key=value …                     |
| MAP_COMMA             | key=value,key=value,…                     |
| MAP_SEMICOLON         | key=value;key=value;…                     |
| RANGE                 | value-value                               |
| LABEL_RANGE           | label:value-value label:value-value …     |
| LABEL_RANGE_COMMA     | label:value-value,label:value-value,…     |
| LABEL_RANGE_SEMICOLON | label:value-value;label:value-value;…     |
| LABEL_ARRAY           | label:value,value,… label:value,value,…   |
| LABEL_ARRAY_SEMICOLON | label:value,value,…;label:value,value,…;… |

# Validator {#参数验证器}

Different types of command item support different validators, as shown below:

| Command Type                              | Validator Support Type                                       |
| :---------------------------------------- | :----------------------------------------------------------- |
| None, Boolean                             | Don't support validator                                      |
| Byte, Short, Integer, Long, Float, Double | Numeric range validator:<br />1. Range (including boundary values): minimum to maximum; <br />2. Specify minimum value: ≥ minimum value. |
| String                                    | Multiple qualified values are separated by spaces.<br />ignoreCase: whether to ignores case;<br />indexAccess: allow the use of indexes instead of specific values (0 represents the first qualified value...). |
| File                                      | checkIsExists: The file path must exist. <br />checkIsFile: The file path cannot point to folder; <br />checkIsDirectory: The file path must point to a folder; <br />checkInnerResource: Preferentially identifies the current runtime resources (allowing access to internal JAR files). |

