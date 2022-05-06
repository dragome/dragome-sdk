package flexjson.factories;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.Map;

import flexjson.JSONException;
import flexjson.ObjectBinder;
import flexjson.ObjectFactory;

public class BeanObjectFactory implements ObjectFactory
{
	public Object instantiate(ObjectBinder context, Object value, Type targetType, Class targetClass)
	{
		try
		{
			if (value instanceof String)
				return value;

			if (value instanceof Map)
			{
				Map map= (Map) value;
				Integer reference= (Integer) map.get("@ref");
				if (reference != null)
				{
					Object object= context.getReferences().get(reference);
					if (map.size() > 1)
						context.bindIntoObject(map, object, targetType);
					return object;
				}
				else
				{
					Object target= instantiate(targetClass);
					Integer id= (Integer) map.get("@id");
					if (id != null)
						context.getReferences().put(id, target);

					return context.bindIntoObject(map, target, targetType);
				}
			}
			else
				return context.bind(value, value.getClass());
		}
		catch (InstantiationException e)
		{
			throw new JSONException(context.getCurrentPath() + ":There was an exception trying to instantiate an instance of " + targetClass.getName(), e);
		}
		catch (IllegalAccessException e)
		{
			throw new JSONException(context.getCurrentPath() + ":There was an exception trying to instantiate an instance of " + targetClass.getName(), e);
		}
		catch (InvocationTargetException e)
		{
			throw new JSONException(context.getCurrentPath() + ":There was an exception trying to instantiate an instance of " + targetClass.getName(), e);
		}
		catch (NoSuchMethodException e)
		{
			throw new JSONException(context.getCurrentPath() + ": " + targetClass.getName() + " lacks a no argument constructor.  Flexjson will instantiate any protected, private, or public no-arg constructor.", e);
		}
	}

	protected Object instantiate(Class clazz) throws IllegalAccessException, InvocationTargetException, InstantiationException, NoSuchMethodException
	{
		Constructor constructor= clazz.getDeclaredConstructor();
		constructor.setAccessible(true);
		return constructor.newInstance();
	}
}
