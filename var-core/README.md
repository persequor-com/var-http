<h2 align="center"><b>Var-HTTP Core</b></h2>

<h4 align="center">This project is named after the Norse god Vár, being the god of oaths and agreements (see <a href="https://en.wikipedia.org/wiki/V%C3%A1r" alt="Wikipedia page of Vár">here</a>).</h4>

<p align="center">
<a href="https://github.com/persequor-com/var-http/tags" alt="GitHub release"><img src="https://img.shields.io/github/v/tag/persequor-com/var-http?label=version"></a>
<a href="https://spdx.org/licenses/MIT.html" alt="License: MIT"><img src="https://img.shields.io/github/license/persequor-com/var-http"></a>
<a href="https://github.com/persequor-com/var-http/actions" alt="Build Status"><img src="https://img.shields.io/github/workflow/status/persequor-com/var-http/Java%20CI%20with%20Gradle"></a>
</p>
<hr>
<p align="center">Maintained by the <a href="https://psqr.eu/">PSQR team</a>.</p>


## Hello World

Var provides a thin and performant web framework for Java, allowing you to easily create, maintain and extend HTTP APIs.
Unlike the massive frameworks available among the Java community, its scope purposely remains limited to Web APIs.
Therefore, if you start a project from scratch, you will most likely have to depend on others libraries, and do some configuration for everything to work well together. 

Luckily, we got you covered and var comes with the `var-test` subproject, whose purpose it to demonstrates how to quickly set up a project with Var from scratch.
Follow the guide [here](../var-test/README.md).

Var is built with flexibility, extensibility and customization at its core : many of the default behaviours can be overridden by simply re-mapping an interface to your implementation in your dependency injection framework. 

## Controllers

Var is a controller-based framework, meaning that you create new API endpoints by registering controllers.

### Hello world
Var comes with a `@Controller` annotation, as well as everything needed for the framework to automatically map the methods to HTTP endpoints.
That means, registering an HTTP endpoint only requires two steps : 
1. Create a method annotated with `@Controller`
```java
public class MyControllerClass{
	@Controller(path = "/hello")
	public String hello(){ return "Hello World !";}
} 

```
2. Register the class in which the method is located to the server.
```java
	server.configure(config-> config.addController(MyControllerClass.class));
```

**Result** :
```shell
> curl localhost:1234/hello
Hello World !
```
### Path variables
The controller annotation natively supports path variables.
Specify the path variable in the controller path using the curly bracket syntax.
The method parameter can then be annotated with `@PathVariable`, with the property `name` matching the value from the controller's path.

Optionally, a description can be passed to the `@PathVariable`.

**Example :**
```java
public class MyControllerClass{
	@Controller(path = "/hello/{name}")
	public String hello(@PathVariable(name = "name") String value){ 
		return "Hello " + value + " !";
	}
} 
```
**Result :**
```shell
> curl localhost:1234/hello/John
Hello John !
```

**Note**: There is no limitations in the number of path parameters, as long as their names are unique.

### Request parameters
Similarly to Path variables, Var supports HTTP request parameters via the `@RequestParameter` annotation.
The name property is required, and additionally the parameter can be set required, and/or provided with a default value.

**Example :**
```java
public class MyControllerClass{
	@Controller(path = "/hello")
	public String hello(@RequestParameter(name = "name") String value){ 
		return "Hello " + value + " !";
	}
} 
```
**Result :**
```shell
> curl localhost:1234/hello?name=John
Hello John !
```

Path variables and Request parameters can easily be combined in a single endpoint.

### Parameter serialization
Var comes with native support for serializing common standard Java types. 
You can rely on the default serializer provided in `var-serializer-guice` (`VarTestSerializer`, backed up by [Jackson](https://github.com/FasterXML/jackson)), or implement your own.
Simply bind the `Serializer` interface in your DI framework.

Once done, you can simply use typed Objects in your controller methods.

## Filters

## Injection

## Servlet
Var is built on top of the java-standard HttpServlet.

##Logging
Var comes with SLF4J, making it natively compatible with all the most common loggers (Logback, Log4J, native Java, etc).
SLF4J is a logger abstraction for Java, allowing streamlining the logs coming from different libraries.
Read more about SLF4J and how to configure it [here](https://www.slf4j.org/).

Note that by default no logger is registered, resulting in a no-op logger. In other words, nothing will happen. 
You will have to choose a logger implementation for your projects, and add it as a dependency. 

## Contributing
For contribution guidelines, see [CONTRIBUTING](../CONTRIBUTING.md).

## License
Var-HTTP is licensed under the terms of the [MIT](../license.txt) License.