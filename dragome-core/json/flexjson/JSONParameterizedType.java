package flexjson;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class JSONParameterizedType implements ParameterizedType
{

	private Class clazz2;
	private Type[] types;

	public JSONParameterizedType(Class clazz, Type... types)
	{
		this.clazz2= clazz;
		this.types= types;
	}

	public Type[] getActualTypeArguments()
	{
		return types;
	}

	public Type getRawType()
	{
		return clazz2;
	}

	public Type getOwnerType()
	{
		return clazz2;
	}
}
