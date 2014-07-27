package flexjson.locators;

import java.util.HashMap;
import java.util.Map;

import flexjson.ClassLocator;
import flexjson.JSONException;
import flexjson.ObjectBinder;
import flexjson.Path;

/**
 * This implementation uses a single field out of the object as the type discriminator.
 * Each unique value of the type field is mapped to a java class using the
 * {@link TypeLocator#add(Object, Class)} method.
 */
public class TypeLocator<T> implements ClassLocator
{

	private String fieldname;
	private Map<T, Class> types= new HashMap<T, Class>();

	public TypeLocator(String fieldname)
	{
		this.fieldname= fieldname;
	}

	public TypeLocator add(T value, Class type)
	{
		types.put(value, type);
		return this;
	}

	public Class locate(ObjectBinder context, Path currentPath) throws ClassNotFoundException
	{
		Object source= context.getSource();
		if (source instanceof Map)
		{
			Map map= (Map) source;
			return types.get(map.get(fieldname));
		}
		else
		{
			throw new JSONException(String.format("%s: Don't know how to locate types for source %s using fieldname %s.  TypeLocator requires the source object be a java.util.Map in order to work.", context.getCurrentPath(), source.getClass(), fieldname));
		}
	}
}
