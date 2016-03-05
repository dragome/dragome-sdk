package java.util;

import com.dragome.commons.javascript.ScriptHelper;

public class Date
{

	private Object nativeDate;

	public Date()
	{
		ScriptHelper.eval("this.nativeDate = new Date()", this);
	}

	public Date(long millis)
	{
		ScriptHelper.put("millis", millis, this);
		ScriptHelper.eval("this.nativeDate = new Date(millis)", this);
	}

	public long getTime()
	{
		return ScriptHelper.evalLong("this.nativeDate.getTime()", this);
	}

	public void setTime(long millis)
	{
		ScriptHelper.put("millis", millis, this);
		ScriptHelper.eval("this.nativeDate.setTime(millis)", this);
	}

	public boolean equals(Object obj)
	{
		Date other= (Date) obj;
		return getTime() == other.getTime();
	}

	public int hashCode()
	{
		long time= getTime();
		return (int) (time ^ (time >>> 32));
	}

	public String toString()
	{
		return (String) ScriptHelper.eval("this.nativeDate.toGMTString()", this);
	}
}
