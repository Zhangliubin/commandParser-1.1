# Modify the Parser File

The exported parser file is the Java source code file (as shown in the following figure). Users can modify the Java source code file directly to modify the parser. In addition, the designer graphical interface also supports importing the source file (drag the file to the interface window, click the Open button, or the shortcut: Ctrl + O) for reediting.

CommandParserDesigner dynamically compiles the parser's source file to a class file and obtains the parser object via `.getParser()`. Finally, the designer graphical interface reproduces the parser based on the member information of the parser object.

![reedit-reedit](../../../image/reedit-reedit.png)