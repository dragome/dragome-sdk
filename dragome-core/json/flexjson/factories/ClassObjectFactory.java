package flexjson.factories;

import java.lang.reflect.Type;

import flexjson.ObjectBinder;
import flexjson.ObjectFactory;

public class ClassObjectFactory implements ObjectFactory
{
	public Object instantiate(ObjectBinder context, Object value, Type targetType, Class targetClass)
	{
		try
		{
			return Class.forName((String) value);
		}
		catch (ClassNotFoundException e)
		{
			throw new RuntimeException(e);
		}
	}

}
