#HTML DOM Handling Tutorial

## Abstractions
Dragome SDK philosophy aims to use or create always an abstraction to perform operations over HTML/CSS.
Component abstraction working in cooperation with templates abstractions could achieve almost any complex UI representation. But direct DOM handling could be useful in case you need to create low level HTML components as the existing Button, Checkbox, Label, List, etc. 

## Layers
DOM manipulation is the first layer over HTML handling, there are more layers located on top this one such as components, templates, and a third level layer on top of them like forms binding module, and component builders.
Since all mentioned layers have dependencies only from higher level to lower level, top layers could be easily dropped in case you just want to perform basic operations.

## W3C standards
In general Dragome uses standard W3C classes for DOM representation. 
These are the ones you can find located in w3c.dom package, ex: *org.w3c.dom.Document, org.w3c.dom.Element*


##HTML Document
To obtain a reference to current HTML document you may use:

``` Java
Document document= ServiceLocator.getInstance().getDomHandler().getDocument();
```

##Finding an element
Document can be inspected as you already do in js, to find an element by id:

``` Java
Element div1= document.getElementById("div1");
```

##Reading/Writing an attribute
The same with elements and their attributes:

``` Java
String className= div1.getAttribute("class");
div1.setAttribute("style", "position: relative;");
```

##Listening an event
You can add an event handler to any element using the following helper passing W3C standard classes:

``` Java
EventDispatcherImpl.setEventListener(div1, new EventListener()
{
	public void handleEvent(Event event)
	{
		if (event.getType().equals("click"))
			//do something
	}
}, "click");
```



