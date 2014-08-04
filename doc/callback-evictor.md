#Callback Evictor Tool

##Why callbacks?
One of the most important aspects about building smooth and responsive web applications is the synchronization between all the different parts of the application such as data fetching, processing, animations, and user interface elements.
The main difference with a desktop or a native environment is that browsers do not give access to the threading model and provide a single thread for everything accessing the user interface (i.e. the DOM).
This means that all the application logic accessing and modifying the user interface elements is always in the same thread.
In order to tacke this, browsers provide asynchronous APIs such as the commonly used XHR (XMLHttpRequest or 'AJAX') APIs.
The way browsers expose asynchronous programming to the application logic is via events or callbacks. In event-based asynchronous APIs, developers register an event handler for a given object (e.g. HTML Element or other DOM objects) and then call the action.
The browser will perform the action usually in a different thread, and trigger the event in the main thread when appropriate.

----------

####Example of synchronous invocation
``` Java
public interface TravelDemo
{
    TravelService travelService= serviceFactory.createSyncService(TravelService.class);
    
    Departure departure= travelService.getDeparture(user.getEmail());
    System.out.println("flight id:" + departure.getFlightId());
}
```
This service usage requires the getDeparture() to be blocking, which will freeze the user interface until the data is fetched.
If the data is local in the JavaScript context then this might not be an issue, however if the data needs to be fetched from the network this could have dramatic impact on the user experience.
So now we need a way to fetch the same information from server but smoothly and avoiding any possible freeze in user interface.

For this purpose we can use async callbacks:
####Same service but using asynchronous call
``` Java
travelService.getDeparture(user.getEmail(), new AsyncCallback<Departure>() 
{
	public void onSuccess(Departure departure) 
	{
		System.out.println("flight id:" + departure.getFlightId());
	}
	public void onFailure(Throwable caught) 
	{
		//failure!
	}
});
```
----------
##What is Callback Evictor?
Callback-evictor is a tool that helps you to get rid of async callbacks hell.
Using continuation techniques combined with dynamic proxies capabilities, this module transforms any synchronous call to a asynchronous one with no special modification to your source code.
Instead of start creating nested callbacks, you can make sync services calls and it'll be converted to async internally.

----------

##What's the problem with callbacks?
####Take a look at callback hell..
``` Java
travelService.getDeparture(user.getEmail(), new AsyncCallback<Departure>() 
{
	public void onSuccess(Departure departure) 
	{
		travelService.getFlight(departure.getFlightId(), new AsyncCallback<Flight>() 
		{
			public void onSuccess(Flight flight) 
			{
				weatherService.getForecast(flight.getDate(), new AsyncCallback<Weather>() 
				{
					public void onSuccess(Weather weather) 
					{
						//ready!
					}
					public void onFailure(Throwable caught) 
					{
						//failure!
					}
				});
			}
			public void onFailure(Throwable caught) 
			{
				//failure!
			}
		});
	}
	public void onFailure(Throwable caught) 
	{
		//failure!
	}
});
```
----------
###And now... synchronous calls heaven!
``` Java
try 
{
	Departure departure= travelService.getDeparture(user.getEmail());
	Flight flight= travelService.getFlight(departure.getFlightId());
	Weather weather= weatherService.getForecast(flight.getDate()); 
	//ready!
} 
catch(Throwable caught) 
{
	//failure!
} 
```

----------
###How can I use it?

First of all you need to configure a yout environment to add Callback Evictor plugin.


``` Java
@DragomeConfiguratorImplementor
public class EvictorConfigurator extends CallbackEvictorConfigurator implements DragomeConfigurator
{
	public ExamplesApplicationConfigurator()
	{
		setEnabled(true);
	}
}
```

After you've configured Callback Evictor plugin you will be able to make synchronous invocations, and all of them will be converted to asynchronous ones in background. 

####For example
``` Java
    TravelService travelService= serviceFactory.createSyncService(TravelService.class);
    Departure departure= travelService.getDeparture(user.getEmail());
    System.out.println("flight id:" + departure.getFlightId());
```

Activating callback evictor, this code will produce an asynchronous call to travel service, disposing its execution thread giving control to the browser and taking the control again when resulting Departure value is available.



