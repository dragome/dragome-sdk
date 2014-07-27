package flexjson.factories;

import java.lang.reflect.Type;
import java.util.Map;

import flexjson.ObjectBinder;
import flexjson.ObjectFactory;

public class ExistingObjectFactory implements ObjectFactory
{

	private Object source;

	public ExistingObjectFactory(Object source)
	{
		this.source= source;
	}

	public Object instantiate(ObjectBinder context, Object value, Type targetType, Class targetClass)
	{
		return context.bindIntoObject((Map) value, source, targetType);
	}
}
