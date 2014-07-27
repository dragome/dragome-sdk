package flexjson.factories;

import java.lang.reflect.Type;

import flexjson.ObjectBinder;
import flexjson.ObjectFactory;

public class CharacterObjectFactory implements ObjectFactory
{

	public Object instantiate(ObjectBinder context, Object value, Type targetType, Class targetClass)
	{
		return value.toString().charAt(0);
	}
}
