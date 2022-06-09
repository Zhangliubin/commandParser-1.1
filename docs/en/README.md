
# About Command Parser

CommandParser is a lightweight Java platform framework for quickly developing, parsing, and managing command-line arguments. It provides a basic graphical interface (CommandParserDesigner) for visually managing and editing command items.

CommandParser has the following advantages:

- Cross-platform: Developed based on Java8, all software libraries are standard libraries, which can run in a wide range of devices and JDK versions.
- GUI-platform: Commandparserdesigner-1.1. jar provides user-friendly graphical design and management features to help developers easily preview and manage parsers with hundreds or thousands of parameters. Graphically designed parsers can directly export Java script files to run.
- Script design: CommandParser supports pure script development and chain calls to complete parameter registration. Script files can also be imported into graphic design programs for visual editing and management.
- Lightweight: commandParser-1.1.jar only 400+ KB, no external dependencies. The core program package is separated from the graphical interface program to reduce the package size.
- Single line parsing: By `parser.parse (...)`, complete parsing of string arrays or files.
- Automated documents and highly customizable: automated documents meet almost all document design requirements, open interface design allows users to customize document styles.
- Support predetermined rules between parameters sets: complete verification of mutual exclusion and dependency of parameters in the parsing stage to reduce the amount of extra code;
- Development mode: Allows debugging parameters and user parameters to be developed in the same script file without interfering with each other.

![CommandParserFrame](../image/CommandParserFrame.png)

> [!COMMENT|label:Contact Developer]
> Liubin Zhang, suranyi.sysu@gmail.com