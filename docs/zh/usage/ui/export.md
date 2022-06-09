# 导出解析器

在 `Command Items` 标签页或 `Command Rules & Global Parameter` 标签页中, 点击右下角 `Save` 按钮保存当前解析器 (全局快捷键: Ctrl + S)。提供两种保存格式:

- **Java Script Builder With Options Format:** 解析器及参数项构建文件
- **Java Script Builder Format:** 解析器文件

![export-export](../../../image/export-export.png)

## Java Script Builder With Options Format 格式

`Java Script Builder With Options Format` 根据参数项的主参数名 (Command Name) 推断变量名 (驼峰命名法), 根据参数项的参数类型 (Command Type) 设置参数类型, 无需对变量进行类型转换。该格式支持的 API 方法如下:

| API 方法                                     | 返回值类型     | 描述                               |
| :------------------------------------------- | :------------- | :--------------------------------- |
| Parser options = Parser.parse(String[] args) | Parser         | 解析指令                           |
| Parser options = Parser.parse(File argsFile) | Parser         | 解析文件中的指令                   |
| Parser.usage()                               | String         | 获取解析器文档                     |
| Parser.getParser()                           | CommandParser  | 获取解析器对象                     |
| parser.getOptions()                          | CommandOptions | 获取解析参数值对象                 |
| options.变量名.value                         | T (范形)       | 获取参数值(未传入时, 该值为默认值) |
| options.变量名.isPassedIn                    | boolean        | 该参数项是否被传入                 |
| options.变量名.matchedParameter              | String         | 捕获的参数值 (字符串原始格式)      |

在入口函数使用解析器桥接参数指令与业务逻辑:

```java
public static void main(String[] args) {
    if (args.length == 0) {
        System.out.println(HttpDownloaderParser.getParser());
        return;
    }

    HttpDownloaderParser options = HttpDownloaderParser.parse(args);
    if (options.help.isPassedIn) {
        System.out.println(HttpDownloaderParser.getParser());
        return;
    }

    try {
        HttpDownloader2.instance(args[0])
                .setOutputFile(options.output.value)
                .setThreads(options.threads.value)
                .setPrintLog(true)
                .setTempDir(options.tempDir.value)
                .setProxy(options.proxy.value)
                .setTimeOut(options.timeout.value)
                .clean(options.overwrite.isPassedIn)
                .download();
    } catch (IOException e) {
        logger.error("{}", e.getMessage());
    }
}
```

## Java Script Builder Format 格式

`Java Script Builder Format` 创建解析器单例, 通过参数名 (参数项的任一参数名) 访问参数解析信息 (值、是否被传入、捕获值), 在获取值时需要进行格式转换。该格式支持的 API 方法如下:

| API 方法                            | 返回值类型     | 描述                                                         |
| :---------------------------------- | :------------- | :----------------------------------------------------------- |
| Parser.parse(String[] args)         | CommandOptions | 解析指令                                                     |
| Parser.parse(File argsFile)         | CommandOptions | 解析文件中的指令                                             |
| Parser.usage()                      | String         | 获取解析器文档                                               |
| Parser.getParser()                  | CommandParser  | 获取解析器对象                                               |
| options.get(参数名)                 | Object         | 获取参数值(未传入时, 该值为默认值) 获得的参数类型是 Object类型, 需要手动转换格式 |
| options.isPassedIn(参数名)          | boolean        | 该参数项是否被传入                                           |
| options.getMatchedParameter(参数名) | String         | 捕获的参数值 (字符串原始格式)                                |

在入口函数使用解析器桥接参数指令与业务逻辑:

```java
public static void main(String[] args) throws IOException {
    if (args.length == 0) {
        // 没有传入参数时, 打印文档
        System.out.println(HttpDownloaderParser.usage());
        return;
    }

    CommandOptions options = HttpDownloaderParser.parse(args);
    if (options.isHelp()) {
        // 传入 help 指令时, 打印文档
        System.out.println(HttpDownloaderParser.getParser());
        return;
    }

    // 业务逻辑
    try {
        HttpDownloader.instance(args[0])
                .setOutputFile((File) options.get("--output"))
                .setThreads((int) options.get("--threads"))
                .setPrintLog(!options.isPassedIn("--no-log"))
                .setTempDir((File) options.get("--temp-dir"))
                .setProxy((String) options.get("--proxy"))
                .setTimeOut((int) options.get("--time-out"))
                .clean(options.isPassedIn("--overwrite"))
                .download();
    } catch (IOException e) {
        logger.error("{}", e.getMessage());
    }
}
```

