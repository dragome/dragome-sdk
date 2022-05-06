package flexjson.factories;

import java.lang.reflect.Type;

import flexjson.ObjectBinder;
import flexjson.ObjectFactory;

public class ClassObjectFactory implements ObjectFactory
{
	public Object instantiate(ObjectBinder context, Object value, Type targetType, Class targetClass)
	{
		return parseType((String) value);
	}

	public static Class<?> parseType(final String className)
	{
		switch (className)
		{
			case "boolean":
				return boolean.class;
			case "byte":
				return byte.class;
			case "short":
				return short.class;
			case "int":
				return int.class;
			case "long":
				return long.class;
			case "float":
				return float.class;
			case "double":
				return double.class;
			case "char":
				return char.class;
			case "void":
				return void.class;
			default:
				String fqn= className.contains(".") ? className : "java.lang.".concat(className);
				try
				{
					return Class.forName(fqn);
				}
				catch (ClassNotFoundException ex)
				{
					throw new IllegalArgumentException("Class not found: " + fqn);
				}
		}
	}

}
