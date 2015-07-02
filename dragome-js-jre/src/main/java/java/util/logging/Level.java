package java.util.logging;

public class Level
{

	/*
	 * Must be declared before any static level declaration!
	 */
	private static Level[] levels= new Level[4];
	private static int count= 0;

	/**
	 * ALL indicates that all messages should be logged. This level is initialized to Integer.MIN_VALUE.
	 */
	public static final Level ALL= new Level("ALL", Integer.MIN_VALUE);

	/**
	 * FINE is a message level providing tracing information. This level is initialized to 500.
	 */
	public static final Level FINER= new Level("FINER", 500);

	/**
	 * INFO is a message level for informational messages. This level is initialized to 800.
	 */
	public static final Level INFO= new Level("INFO", 800);

	/**
	 * WARNING is a message level indicating a potential problem. This level is initialized to 900.
	 */
	public static final Level WARNING= new Level("WARNING", 900);

	/**
	 * OFF is a special level that can be used to turn off logging. This level is initialized to Integer.MAX_VALUE.
	 */
	public static final Level OFF= new Level("OFF", Integer.MAX_VALUE);
	
    public static final Level FINEST = new Level("FINEST", 300);
    
    public static final Level SEVERE = new Level("SEVERE",1000);    

	

	private int value;
	private String name;

	/**
	 * Parse a level name string into a Level.
	 * The argument string may consist of either a level name or an integer value. 
	 */
	public static Level parse(String name) throws IllegalArgumentException
	{
		for (Level level : levels)
		{
			if (level.matches(name))
			{
				return level;
			}
		}
		throw new IllegalArgumentException("Invalid level: " + name);
	}

	/**
	 * Create a named Level with a given integer value.
	 */
	protected Level(String name, int value)
	{
		this.name= name;
		this.value= value;
		levels[count++]= this;
	}

	/**
	 * Returns true if the argument string either matches the level name or the level value. 
	 */
	private boolean matches(String pattern)
	{
		return pattern.equals(name) || pattern.equals(String.valueOf(value));
	}

	/**
	 * Return the non-localized string name of the Level.
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Get the integer value for this level.
	 */
	public final int intValue()
	{
		return value;
	}

}
