package com.dragome.forms.bindings.reflect;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;

import com.dragome.forms.bindings.client.bean.AbstractPropertyDescriptor;
import com.dragome.forms.bindings.client.bean.NotCollectionPropertyException;
import com.dragome.forms.bindings.client.bean.Path;
import com.dragome.forms.bindings.client.bean.PropertyDescriptor;
import com.dragome.forms.bindings.client.bean.ReadOnlyPropertyException;
import com.dragome.forms.bindings.client.bean.TargetBeanIsNullException;
import com.dragome.forms.bindings.client.bean.UnknownPropertyException;

/**
 * Created by IntelliJ IDEA.
 * User: andrew
 * Date: Jun 1, 2010
 * Time: 11:34:01 AM
 * To change this template use File | Settings | File Templates.
 */
class BeanDescriptor
{
	private Class beanClass;
	private HashMap<String, PropertyDescriptor> descriptors= new HashMap<String, PropertyDescriptor>();
	private HashMap<Class<?>, Class<?>> primitives= new HashMap<Class<?>, Class<?>>();

	public BeanDescriptor(Class beanClass)
	{
		this.beanClass= beanClass;
		primitives.put(boolean.class, Boolean.class);
		primitives.put(char.class, Character.class);
		primitives.put(byte.class, Byte.class);
		primitives.put(short.class, Short.class);
		primitives.put(int.class, Integer.class);
		primitives.put(long.class, Long.class);
		primitives.put(float.class, Float.class);
		primitives.put(double.class, Double.class);
	}

	public PropertyDescriptor getPropertyDescriptor(Path path)
	{
		PropertyDescriptor descriptor= descriptors.get(path.getPropertyName());

		if (descriptor == null)
		{
			ensurePropertyExists(path);
			descriptor= new PropertyDescriptorImpl(path, beanClass, isMutable(path));
			descriptors.put(path.getPropertyName(), descriptor);
		}

		return descriptor;
	}

	private boolean isMutable(Path path)
	{
		// the getter exists...
		getGetter(path);

		try
		{
			// oh so hacky...
			getSetter(path);
			return true;
		}
		catch (ReadOnlyPropertyException e)
		{
			return false;
		}
	}

	public Object readProperty(Object bean, Path path) throws UnknownPropertyException
	{
		if (bean == null)
		{
			return null;
		}

		try
		{
			return getGetter(path).invoke(bean);
		}
		catch (IllegalAccessException e)
		{
			throw new UnknownPropertyException(path);
		}
		catch (InvocationTargetException e)
		{
			throw new UnknownPropertyException(path);
		}
	}

	public void writeProperty(Object bean, Path path, Object value) throws UnknownPropertyException, ReadOnlyPropertyException, TargetBeanIsNullException
	{
		if (bean == null)
		{
			throw new TargetBeanIsNullException(path, beanClass);
		}

		Method setter= getSetter(path);

		try
		{
			setter.invoke(bean, value);
		}
		catch (IllegalAccessException e)
		{
			throw new UnknownPropertyException(path);
		}
		catch (InvocationTargetException e)
		{
			throw new UnknownPropertyException(path);
		}
	}

	private void ensurePropertyExists(Path path)
	{
		// oh so hacky...
		getGetter(path);
	}

	private Method findMethod(String methodName, Class... parameterTypes) throws NoSuchMethodException
	{
		Class theClass= this.beanClass;
		while (true)
		{
			//System.out.println("hola mundo");
			int a= 1;
			try
			{
				return theClass.getMethod(methodName, parameterTypes);
			}
			catch (NoSuchMethodException e)
			{
				theClass= theClass.getSuperclass();
				if (theClass == null)
				{
					throw new NoSuchMethodException();
				}
			}
		}
	}

	private Method getGetter(Path path) throws UnknownPropertyException
	{
		try
		{
			return findMethod("get" + capitalise(path.getPropertyName()));
		}
		catch (NoSuchMethodException e)
		{
			try
			{
				// should really test it's return type is boolean...
				return findMethod("is" + capitalise(path.getPropertyName()));
			}
			catch (NoSuchMethodException e1)
			{
				throw new UnknownPropertyException(path);
			}
		}
	}

	private Method getSetter(Path path) throws ReadOnlyPropertyException
	{
		try
		{
			return beanClass.getMethod("set" + capitalise(path.getPropertyName()), getRawPropertyType(path));
		}
		catch (NoSuchMethodException e)
		{
			throw new ReadOnlyPropertyException(path);
		}
	}

	public Class getPropertyType(Path path)
	{
		Class<?> type= getRawPropertyType(path);
		return type.isPrimitive() ? primitives.get(type) : type;
	}

	private Class<?> getRawPropertyType(Path path)
	{
		return getGetter(path).getReturnType();
	}

	public Class getElementType(Path path)
	{
		Method method= getGetter(path);
		Class<?> type= method.getReturnType();

		if (!Collection.class.isAssignableFrom(type))
		{
			throw new NotCollectionPropertyException(path, type);
		}

		Type returnType= method.getGenericReturnType();

		if (returnType instanceof ParameterizedType)
		{
			Type[] typeArgs= ((ParameterizedType) returnType).getActualTypeArguments();
			return typeArgs.length == 1 ? (Class) typeArgs[0] : Object.class;
		}
		else
		{
			return Object.class;
		}
	}

	private String capitalise(String property)
	{
		return property.substring(0, 1).toUpperCase() + property.substring(1);
	}

	private class PropertyDescriptorImpl extends AbstractPropertyDescriptor
	{
		public PropertyDescriptorImpl(Path path, Class beanClass, boolean mutable)
		{
			super(path.getFullPath(), path.getParentPath(), path.getPropertyName(), beanClass, mutable);
		}

		public Class getValueType()
		{
			return getPropertyType(this);
		}

		public Class getElementType() throws NotCollectionPropertyException
		{
			return BeanDescriptor.this.getElementType(this);
		}

		public boolean isCollection()
		{
			return Collection.class.isAssignableFrom(getValueType());
		}

		public Object readProperty(Object bean)
		{
			return BeanDescriptor.this.readProperty(bean, this);
		}

		public void writeProperty(Object bean, Object value) throws ReadOnlyPropertyException, TargetBeanIsNullException
		{
			BeanDescriptor.this.writeProperty(bean, this, value);
		}
	}
}
