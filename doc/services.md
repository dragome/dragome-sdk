#Services in Dragome

There are some parts of your application that need to be located at server side, such as database access, ORM interactions, heavy load proccesses, etc. 

For this purpose a services mechanism is provided, that helps you to communicate both sides with almost no effort. 
It's very simple, you need to create a Interface for your service, and then create a concrete class that implements it, just this.



Let's see an example:

## Service definition
**Service Interface**
``` Java
public interface HelloWorldService
{
	public abstract String getGreetingsFor(String name);
}
```

## Service implementation

Dragome will automatically resolve which is the implementor of your service, will create a proxy that serialize the data, sends the petition to server, deserialize data, and execute the desired method of concrete class. Then if there is a result it will transport it back to the browser using the same automatic mechanism.
Service implementations are not compile by Dragome compiler because they are located at server side exclusively, so you need to tell the compiler to avoid compilation over this type of classes. To do this you need to locate them in a package that contains the "serverside" token inside its name, for example: "**org.myproject.services.serverside**"

**Service Implementor**
``` Java
public class HelloWolrdServiceImpl implements HelloWorldService
{
	public String getGreetingsFor(String name)
	{
		return "Hello " + name + "! (" + new Date() + ")";
	}
}
```

---
## Synchronous calls
In case you want to execute a synchronous call:

**Service instantation**
``` Java
HelloWorldService helloWorldService= serviceFactory.createSyncService(HelloWorldService.class);
```

**Service invocation**
``` Java
String result= helloWorldService.getGreetingsFor("World");
```

---
## Asynchronous calls
And if you need to call it asynchronously, you need to create an AsyncServiceExecutor and then use it with a AsyncCallback:

**Service instantation**
``` Java
AsyncServiceExecutor<HelloWorldService> asyncHelloWorldExecutor= serviceFactory.createAsyncService(HelloWorldService.class);
	
```

**Service invocation**
``` Java
asyncHelloWorldExecutor.executeAsync(asyncHelloWorldExecutor.getService().getGreetingsFor("Fernando"), new AsyncCallback<String>()
{
	public void onSuccess(String result)
	{
		System.out.println(result);
	}

	public void onError()
	{
	}
});	
```

---
## 'ServiceImplementation' Annotation
If there is more than one implementors of your service and you want to specify Dragome what to choose you may use this annotation in interface definition:

**Service interface**
``` Java
@ServiceImplementation(HelloWolrdServiceImpl.class)
public interface HelloWorldService
{
	public abstract String getGreetingsFor(String name);
}
	
```




