package com.dragome.helpers;

import static com.dragome.helpers.ReflectionHelper.getPropertyName;

import java.lang.reflect.Method;

public class BeanProperty
{
	private Method method;
	private Object entity;

	public BeanProperty()
	{
	}

	public BeanProperty(Method method, Object entity)
	{
		this.method= method;
		this.entity= entity;
	}

	public Object getValue()
	{
		return ReflectionHelper.getPropertyValue(entity, getPropertyName(method));
	}

	public Object setValue(Object value)
	{
		try
		{
			ReflectionHelper.setPropertyValue(entity, getPropertyName(method), value);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return value;
	}

	public String getName()
	{
		return getPropertyName(method);
	}
}