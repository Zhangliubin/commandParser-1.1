# Import CommandParser.jar

**Method 1 :** In Project Structure, select Libraries and add commandParser-1.1.jar.

![usecommandparser-way1](../../image/usecommandparser-way1.png)

**Method 2:** Create a lib folder in your project, add the package of commandParser-1.1.jar, and right-click "Add as Library...".

![usecommandparser-way2](../../image/usecommandparser-way2.png)

# Create  Jar Package

After creating the parser source file, importing the commandParser-1.1.jar package, and writing the entry function (main class), the command-line program development is completed. Finally, package the Java project as a JAR package (using IDEA as an example):

Click on: Project Structure... > Artifacts > + > JAR > From modules with dependencies... Displays the left view window. Select the entry function location in Main Class and click OK. Finally, go to Build > Build Artifacts.

![usecommandparser-exportJar](../../image/usecommandparser-exportJar.png)