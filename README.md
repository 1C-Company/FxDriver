# FxDriver - Selenium based JavaFX testing framework

Description
============
FxDriver is a driver for Selenium WebDriver project for testing JavaFX desktop applications. 
This project allows you to write system tests for a whole application, finds controls inside application windows and interacts with them.
The project is a single jar file and you can attach it to your JavaFX application as a java agent.

Usage
============
* Run your application with option: ```-javaagent:/path/to/fxagent.jar```
* This agent runs selenium server inside your application using port 4444 (you can change this: ```-javaagent:/path/to/fxagent.jar=port=5500```)
* Use RemoteWebDriver instance to connect to this server. Provide port number and browserName parameters. Example:
```java
new RemoteWebDriver(new URL("http://localhost:4444/wd/hub/"), new DesiredCapabilities("javafx", "", Platform.ANY));
```
* Use standard WebDriver methods for writing tests.

Features
============
* Id and class name selectors are supported. css selector exists in experimental mode.
* You can find element inside another element.
* Mouse and keyboard interactions. Class "Actions" works as expected.
* getAttributes returns a lot of useful properties:
  * Any JavaFX properties available by their names.
  * For any node `tooltip` property exists with the tooltip text
  * There is a `progress` property for the Progress Bar node. This property contains progress value. Integer value between 0 and 100. Progress -1 means infinite progress bar.
  * `selected` property available for CheckBox node. Two values are supported: `true` and `false`.
* getWindowHandle() returns PID (Process ID) of application under test.
* Works on both Java 8 and Java 9.

Classloading
============
As we know, Java loads agent classes using bootstrap classloader. That behaviour produces problems, because selenium server uses many classes from well known libraries, like guava. They will conflict with classes used in AUT.
To avoid this problem, we use separate classloader. After application starts with agent parameter, it loads only one agent class using bootstrap classloader. Agent creates new class loader with fxdriver classes and sets parent classloader to bootstrap classloader. This is important, because application classloader contains AUT classes and we want to avoid conflicts.