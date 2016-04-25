package java.lang;

import com.dragome.commons.javascript.ScriptHelper;

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
		ScriptHelper.put("enumType", enumType, null);
		ScriptHelper.put("name", name, null);
		return (T) ScriptHelper.eval("enumType.$$$nativeClass.$$clinit_()[\"$$$\"+name]", null);
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

	public final Class<E> getDeclaringClass()
	{
		Class<?> clazz= getClass();
		Class<?> zuper= clazz.getSuperclass();
		return (zuper == Enum.class) ? (Class<E>) clazz : (Class<E>) zuper;
	}
}
