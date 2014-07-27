package java.lang;

/**
 * This is the common base class of all Java language enumeration types.
 */
public abstract class Enum<E>
{

	private String desc;
	private int ordinal;

	/**
	 * Sole constructor.
	 */
	protected Enum(String theDesc, int theOrdinal)
	{
		desc= theDesc;
		ordinal= theOrdinal;
	}

	/**
	 * Returns the enum constant of the specified enum type with the specified name.
	 *
	 * Note: This method (signature only) is required by the JDK compiler!
	 */
	public static <T extends Enum<T>> T valueOf(Class<T> enumType, String name)
	{
		throw new UnsupportedOperationException();
	}

	/**
	 * Returns the ordinal of this enumeration constant.
	 */
	public int ordinal()
	{
		return ordinal;
	}

	/**
	 * Returns the name of this enum constant.
	 */
	public String toString()
	{
		return desc;
	}

	public String name()
	{
		//TODO revisar
		return null;
	}

}
