package java.util;

import com.dragome.commons.javascript.ScriptHelper;

public final class TimeZone
{

	private static String GMT= "GMT";
	private static String LOCAL_ZONE= "LOCAL_ZONE";
	private String id;

	public TimeZone()
	{
		id= LOCAL_ZONE;
	}

	private TimeZone(String theId)
	{
		id= theId;
	}

	public int getOffset(int l1, int l2, int l3, int l4, int l5, int l6)
	{
		throw new UnsupportedOperationException("");
	}

	public int getRawOffset()
	{
		if (id.equals(GMT))
			return 0;
		return ScriptHelper.evalInt("new Date().getTimezoneOffset() / 60", this);
	}

	public boolean useDaylightTime()
	{
		throw new UnsupportedOperationException();
	}

	public String getID()
	{
		return id;
	}

	public static TimeZone getTimeZone(String id)
	{
		if (!id.equals(GMT) && !id.equals(LOCAL_ZONE))
		{
			id= GMT;
		}
		return new TimeZone(id);
	}

	public static TimeZone getDefault()
	{
		return new TimeZone();
	}

	public static String[] getAvailableIDs()
	{
		return new String[] { GMT, LOCAL_ZONE };
	}
}
