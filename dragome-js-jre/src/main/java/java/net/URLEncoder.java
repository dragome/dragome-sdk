package java.net;

import com.dragome.commons.javascript.ScriptHelper;

/**
 * Utility class for HTML form encoding.
 * 
 * 
 */
public class URLEncoder
{

	/**
	 * Translates a string into application/x-www-form-urlencoded format using a specific encoding scheme.

	 * @param enc must be 'UTF-8'
	 */
	public static String encode(String s, String enc)
	{ // TODO: throws UnsupportedEncodingException
		//s = s.replaceAll(" ", "+");
		ScriptHelper.put("s", s, null);
		s= (String) ScriptHelper.eval("encodeURIComponent(s)", null);
		return s;
	}

}
