package flexjson;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import flexjson.factories.ArrayObjectFactory;
import flexjson.factories.BeanObjectFactory;
import flexjson.factories.BooleanObjectFactory;
import flexjson.factories.ByteObjectFactory;
import flexjson.factories.CharacterObjectFactory;
import flexjson.factories.ClassLocatorObjectFactory;
import flexjson.factories.ClassObjectFactory;
import flexjson.factories.DoubleObjectFactory;
import flexjson.factories.EnumObjectFactory;
import flexjson.factories.FloatObjectFactory;
import flexjson.factories.IntegerObjectFactory;
import flexjson.factories.ListObjectFactory;
import flexjson.factories.LongObjectFactory;
import flexjson.factories.MapObjectFactory;
import flexjson.factories.SetObjectFactory;
import flexjson.factories.ShortObjectFactory;
import flexjson.factories.StringObjectFactory;

public class ObjectBinder
{

	private Stack<Object> objectStack= new Stack<Object>();
	private Stack<Object> jsonStack= new Stack<Object>();
	private Path currentPath= new Path();
	private Map<Object, ObjectFactory> factories;
	private Map<Path, ObjectFactory> pathFactories= new HashMap<Path, ObjectFactory>();
	public Map<Integer, Object> references= new HashMap<Integer, Object>();

	public Map<Integer, Object> getReferences()
	{
		return references;
	}

	public ObjectBinder()
	{
		factories= new HashMap<Object, ObjectFactory>();
		factories.put(Class.class, new ClassObjectFactory());
		factories.put(Object.class, new BeanObjectFactory());
		factories.put(Collection.class, new ListObjectFactory());
		factories.put(List.class, new ListObjectFactory());
		factories.put(Set.class, new SetObjectFactory());
		//        factories.put( SortedSet.class, new SortedSetObjectFactory() );
		factories.put(Map.class, new MapObjectFactory());
		factories.put(Integer.class, new IntegerObjectFactory());
		factories.put(int.class, new IntegerObjectFactory());
		factories.put(Float.class, new FloatObjectFactory());
		factories.put(float.class, new FloatObjectFactory());
		factories.put(Double.class, new DoubleObjectFactory());
		factories.put(double.class, new DoubleObjectFactory());
		factories.put(Short.class, new ShortObjectFactory());
		factories.put(short.class, new ShortObjectFactory());
		factories.put(Long.class, new LongObjectFactory());
		factories.put(long.class, new LongObjectFactory());
		factories.put(Byte.class, new ByteObjectFactory());
		factories.put(byte.class, new ByteObjectFactory());
		factories.put(Boolean.class, new BooleanObjectFactory());
		factories.put(boolean.class, new BooleanObjectFactory());
		factories.put(Character.class, new CharacterObjectFactory());
		factories.put(char.class, new CharacterObjectFactory());
		factories.put(Enum.class, new EnumObjectFactory());
		//        factories.put( Date.class, new DateObjectFactory() );
		factories.put(String.class, new StringObjectFactory());
		factories.put(Array.class, new ArrayObjectFactory());
		//        factories.put( BigDecimal.class, new BigDecimalFactory() );
		//        factories.put( BigInteger.class, new BigIntegerFactory() );
	}

	public ObjectBinder use(Path path, ObjectFactory factory)
	{
		pathFactories.put(path, factory);
		return this;
	}

	public ObjectBinder use(Object clazz, ObjectFactory factory)
	{
		factories.put(clazz, factory);
		return this;
	}

	public Path getCurrentPath()
	{
		return currentPath;
	}

	public Object bind(Object input)
	{
		return this.bind(input, null);
	}

	public Object bind(Object source, Object target)
	{
		if (target instanceof Map)
		{
			bindIntoMap((Map) source, (Map<Object, Object>) target, null, null);
		}
		else if (target instanceof Collection)
		{
			bindIntoCollection((Collection) source, (Collection<Object>) target, null);
		}
		else
		{
			bindIntoObject((Map) source, target, target.getClass());
		}
		return target;
	}

	public Object bind(Object input, Type targetType)
	{
		jsonStack.add(input);
		try
		{
			if (input == null)
				return null;
			Class targetClass= findClassName(input, getTargetClass(targetType));
			ObjectFactory factory= findFactoryFor(targetClass);
			if (factory == null)
				throw new JSONException(currentPath + ": + Could not find a suitable ObjectFactory for " + targetClass);
			return factory.instantiate(this, input, targetType, targetClass);
		}
		finally
		{
			jsonStack.pop();
		}
	}

