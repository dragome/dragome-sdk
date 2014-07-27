package flexjson.factories;

import java.lang.reflect.Type;

import flexjson.ObjectBinder;
import flexjson.ObjectFactory;

public class SortedSetObjectFactory implements ObjectFactory
{
	public Object instantiate(ObjectBinder context, Object value, Type targetType, Class targetClass)
	{
		return targetClass;
		//	if (value instanceof Collection)
		//	{
		//	    return context.bindIntoCollection((Collection) value, new TreeSet(), targetType);
		//	}
		//	else
		//	{
		//	    TreeSet<Object> set= new TreeSet<Object>();
		//	    set.add(context.bind(value));
		//	    return set;
		//	}
	}
}
