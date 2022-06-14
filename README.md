# About Command Parser

CommandParser is a lightweight Java platform for quickly developing, parsing, and managing command-line arguments. It provides a basic graphical interface (CommandParserDesigner) for managing and editing command items visually.

CommandParser has the following advantages:

- **Cross-platform**: It is developed based on Java8, and all software libraries are standardized, which can be run in a wide range of devices and JDK versions.
- **GUI-platform**: Commandparserdesigner-1.1. jar provides user-friendly graphical design and management functions to help developers easily preview and manage parsers with hundreds or thousands of parameters. Graphically designed parsers can directly export Java script files to run.
- **Script designer**: CommandParser supports pure script development, and parameter registration can be completed by invocation chaining. Script files can also be imported into GUI platform for visually editing and management.
- **Lightweight**: The commandParser-1.1.jar only occupies 400+ KB with no external dependencies. The core program package is separated from the graphical interface program to reduce the package size.
- **Single line parse**: It completes parsing of string arrays or files by `parser.parse (...)`.
- **Automated documents and highly customizable**: automated documents meet almost all requirements for document designing, and open interface design allows users to customize document styles.
- **Support predetermined rules between parameter sets**: complete verification of mutual exclusion and parameter dependency relationships in the parsing stage to reduce the amount of extra code;
- **Development mode**: Allows debugging parameters and user parameters to be developed in the same script file without interfering with each other.

# Command Parser 简介

CommandParser 是一个基于 Java 平台开发的轻量级框架，用于快速地开发、解析、管理命令行参数。它提供了一个基本的图形界面 (CommandParserDesigner)，用于可视化地管理、编辑命令项目。

CommandParser 具有如下优点:

- 跨平台: 基于 Java8 开发，所有软件库均为标准库，能够在广泛的设备及 JDK 版本中运行；
- 图形化设计: commandParserDesigner-1.1.jar 提供了友好的图形化设计与管理功能，帮助开发者轻松预览、管理具有成百上千参数项的解析器；图形化设计的解析器可以直接导出 Java 脚本文件运行；
- 脚本设计: CommandParser 支持纯脚本开发，链式调用完成参数项注册；脚本文件也可以导入图形化设计程序进行可视化编辑、管理；
- 轻量级: commandParser-1.1.jar 仅 400+ KB，无外部依赖；核心程序包与图形界面程序分离，减小包大小；
- 单行解析: 通过 `Parser.parse(...)` 完成字符串数组或文件的解析;
- 自动化文档及高度可定制化: 自动化文档几乎满足所有的文档设计需求，开放接口设计允许用户自定制文档风格；
- 支持指令集之间的预定规则: 在解析阶段完成指令互斥、依赖关系的校验，减少额外代码量；
- 开发模式: 允许调试参数与用户参数在同一个脚本文件中进行开发，互不干扰。

![CommandParser 框架](http://pmglab.top/commandParser/image/CommandParserFrame.png)

项目发布网站: http://pmglab.top/commandParser/

API 文档: http://pmglab.top/commandParser/api-docs
