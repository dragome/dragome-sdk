package java.net;

import com.dragome.commons.javascript.ScriptHelper;

/**
 * Utility class for HTML form decoding.
 * 
 * 
 */
public class URLDecoder
{

	/**
	 * Decodes a application/x-www-form-urlencoded string using a specific encoding scheme.
	 * 
	 * @param enc must be 'UTF-8'
	 */
	public static String decode(String s, String enc)
	{
		ScriptHelper.put("s", s, null);
		s= s.replaceAll("\\+", " ");
		s= (String) ScriptHelper.eval("decodeURIComponent(s)", null);
		return s;
	}

}
