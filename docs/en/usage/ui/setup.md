# Launch Software {#启动设计器图形界面}

Enter `java -jar ./commandParserDesigner-1.1.jar` in the terminal or double click JAR package to start the graphical interface.

![setup-setup](../../../image/setup-setup.png)

> [!TIP|label:Set shortcut command]
>
> Use the following way to set `java -jar . /commandParserDesigner-1.1.jar` as a shortcut command on Macos or Unix systems.
>
> ```bash
> # open the file of environment variables
> vim ~/.zshrc
> 
> # Add instruction shorthand (${path} is the abosulotely path of "commandParserDesigner-1.1.jar")
> alias commandParser="java -jar ${path}"
> 
> # Click Esc, enter ":x" and press "Enter", sava and quit this file
> source ~/.zshrc
> ```

# Check For Updates

Click the right mouse button on the tab bar to expand the menu. Clicking "Check for Updates" will help to check for the new version of `Command Parser` and `Command Parser Designer`.

![setup-update](../../../image/setup-update.png)

# View Logbook

Users can view the work log of the current program in the `Logger` tab, the log system was built based on log4j + Logback.

> [!DANGER|label:Command Parser Designer is only used as single software]
>
> Please launch the GUI interface before loading the log system for redirecting logs to the JSwing panel requires successfully. Thus, when importing the commandParserDesigner.jar as package, GUI components will be loaded whether or not the GUI is actively invoked, and then effects the main business logic.

![setup-logger](../../../image/setup-logger.png)

Log information is also displayed at the terminal when starting from the terminal (garbled characters will be displayed if the terminal does not support this character set):

![setup-console](../../../image/setup-console.png)