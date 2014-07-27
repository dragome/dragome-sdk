package flexjson.factories;

import java.lang.reflect.Type;

import flexjson.ObjectBinder;
import flexjson.ObjectFactory;

public class LongObjectFactory implements ObjectFactory
{
	public Object instantiate(ObjectBinder context, Object value, Type targetType, Class targetClass)
	{
		if (value instanceof Number)
		{
			return ((Number) value).longValue();
		}
		else
		{
			try
			{
				return Long.parseLong(value.toString());
			}
			catch (Exception e)
			{
				throw context.cannotConvertValueToTargetType(value, Long.class);
			}
		}
	}
}
