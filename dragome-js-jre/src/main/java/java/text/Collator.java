package java.text;

import java.util.Comparator;

import com.dragome.commons.javascript.ScriptHelper;

public abstract class Collator implements Comparator<Object>
{

	protected Collator()
	{
	}

	// According the ECMAScript Language Specification, the method localeCompare must return
	// 0 or -0 when comparing two strings that are considered canonically equivalent by the Unicode standard.
	// This is not honored by Safari. As a workaround, we compute the offset to the expected 0 value. Calls
	// method String.compareTo are then corrected by this value.
	static int equalityOffset= ScriptHelper.evalInt("'a'.localeCompare('a')", null);

	public int compare(Object o1, Object o2)
	{
		return compare((String) o1, (String) o2);
	}

	public abstract int compare(String o1, String o2);

	/**
	 * Gets the Collator for the desired locale.
	 */
	public static Collator getInstance()
	{
		return new Collator()
		{
			public int compare(String s1, String s2)
			{
				ScriptHelper.put("s1", s1, this);
				ScriptHelper.put("s2", s2, this);
				return ScriptHelper.evalInt("s1.localeCompare(s2)", this) - equalityOffset;
			}
		};
	}

}
