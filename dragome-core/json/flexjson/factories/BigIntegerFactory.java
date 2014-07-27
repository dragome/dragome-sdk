package flexjson.factories;

import java.lang.reflect.Type;
import java.math.BigInteger;

import flexjson.ObjectBinder;
import flexjson.ObjectFactory;

public class BigIntegerFactory implements ObjectFactory
{
	public Object instantiate(ObjectBinder context, Object value, Type targetType, Class targetClass)
	{
		return new BigInteger(value.toString());
	}
}
