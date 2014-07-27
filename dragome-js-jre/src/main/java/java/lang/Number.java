package java.lang;

public abstract class Number
{

	/**
	 * Returns the value of the specified number as a byte.
	 */
	public byte byteValue()
	{
		return (byte) intValue();
	}

	/**
	 * Returns the value of the specified number as a double.
	 */
	public abstract double doubleValue();

	/**
	 * Returns the value of the specified number as a float.
	 */
	public abstract float floatValue();

	/**
	 * Returns the value of the specified number as an int. 
	 */
	public abstract int intValue();

	/**
	 * Returns the value of the specified number as a long.
	 */
	public abstract long longValue();

	/**
	 * Returns the value of the specified number as a short.
	 */
	public short shortValue()
	{
		return (short) intValue();
	}

}
