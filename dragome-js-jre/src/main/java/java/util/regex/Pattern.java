package java.util.regex;

import com.dragome.commons.javascript.ScriptHelper;

public final class Pattern
{

	public static final String CASE_INSENSITIVE = null;
	private String regex;

	/**
	 * Compiles the given regular expression into a pattern.
	 * @param regex The expression to be compiled
	 */
	public static Pattern compile(String regex)
	{
		Pattern pattern= new Pattern();
		pattern.regex= regex;
		return pattern;
	}

	/**
	 * Creates a matcher that will match the given input against this pattern.
	 */
	public Matcher matcher(CharSequence input)
	{
		regex= regex.replace("*+", "+");
		return new Matcher(ScriptHelper.eval("new RegExp(this.$$$regex___java_lang_String, 'g')", this), input);
	}

	public static Pattern compile(String pattern, String caseInsensitive) {
		// TODO Auto-generated method stub
		return null;
	}

	public static boolean matches(String string, String line) {
		// TODO Auto-generated method stub
		return false;
	}

}
