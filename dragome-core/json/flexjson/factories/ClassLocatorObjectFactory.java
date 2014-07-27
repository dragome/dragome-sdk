package flexjson.factories;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;

import flexjson.ClassLocator;
import flexjson.JSONException;
import flexjson.ObjectBinder;
import flexjson.ObjectFactory;

public class ClassLocatorObjectFactory implements ObjectFactory
{

	private ClassLocator locator;

	public ClassLocatorObjectFactory(ClassLocator locator)
	{
		this.locator= locator;
	}

	public Object instantiate(ObjectBinder context, Object value, Type targetType, Class targetClass)
	{
		Class clazz= null;
		try
		{
			clazz= locator.locate(context, context.getCurrentPath());
			if (clazz != null)
			{
				if (Collection.class.isAssignableFrom(clazz))
				{
					return context.bindIntoCollection((Collection) value, (Collection<Object>) createTargetObject(clazz), targetType);
				}
				else if (Map.class.isAssignableFrom(clazz))
				{
					if (targetType instanceof ParameterizedType)
					{
						ParameterizedType ptype= (ParameterizedType) targetType;
						return context.bindIntoMap((Map) value, (Map<Object, Object>) createTargetObject(clazz), ptype.getActualTypeArguments()[0], ptype.getActualTypeArguments()[1]);
					}
					else
					{
						return context.bindIntoMap((Map) value, (Map<Object, Object>) createTargetObject(clazz), null, null);
					}
				}
				else if (value instanceof Map)
				{
					return context.bindIntoObject((Map) value, createTargetObject(clazz), clazz);
				}
				else
				{
					return context.bindPrimitive(value, clazz);
				}
			}
			else
			{
				return null;
			}
		}
		catch (ClassNotFoundException ex)
		{
			throw new JSONException(String.format("%s: Could not find class %s", context.getCurrentPath(), ex.getMessage()), ex);
		}
		catch (IllegalAccessException e)
		{
			throw new JSONException(String.format("%s: Could not instantiate class %s", context.getCurrentPath(), clazz.getName()), e);
		}
		catch (InstantiationException e)
		{
			throw new JSONException(String.format("%s: Problem while instantiating class %s", context.getCurrentPath(), clazz.getName()), e);
		}
		catch (NoSuchMethodException e)
		{
			throw new JSONException(String.format("%s: Could not find a no-arg constructor for %s", context.getCurrentPath(), clazz.getName()), e);
		}
		catch (InvocationTargetException e)
		{
			throw new JSONException(String.format("%s: Problem while invoking the no-arg constructor for %s", context.getCurrentPath(), clazz.getName()), e);
		}
	}

	private Object createTargetObject(Class clazz) throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException
	{
		Constructor constructor= clazz.getDeclaredConstructor();
		constructor.setAccessible(true);
		Object target= constructor.newInstance();
		return target;
	}

	public ClassLocator getLocator()
	{
		return locator;
	}
}
