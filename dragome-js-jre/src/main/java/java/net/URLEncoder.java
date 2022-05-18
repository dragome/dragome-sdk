package java.net;

import java.io.UnsupportedEncodingException;

import com.dragome.commons.javascript.ScriptHelper;

public class URLEncoder
{

	static final String digits= "0123456789ABCDEF";

	private URLEncoder()
	{
	}

	@Deprecated
	public static String encode(String s)
	{
		if (s == null)
			throw new NullPointerException();

		return (String) ScriptHelper.eval("encodeURI(s)", null);
	}

	public static String encode(String s, String enc) throws UnsupportedEncodingException
	{
		return encode(s);
	}
}