package flexjson.factories;

import java.lang.reflect.Type;

import flexjson.ObjectBinder;
import flexjson.ObjectFactory;

public class ByteObjectFactory implements ObjectFactory
{

	public Object instantiate(ObjectBinder context, Object value, Type targetType, Class targetClass)
	{
		if (value instanceof Number)
		{
			return ((Number) value).byteValue();
		}
		else
		{
			throw context.cannotConvertValueToTargetType(value, Byte.class);
		}
	}
}
