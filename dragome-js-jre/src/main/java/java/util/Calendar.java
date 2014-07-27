package java.util;

import com.dragome.commons.javascript.ScriptHelper;

public class Calendar
{

	//    static int  AM
	//    Value of the AM_PM field indicating the period of the day from midnight to just before noon.
	//static int  AM_PM
	//    Field number for get and set indicating whether the HOUR is before or after noon.
	//static int  APRIL
	//    Value of the MONTH field indicating the fourth month of the year.
	//static int  AUGUST
	//    Value of the MONTH field indicating the eighth month of the year.
	//static int  DATE
	//    Field number for get and set indicating the day of the month.

	/**
	 * Field number for get and set indicating the day of the month.
	 */
	public static int DAY_OF_MONTH= 5;

	/*
	 * Field number for get and set indicating the day of the week.
	 */
	public static final int DAY_OF_WEEK= 7;

	//static int  DECEMBER
	//    Value of the MONTH field indicating the twelfth month of the year.
	//static int  FEBRUARY
	//    Value of the MONTH field indicating the second month of the year.
	//protected  int[]    fields
	//    The field values for the currently set time for this calendar.
	//static int  FRIDAY
	//    Value of the DAY_OF_WEEK field indicating Friday.
	//static int  HOUR
	//    Field number for get and set indicating the hour of the morning or afternoon.

	/**
	 * Field number for get and set indicating the hour of the day.
	 */
	public static int HOUR_OF_DAY= 11;
	//    
	//protected  boolean[]    isSet
	//    The flags which tell if a specified time field for the calendar is set.
	//static int  JANUARY
	//    Value of the MONTH field indicating the first month of the year.
	//static int  JULY
	//    Value of the MONTH field indicating the seventh month of the year.
	//static int  JUNE
	//    Value of the MONTH field indicating the sixth month of the year.
	//static int  MARCH
	//    Value of the MONTH field indicating the third month of the year.
	//static int  MAY
	//    Value of the MONTH field indicating the fifth month of the year.
	//static int  MILLISECOND
	//    Field number for get and set indicating the millisecond within the second.

	/**
	 * Field number for get and set indicating the minute within the hour.
	 */
	public static int MINUTE= 12;

	//static int  MONDAY
	//    Value of the DAY_OF_WEEK field indicating Monday.

	/**
	 * Field number for get and set indicating the month.
	 */
	public static int MONTH= 2;

	//static int  NOVEMBER
	//    Value of the MONTH field indicating the eleventh month of the year.
	//static int  OCTOBER
	//    Value of the MONTH field indicating the tenth month of the year.
	//static int  PM
	//    Value of the AM_PM field indicating the period of the day from noon to just before midnight.
	//static int  SATURDAY
	//    Value of the DAY_OF_WEEK field indicating Saturday.
	public static int SECOND= 13;
	//    Field number for get and set indicating the second within the minute.
	//static int  SEPTEMBER
	//    Value of the MONTH field indicating the ninth month of the year.
	//static int  SUNDAY
	//    Value of the DAY_OF_WEEK field indicating Sunday.
	//static int  THURSDAY
	//    Value of the DAY_OF_WEEK field indicating Thursday.
	//protected  long     time
	//    The currently set time for this calendar, expressed in milliseconds after January 1, 1970, 0:00:00 GMT.
	//static int  TUESDAY
	//    Value of the DAY_OF_WEEK field indicating Tuesday.
	//static int  WEDNESDAY
	//    Value of the DAY_OF_WEEK field indicating Wednesday.
	/**
	 * Field number for get and set indicating the year.
	 */
	public static int YEAR= 1;

	/**
	 * Field number for get and set indicating the raw offset from GMT in milliseconds.
	 */
	public static int ZONE_OFFSET= 15;

