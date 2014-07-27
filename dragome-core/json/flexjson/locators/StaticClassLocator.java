package flexjson.locators;

import flexjson.ClassLocator;
import flexjson.ObjectBinder;
import flexjson.Path;

/**
 * Simple implementation for translating an object path to a single class.
 * Normally you would not use this class directly and use the
 * {@link flexjson.JSONDeserializer#use(String, Class)} method
 * instead.
 */
public class StaticClassLocator implements ClassLocator
{
	private Class target;

	public StaticClassLocator(Class clazz)
	{
		target= clazz;
	}

	public Class locate(ObjectBinder context, Path currentPath)
	{
		return target;
	}
}