	public <T extends Collection<Object>> T bindIntoCollection(Collection value, T target, Type targetType)
	{
		Type valueType= null;
		if (targetType instanceof ParameterizedType)
		{
			valueType= ((ParameterizedType) targetType).getActualTypeArguments()[0];
		}
		jsonStack.add(value);
		objectStack.add(target);
		getCurrentPath().enqueue("values");
		for (Object obj : value)
		{
			target.add(bind(obj, valueType));
		}
		getCurrentPath().pop();
		objectStack.pop();
		jsonStack.pop();
		return target;
	}

	public Object bindIntoMap(Map input, Map<Object, Object> result, Type keyType, Type valueType)
	{
		jsonStack.add(input);
		objectStack.add(result);
		for (Object inputKey : input.keySet())
		{
			currentPath.enqueue("keys");
			Object key= bind(inputKey, keyType);
			currentPath.pop();
			currentPath.enqueue("values");
			Object value= bind(input.get(inputKey), valueType);
			currentPath.pop();
			result.put(key, value);
		}
		objectStack.pop();
		jsonStack.pop();
		return result;
	}

	public Object bindIntoObject(Map jsonOwner, Object target, Type targetType)
	{
		try
		{
			objectStack.add(target);
			BeanAnalyzer analyzer= BeanAnalyzer.analyze(target.getClass());
			for (BeanProperty descriptor : analyzer.getProperties())
			{
				Object value= findFieldInJson(jsonOwner, descriptor);
				if (value != null)
				{
					currentPath.enqueue(descriptor.getName());
					Method setMethod= descriptor.getWriteMethod();
					if (setMethod != null)
					{
						Type[] types= setMethod.getGenericParameterTypes();
						if (types.length == 1)
						{
							Type paramType= types[0];
							setMethod.invoke(objectStack.peek(), bind(value, resolveParameterizedTypes(paramType, targetType)));
						}
						else
						{
							throw new JSONException(currentPath + ":  Expected a single parameter for method " + target.getClass().getName() + "." + setMethod.getName() + " but got " + types.length);
						}
					}
					else
					{
						Field field= descriptor.getProperty();
						if (field != null)
						{
							field.setAccessible(true);
							field.set(target, bind(value, field.getGenericType()));
						}
					}
					currentPath.pop();
				}
			}
			return objectStack.pop();
		}
		catch (IllegalAccessException e)
		{
			throw new JSONException(currentPath + ":  Could not access the no-arg constructor for " + target.getClass().getName(), e);
		}
		catch (InvocationTargetException ex)
		{
			throw new JSONException(currentPath + ":  Exception while trying to invoke setter method.", ex);
		}
	}

	public JSONException cannotConvertValueToTargetType(Object value, Class targetType)
	{
		return new JSONException(String.format("%s:  Can not convert %s into %s", currentPath, value.getClass().getName(), targetType.getName()));
	}

	private Class getTargetClass(Type targetType)
	{
		if (targetType == null)
		{
			return null;
		}
		else if (targetType instanceof Class)
		{
			return (Class) targetType;
		}
		else if (targetType instanceof ParameterizedType)
		{
			return (Class) ((ParameterizedType) targetType).getRawType();
		}
		else if (targetType instanceof GenericArrayType)
		{
			return Array.class;
		}
		else if (targetType instanceof WildcardType)
		{
			return null; // nothing you can do about these.  User will have to specify this with use()
		}
		else if (targetType instanceof TypeVariable)
		{
			return null; // nothing you can do about these.  User will have to specify this with use()
		}
		else
		{
			throw new JSONException(currentPath + ":  Unknown type " + targetType);
		}
	}

	private Type resolveParameterizedTypes(Type genericType, Type targetType)
	{
		if (genericType instanceof Class)
		{
			return genericType;
		}
		else if (genericType instanceof ParameterizedType)
		{
			return genericType;
		}
		else if (genericType instanceof TypeVariable)
		{
			return targetType;
		}
		else if (genericType instanceof WildcardType)
		{
			return targetType;
		}
		else if (genericType instanceof GenericArrayType)
		{
			return ((GenericArrayType) genericType).getGenericComponentType();
		}
		else
		{
			throw new JSONException(currentPath + ":  Unknown generic type " + genericType + ".");
		}
	}

	private Class findClassName(Object map, Class targetType) throws JSONException
	{
		if (!pathFactories.containsKey(currentPath))
		{
			Class mostSpecificType= useMostSpecific(targetType, map instanceof Map ? findClassInMap((Map) map, null) : null);
			if (mostSpecificType == null)
			{
				return map.getClass();
			}
			else
			{
				return mostSpecificType;
			}
		}
		else
		{
			return null;
		}
	}

