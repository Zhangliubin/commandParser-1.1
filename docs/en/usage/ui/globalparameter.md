# Set Global Parameters

Set parser's global parameters in `Command Rules & Global Parameter` tab. Global parameter includes the followings:

- **Program Name**

- **Usage Style:** Format of automatic document. Double click can edit format, choose "..." will create new document format.

- **Offset:** offset of input parameters.

  - Skip the first `offset` parameters of the input parameters.

  - ```
    # when offset = 3, the following commmands will skip the first three parameter and parse "--level 5 -t 4 -o ~/test.gz"
    bgzip compress <file> --level 5 -t 4 -o ~/test.gz
    ```

- **Max Matched Items:** the maximum number of matched command items.

  - Set the maximum number of matched command items (default value: `-1`). When the maximum number of input and matched command items is reached, the subsequent parameters are no longer parsed, but the parameter value of the last matched command item.

  - ```
    # when maxMatchedItems = 1, the following commmands only matched "bgzip" and following parameters, "compress <file> decompress <file>",  will be the value of "bgzip"
    bgzip compress <file> decompress <file>
    ```

- **AutoHelp:** When no parameter is passed in, add default parameter "help" automatically.

- **@Syntax:** Whether identify `@` as symbol of getting address (file content the address corresponding is as input parameter).

- **Debug:** Debug model switch. The command items tagged with "Debug" will only can be shown and used in debug mode.

![globalparameter-set](../../../image/globalparameter-set.png)

# Setting the format of automated documents

On the `Command Rules & Global Parameter` tab, double click the `Usage Style` check box or drop down to select `...` , open the document format editor. The format editor is used to control the automated document format in the `Usage` tab (as shown on the left), where you can also copy and search (Ctrl + F).

![globalparameter-usage](../../../image/globalparameter-usage.png)