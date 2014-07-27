package flexjson.factories;

import java.lang.reflect.Type;

import flexjson.ObjectBinder;
import flexjson.ObjectFactory;

public class ShortObjectFactory implements ObjectFactory
{
	public Object instantiate(ObjectBinder context, Object value, Type targetType, Class targetClass)
	{
		if (value instanceof Number)
		{
			return ((Number) value).shortValue();
		}
		else
		{
			try
			{
				return Short.parseShort(value.toString());
			}
			catch (Exception e)
			{
				throw context.cannotConvertValueToTargetType(value, Short.class);
			}
		}
	}
}