	protected Class useMostSpecific(Class classFromTarget, Class typeFound)
	{
		if (classFromTarget != null && typeFound != null)
		{
			return typeFound.isAssignableFrom(classFromTarget) ? classFromTarget : typeFound;
		}
		else if (typeFound != null)
		{
			return typeFound;
		}
		else if (classFromTarget != null)
		{
			return classFromTarget;
		}
		else
		{
			return null;
		}
	}

	protected Class findClassInMap(Map map, Class override)
	{
		if (override == null)
		{
			Integer reference= (Integer) map.get("@ref");
			if (reference != null)
				return getReferences().get(reference).getClass();

			String classname= (String) map.remove("class");
			try
			{
				if (classname != null)
				{
					return Class.forName(classname);
				}
				else
				{
					return null;
				}
			}
			catch (ClassNotFoundException e)
			{
				throw new JSONException( /*String.format( "%s:  Could not load %s", currentPath, classname ), */e);
			}
		}
		else
		{
			return override;
		}
	}

	private ObjectFactory findFactoryFor(Class targetType)
	{
		ObjectFactory factory= pathFactories.get(currentPath);
		if (factory == null)
		{
			if (targetType != null && targetType.isArray())
				return factories.get(Array.class);
			return findFactoryByTargetClass(targetType);
		}
		return factory;
	}

	private ObjectFactory findFactoryByTargetClass(Class targetType)
	{
		ObjectFactory factory;
		factory= factories.get(targetType);
		if (factory == null && targetType != null && targetType.getSuperclass() != null)
		{
			for (Class intf : targetType.getInterfaces())
			{
				factory= findFactoryByTargetClass(intf);
				if (factory != null)
					return factory;
			}
			return findFactoryByTargetClass(targetType.getSuperclass());
		}
		else
		{
			return factory;
		}
	}

	protected Object instantiate(Class clazz)
	{
		try
		{
			Constructor constructor= clazz.getDeclaredConstructor();
			constructor.setAccessible(true);
			return constructor.newInstance();
		}
		catch (InstantiationException e)
		{
			throw new JSONException(currentPath + ":There was an exception trying to instantiate an instance of " + clazz.getName(), e);
		}
		catch (IllegalAccessException e)
		{
			throw new JSONException(currentPath + ":There was an exception trying to instantiate an instance of " + clazz.getName(), e);
		}
		catch (InvocationTargetException e)
		{
			throw new JSONException(currentPath + ":There was an exception trying to instantiate an instance of " + clazz.getName(), e);
		}
		catch (NoSuchMethodException e)
		{
			throw new JSONException(currentPath + ": " + clazz.getName() + " lacks a no argument constructor.  Flexjson will instantiate any protected, private, or public no-arg constructor.", e);
		}
	}

	private Object findFieldInJson(Map map, BeanProperty property)
	{
		Object value= map.get(property.getName());
		if (value == null)
		{
			String field= property.getName();
			value= map.get(upperCase(field));
		}

		return value;
	}

	private String upperCase(String field)
	{
		return Character.toUpperCase(field.charAt(0)) + field.substring(1);
	}

	public Object getTarget()
	{
		return objectStack.peek();
	}

	public Object getSource()
	{
		return jsonStack.peek();
	}

	public Object bindPrimitive(Object value, Class clazz)
	{
		if (value.getClass() == clazz)
		{
			return value;
		}
		else if (value instanceof Number && clazz.equals(Double.class))
		{
			return ((Number) value).doubleValue();
		}
		else if (value instanceof Number && clazz.equals(Integer.class))
		{
			return ((Number) value).intValue();
		}
		else if (value instanceof Number && clazz.equals(Long.class))
		{
			return ((Number) value).longValue();
		}
		else if (value instanceof Number && clazz.equals(Short.class))
		{
			return ((Number) value).shortValue();
		}
		else if (value instanceof Number && clazz.equals(Byte.class))
		{
			return ((Number) value).byteValue();
		}
		else if (value instanceof Number && clazz.equals(Float.class))
		{
			return ((Number) value).floatValue();
		}
		else if (value instanceof Boolean && clazz.equals(Boolean.class))
		{
			return value;
		}
		else if (value instanceof Long && clazz == Date.class)
		{
			return new Date((Long) value);
		}
		else
		{
			throw new JSONException(String.format("%s: Don't know how to bind %s into class %s.  You might need to use an ObjectFactory instead of a plain class.", getCurrentPath().toString(), value, clazz.getName()));
		}
	}

	public Class findClassAtPath(Path currentPath) throws ClassNotFoundException
	{
		ObjectFactory factory= pathFactories.get(currentPath);
		if (factory instanceof ClassLocatorObjectFactory)
		{
			return ((ClassLocatorObjectFactory) factory).getLocator().locate(this, currentPath);
		}
		else
		{
			return null;
		}
	}
}
