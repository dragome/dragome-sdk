# Dragome JSNI (Javascript Native Interface)

JSNI is the mechanism used by Dragome to execute javascript code from Java (or bytecode).
DOM manipulation and several js-JRE classes make intensive use of this feature. For instance, reflection API, dynamic proxies, and essential classes such as Class and Method have a lot of interactions with javascript using JSNI feature.
When you execute the application in debug mode ("debug=true" at querystring), you can execute each single JSNI call one by one, for example to take a look at DOM changes step by step. Take a look at: 

[![ScreenShot](crud-debugging-screenshot.jpg)](http://www.youtube.com/embed/ktlMWKNVhgo)

To accomplish these interactions JSNI provides a set of methods located to com.dragome.commons.javascript.ScriptHelper class:
 1. put 
 2. eval
 3. evalInt / evalLong / evalFloat / evalDouble / evalChar / evalBoolean
 4. evalNoResult

*Note: all these JSNI methods receive as last argument a reference to the caller, this is necessary to detect the method (context) where the execution is performed. If you are calling it from a static method you must set it to null, otherwise always set it to "this".*


##**JSNI methods description** 
## *'put' method*
####Signature
``` Java
void put(String s, Object value, Object callerInstance)
```
Creates a variable in current javascript context.

-----

## *'eval' method*
####Signature
``` Java
Object eval(String expression, Object callerInstance)
```
Returns the resulting value of evaluating the expression in javascript context.

-----

## *'evalInt|Long|Float|Double|Char|Boolean' methods*
####Signature
``` Java
Object eval[Type](String expression, Object callerInstance)
```
Returns the resulting value of corresponding type evaluating the expression in javascript context.

-----

## *'evalNoResult' method*
####Signature
``` Java
void evalNoResult(String expression, Object callerInstance)
```
Evaluates the expression in javascript context returning no value.


##**Examples**

 If we want to execute a JQuery selector to make desired elements draggable and resizable:
``` Java
ScriptHelper.evalNoResult("$('.draggable').draggable().resizable()", this);
```

----

Creating a variable with value 10, and getting this value plus 4:
``` Java
ScriptHelper.put("x", 10, this);
int newValue= ScriptHelper.evalInt("x + 4", this);
```

----

LocalStorage implementation
``` Java
public class LocalStorage
{
	public <T> T load(String aKey)
	{
		ScriptHelper.put("key", aKey, this);
		String serializedValue= (String) ScriptHelper.eval("localStorage.getItem(key)", this);
		if (serializedValue != null && !serializedValue.isEmpty())
			return (T) ServiceLocator.getInstance().getSerializationService().deserialize(serializedValue);
		else
			return null;
	}

	public <T> void save(String aKey, T aValue)
	{
		String serializedValue= ServiceLocator.getInstance().getSerializationService().serialize(aValue);
		ScriptHelper.put("key", aKey, this);
		ScriptHelper.put("serialized", serializedValue, this);
		ScriptHelper.eval("localStorage.setItem(key, serialized)", this);
	}
}
```

