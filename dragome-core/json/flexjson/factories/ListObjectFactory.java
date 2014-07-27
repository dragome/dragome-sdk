package flexjson.factories;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;

import flexjson.ObjectBinder;
import flexjson.ObjectFactory;

public class ListObjectFactory implements ObjectFactory
{
	public Object instantiate(ObjectBinder context, Object value, Type targetType, Class targetClass)
	{
		if (value instanceof Collection)
		{
			return context.bindIntoCollection((Collection) value, new ArrayList(), targetType);
		}
		else
		{
			ArrayList<Object> set= new ArrayList<Object>();
			set.add(context.bind(value));
			return set;
		}
	}
}
