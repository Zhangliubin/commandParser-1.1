# 重编辑解析器文件

导出的解析器文件是 Java 源代码文件 (如下图), 用户可以直接修改 Java 源代码文件实现解析器的修改。此外,设计器图形界面也支持导入该源码文件 (拖拽文件到界面窗口、点击 Open 按钮或快捷键 Ctrl + O) 实现重编辑。

CommandParserDesigner 将解析器源代码文件动态编译为 class 文件, 并通过 .getParser() 获取解析器对象。最后, 设计器图形界面根据解析器对象的成员信息复现解析器。

![reedit-reedit](../../../image/reedit-reedit.png)