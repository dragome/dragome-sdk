package flexjson.factories;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import flexjson.JSONException;
import flexjson.ObjectBinder;
import flexjson.ObjectFactory;

public class DateObjectFactory implements ObjectFactory
{
	List<DateFormat> dateFormats;

	public DateObjectFactory()
	{
		dateFormats= new ArrayList<DateFormat>();
		dateFormats.add(new SimpleDateFormat("MM/dd/yy hh:mm:ss"));
		dateFormats.add(new SimpleDateFormat("MM/dd/yy"));
	}
 
	public DateObjectFactory(List<DateFormat> dateFormats)
	{
		this.dateFormats= dateFormats;
	}

	public Object instantiate(ObjectBinder context, Object value, Type targetType, Class targetClass)
	{
		try
		{
			if (value instanceof Double)
			{
				return instantiateDate((Class) targetType, ((Double) value).longValue(), context);
			}
			else if (value instanceof Long)
			{
				return instantiateDate((Class) targetType, (Long) value, context);
			}
			else
			{
				for (DateFormat format : dateFormats)
				{
					try
					{
						return format.parse(value.toString());
					}
					catch (ParseException e)
					{
						// try next format
					}
				}
				throw new JSONException(String.format("%s:  Parsing date %s was not recognized as a date format", context.getCurrentPath(), value));
			}
		}
		catch (IllegalAccessException e)
		{
			throw new JSONException(String.format("%s:  Error encountered trying to instantiate %s", context.getCurrentPath(), ((Class) targetType).getName()), e);
		}
		catch (InstantiationException e)
		{
			throw new JSONException(String.format("%s:  Error encountered trying to instantiate %s.  Make sure there is a public constructor that accepts a single Long.", context.getCurrentPath(), ((Class) targetType).getName()), e);
		}
		catch (InvocationTargetException e)
		{
			throw new JSONException(String.format("%s:  Error encountered trying to instantiate %s.  Make sure there is a public constructor that accepts a single Long.", context.getCurrentPath(), ((Class) targetType).getName()), e);
		}
	}

	private Date instantiateDate(Class targetType, Long value, ObjectBinder context) throws IllegalAccessException, InstantiationException, InvocationTargetException
	{
		try
		{
			Constructor constructor= targetType.getConstructor(Long.TYPE);
			return (Date) constructor.newInstance(value);
		}
		catch (NoSuchMethodException e)
		{
			Date d= (Date) targetType.newInstance();
			d.setTime(value);
			return d;
		}
	}
}
