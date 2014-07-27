package flexjson;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class BeanAnalyzer
{

	private static ThreadLocal<Map<Class, BeanAnalyzer>> cache= new ThreadLocal<Map<Class, BeanAnalyzer>>();

	private Class clazz2;
	private BeanAnalyzer superBean;
	private Map<String, BeanProperty> properties;

	public static BeanAnalyzer analyze(Class clazz2)
	{
		if (cache.get() == null)
			cache.set(new HashMap<Class, BeanAnalyzer>());
		if (clazz2 == null)
			return null;
		if (!cache.get().containsKey(clazz2))
		{
			cache.get().put(clazz2, new BeanAnalyzer(clazz2));
		}
		return cache.get().get(clazz2);
	}

	public static void clearCache()
	{
		cache.remove();
	}

	protected BeanAnalyzer(Class clazz)
	{
		this.clazz2= clazz;
		superBean= analyze(clazz.getSuperclass());
		populateProperties();
	}

	private void populateProperties()
	{
		properties= new HashMap<String, BeanProperty>();
		for (Method method : clazz2.getDeclaredMethods())
		{
			int modifiers= method.getModifiers();
			if (Modifier.isStatic(modifiers))
				continue;

			int numberOfArgs= method.getParameterTypes().length;
			String name= method.getName();
			if (name.length() <= 3 && !name.startsWith("is"))
				continue;

			if (numberOfArgs == 0)
			{
				if (name.startsWith("get"))
				{
					String property= uncapitalize(name.substring(3));
					if (!properties.containsKey(property))
					{
						properties.put(property, new BeanProperty(property, this));
					}
					properties.get(property).setReadMethod(method);
				}
				else if (name.startsWith("is"))
				{
					String property= uncapitalize(name.substring(2));
					if (!properties.containsKey(property))
					{
						properties.put(property, new BeanProperty(property, this));
					}
					properties.get(property).setReadMethod(method);
				}
			}
			else if (numberOfArgs == 1)
			{
				if (name.startsWith("set"))
				{
					String property= uncapitalize(name.substring(3));
					if (!properties.containsKey(property))
					{
						properties.put(property, new BeanProperty(property, this));
					}
					properties.get(property).addWriteMethod(method);
				}
			}
		}

		for (Field publicProperties : clazz2.getFields())
		{
			int modifiers= publicProperties.getModifiers();
			if (Modifier.isStatic(modifiers) || Modifier.isTransient(modifiers))
				continue;
			if (!properties.containsKey(publicProperties.getName()))
			{
				properties.put(publicProperties.getName(), new BeanProperty(publicProperties, this));
			}
		}
	}

	public BeanAnalyzer getSuperBean()
	{
		return superBean;
	}

	private String uncapitalize(String value)
	{
		if (value.length() < 2)
		{
			return value.toLowerCase();
		}
		else if (Character.isUpperCase(value.charAt(0)) && Character.isUpperCase(value.charAt(1)))
		{
			return value;
		}
		else
		{
			return Character.toLowerCase(value.charAt(0)) + value.substring(1);
		}
	}

	public BeanProperty getProperty(String name)
	{
		BeanAnalyzer current= this;
		while (current != null)
		{
			BeanProperty property= current.properties.get(name);
			if (property != null)
				return property;
			current= current.superBean;
		}
		return null;
	}

	public Collection<BeanProperty> getProperties()
	{
		Map<String, BeanProperty> properties= new HashMap<String, BeanProperty>(this.properties);
		BeanAnalyzer current= this.superBean;
		while (current != null)
		{
			merge(properties, current.properties);
			current= current.superBean;
		}
		return properties.values();
	}

	private void merge(Map<String, BeanProperty> destination, Map<String, BeanProperty> source)
	{
		for (String key : source.keySet())
		{
			if (!destination.containsKey(key))
			{
				destination.put(key, source.get(key));
			}
		}
	}

	public boolean hasProperty(String name)
	{
		return properties.containsKey(name);
	}

	protected Field getDeclaredField(String name)
	{
		try
		{
			return clazz2.getDeclaredField(name);
		}
		catch (NoSuchFieldException e)
		{
			// ignore field does not exist.
			return null;
		}
	}
}