	/*    public static final int     AM  0
	    public static final int     AM_PM   9
	    public static final int     APRIL   3
	    public static final int     AUGUST  7
	    public static final int     DATE    5
	    public static final int     DAY_OF_MONTH    5
	    public static final int     DAY_OF_WEEK     7
	    public static final int     DECEMBER    11
	    public static final int     FEBRUARY    1
	    public static final int     FRIDAY  6
	    public static final int     HOUR    10
	    public static final int     HOUR_OF_DAY     11
	    public static final int     JANUARY     0
	    public static final int     JULY    6
	    public static final int     JUNE    5
	    public static final int     MARCH   2
	    public static final int     MAY     4
	    public static final int     MILLISECOND     14
	    public static final int     MINUTE  12
	    public static final int     MONDAY  2
	    public static final int     MONTH   2
	    public static final int     NOVEMBER    10
	    public static final int     OCTOBER     9
	    public static final int     PM  1
	    public static final int     SATURDAY    7
	    public static final int     SECOND  13
	    public static final int     SEPTEMBER   8
	    public static final int     SUNDAY  1
	    public static final int     THURSDAY    5
	    public static final int     TUESDAY     3
	    public static final int     WEDNESDAY   4
	    public static final int     YEAR    1
	*/
	private Date date;
	private TimeZone timeZone;
	private String zID;

	public Calendar()
	{
		date= new Date();
	}

	public final Date getTime()
	{
		return date;
	}

	public final void setTime(Date theDate)
	{
		date= theDate;
	}

	public static Calendar getInstance()
	{
		return getInstance(new TimeZone());
	}

	public static Calendar getInstance(TimeZone zone)
	{
		Calendar cal= new Calendar();
		//	cal.setTimeZone(zone);   //TODO: compilador
		return cal;
	}

	protected long getTimeInMillis()
	{
		return date.getTime();
	}

	protected void setTimeInMillis(long millis)
	{
		date.setTime(millis);
	}

	public final int get(int field)
	{

		String functionName= zID;

		if (field == MONTH)
		{
			functionName+= "Month";
		}
		else if (field == DAY_OF_WEEK)
		{
			functionName+= "Day";
		}
		else if (field == DAY_OF_MONTH)
		{
			functionName+= "Date";
		}
		else if (field == YEAR)
		{
			functionName+= "FullYear";
		}
		else if (field == HOUR_OF_DAY)
		{
			functionName+= "Hours";
		}
		else if (field == MINUTE)
		{
			functionName+= "Minutes";
		}
		else if (field == SECOND)
		{
			functionName+= "Seconds";
		}
		else if (field == ZONE_OFFSET)
		{
			functionName= "TimezoneOffset";
		}
		else
			throw new IllegalArgumentException("Field number not allowed: " + field);

		ScriptHelper.put("functionName", "get" + functionName, this);
		int value= ScriptHelper.evalInt("this.date.nativeDate[functionName]()", this);

		if (field == DAY_OF_WEEK)
		{
			/*
			 * Day    Java JavaScript
			 * Sunday   1     0
			 * Monday   2     1
			 * Saturday 7     6
			 */
			// Map JavaScript to Java
			value= value + 1;
		}

		return value;
	}

	public final void set(int field, int value)
	{
		String functionName;

		if (field == MONTH)
		{
			functionName= zID + "Month";
		}
		else if (field == YEAR)
		{
			functionName= zID + "Year";
		}
		else if (field == DAY_OF_WEEK)
		{
			// Map Java to JavaScript
			value= (value - 1 + 7) % 7;
			functionName= zID + "Day";
		}
		else
			throw new IllegalArgumentException("Field number not allowed: " + field);

		ScriptHelper.put("value", value, this);
		ScriptHelper.put("functionName", "set" + functionName, this);
		ScriptHelper.eval("this.date.nativeDate[functionName](value)", this);
	}

	public boolean equals(Object obj)
	{
		return date.equals(((Calendar) obj).date);
	}

	public boolean before(Object when)
	{
		return getTimeInMillis() < ((Calendar) when).getTimeInMillis();
	}

	public boolean after(Object when)
	{
		return getTimeInMillis() > ((Calendar) when).getTimeInMillis();
	}

	public void setTimeZone(TimeZone zone)
	{
		timeZone= zone;
		zID= "";
		if (timeZone.getID().equals("GMT"))
		{
			zID= "UTC";
		}
	}

	public TimeZone getTimeZone()
	{
		return timeZone;
	}

	//protected abstract void computeFields();

	//protected abstract void computeTime();
}
