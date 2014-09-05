#What is Dragome?
Dragome is an open source tool for creating web applications in pure Java language.  
Based on bytecode to javascript compilation, you may execute applications written in Java directly on browsers.  
You can use your favorite IDE, your favorite Java frameworks and tools because Dragome is totally transparent.  


[Learn how to code Dragome apps in 2 minutes][1]

----------

##Why Dragome
* Full Java stack web applications, code everything in Java (server side and client side). It will be transformed to javascript automatically.
* Fast GUI development using components, builders and two-way databinding, using similar AngularJs concepts.
* Debug mode for debugging in your favorite Java IDE.
* No IDE plugins or browser plugins required.
* Java 8 ready! Build your pages using lambda expressions, default methods, streams
* You can use [Dynamic Proxies][2] and [Java Reflection API][3]
* Get rid of callbacks hell: Make async calls with no callbacks! How? See [Callback Evictor][4] Tool!
* Use continuation in your development: you can pause your resulting js program and continue it whenever you need.
* Unit testing integration: you can also run your Junit tests on a browser.
* Javascript native interface for low level component construction.
* Very powerful template engine. No logic in HTML files, only ids for locating templates placeholders.
* ... more at [Features][5]


Also see [Dragome Todos](doc/todos.md) for more info about the future

----------


###Start working with Dragome right now!
#### Using maven archetype
```shell
mvn archetype:generate -DarchetypeGroupId=com.dragome -DarchetypeArtifactId=simple-webapp-archetype -DarchetypeVersion=1.0 -DgroupId={your-package-name} -DartifactId={your-app-name}
```

#### Using maven dependency in your project
``` xml
<dependency>
  <groupId>com.dragome</groupId>
  <artifactId>dragome-sdk</artifactId>
  <version>0.95.1-beta1</version>
  <type>pom</type>
</dependency>
```

#### Clone and build
[How to build the SDK][6]


#### Setup example application
[Setup your application](doc/app-setup.md)

----------

###How is programming web apps with Dragome?
**pure Java, pure HTML, runs as js inside browser!**



### See it in action
[![ScreenShot](doc/crud-debugging-screenshot.jpg)](http://youtu.be/WyseTuRZkNk)



### Debug your application in Java with your favorite IDE
[![ScreenShot](doc/crud-debugging-screenshot.jpg)](http://youtu.be/ktlMWKNVhgo)


----------

### Want to contribute?

* Fork the project on Github.
* Create an issue or fix one from the issues list.
* Share your ideas or ask questions on mailing list - don't hesitate to write a reply - that helps us improve javadocs/FAQ.
* If you miss a particular feature - browse or ask on the [mailing list](https://groups.google.com/d/forum/dragome) - don't hesitate to write a reply, show us a sample code and describe the problem.
* Write a blog post about how you use or extend Dragome.
* Please suggest changes to javadoc/exception messages when you find something unclear.
* If you have problems with documentation, find it non intuitive or hard to follow - let us know about it, we'll try to make it better according to your suggestions. Any constructive critique is greatly appreciated. Don't forget that this is an open source project developed and documented in spare time.

----------

###Take a look at the following source code:

For more details see [Hello World Application][7]

**Service definition**
``` Java
public interface HelloWorldService
{
	public abstract String getGreetingsFor(String name);
}
```

**Service implementation**
``` Java
public class HelloWolrdServiceImpl implements HelloWorldService
{
	public String getGreetingsFor(String name)
	{
		return "Hello " + name + "! (" + new Date() + ")";
	}
}
```

**Service consumer - GUI**
``` Java
public class HelloWorldPage extends DragomeVisualActivity
{
	HelloWorldService helloWorldService= serviceFactory.createSyncService(HelloWorldService.class);

	public void build()
	{
		final VisualLabel<String> label= new VisualLabelImpl<String>("message");
		final VisualButton button= new VisualButtonImpl("button", v -> label.setValue(helloWorldService.getGreetingsFor("World")));
		mainPanel.addChild(label);
		mainPanel.addChild(button);
	}
}
```

**HTML**
``` Html
<html>
<head>
<script type="text/javascript" src="dragome/dragome.js"></script>
</head>

<body>
	Message: <b data-template="message">text</b> <br>
	<button data-template="button">Say hello!</button>
</body>

</html>
```


  [1]: doc/two-minutes-tutorial.md#DRAGOME%202'%20TUTORIAL
  [2]: http://docs.oracle.com/javase/7/docs/api/java/lang/reflect/Proxy.html
  [3]: http://docs.oracle.com/javase/tutorial/reflect/
  [4]: doc/callback-evictor.md
  [5]: doc/features.md
  [6]: doc/how-to-build.md
  [7]: doc/helloworld-app.md
