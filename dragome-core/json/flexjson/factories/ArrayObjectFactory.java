package flexjson.factories;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import flexjson.JSONException;
import flexjson.ObjectBinder;
import flexjson.ObjectFactory;
import flexjson.transformer.ClassTransformer;

public class ArrayObjectFactory implements ObjectFactory
{

	public Object instantiate(ObjectBinder context, Object value, Type targetType, Class targetClass)
	{
		List list;
		String type= null;
		if (value instanceof Map)
		{
			Map map= (Map) value;
			list= (List) map.get("items");
			type= (String) map.get("type");
		}
		else
			list= (List) value;

		context.getCurrentPath().enqueue("values");
		try
		{
			Class memberClass= targetClass.getComponentType() != null ? targetClass.getComponentType() : context.findClassAtPath(context.getCurrentPath());
			if (memberClass == null)
			{
				if (type != null)
					memberClass= ClassObjectFactory.parseType(type);
				else
					memberClass= Object.class;
				//				throw new JSONException("Missing concrete class for array.  You might require a use() method.");
			}
			Object array= Array.newInstance(memberClass, list.size());
			for (int i= 0; i < list.size(); i++)
			{
				Object v= context.bind(list.get(i), memberClass);
				Array.set(array, i, v);
			}
			return array;
		}
		catch (ClassNotFoundException ex)
		{
			throw new JSONException(String.format("%s: Could not find class %s", context.getCurrentPath(), ex.getMessage()), ex);
		}
		finally
		{
			context.getCurrentPath().pop();
		}
	}
}
