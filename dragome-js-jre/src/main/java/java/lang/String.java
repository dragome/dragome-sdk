/*
 * Copyright (c) 2011-2014 Fernando Petrola
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package java.lang;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.StringJoiner;

import com.dragome.commons.javascript.ScriptHelper;

/*
 * Copyright (c) 2005 j2js.com,
 *
 * All Rights Reserved. This work is distributed under the j2js Software License [1]
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.
 *
 * [1] http://www.j2js.com/license.txt
 */
public final class String implements CharSequence, Comparable<String>, Serializable
{

	private int hashCode;

	private static void consume(String me)
	{
		// Tagging method.
		// All String constructors are converted to methods returning the argument of the last method call.
	}

	public String()
	{
		consume("");
	}

	public String(String value)
	{
		// Constructor needs to throw a NullPointerException if value is null.
		consume(value.toString());
	}

	public String(char[] data)
	{
		consume(String.valueOf(data));
	}

	public String(char[] data, int offset, int count)
	{
		consume(String.valueOf(data, offset, count));
	}

	private static String init(byte[] bytes, int offset, int count, String enc)
	{
		char[] chars= new char[count];
		for (int i= 0; i < count; i++)
		{
			chars[i]= (char) bytes[offset + i];
		}

		return String.valueOf(chars);
	}

	public String(byte[] bytes, int offset, int count, String enc)
	{
		consume(init(bytes, offset, count, enc));
	}

	public String(byte[] bytes, String enc)
	{
		consume(init(bytes, 0, bytes.length, enc));
	}

	public String(byte[] bytes, int offset, int count)
	{
		consume(init(bytes, offset, count, null));
	}

	public String(byte[] bytes)
	{
		consume(init(bytes, 0, bytes.length, null));
	}

	public String(StringBuffer buffer)
	{
		consume(buffer.toString());
	}

	/**
	 * Returns the length of this string.
	 */
	public int length()
	{
		return ScriptHelper.evalInt("this.length", this);
	}

	/**
	 * Returns the character at the specified index.
	 */
	public char charAt(int index)
	{
		if (index < 0 || index >= length())
		{
			throw new IndexOutOfBoundsException();
		}
		ScriptHelper.put("index", index, this);
		return ScriptHelper.evalChar("this.charCodeAt(index)", this);
	}

	/**
	 * Copies characters from this string into the destination character array.
	 */
	public void getChars(int srcBegin, int srcEnd, char[] dst, int dstBegin)
	{
		for (int i= srcBegin; i < srcEnd; i++)
			dst[i - srcBegin + dstBegin]= charAt(i);
	}

	/**
	 * Encodes this String into a sequence of bytes using the named charset, storing the result into a new byte array.
	 */
	public byte[] getBytes(String enc) throws UnsupportedEncodingException
	{
		return getBytes();
	}

	/**
	 * Encodes this String into a sequence of bytes using the platform's default charset, storing the result into a new byte array.
	 */
	public byte[] getBytes()
	{
		byte[] data= new byte[length()];
		for (int i= 0; i < data.length; i++)
		{
			data[i]= (byte) charAt(i);
		}
		return data;
	}

	/**
	 * Compares this string to the specified object.
	 */
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj instanceof String)
		{
			ScriptHelper.put("obj", obj, this);
			ScriptHelper.evalNoResult("var string1 = String(this)", this);
			ScriptHelper.evalNoResult("var string2 = String(obj)", this);
			int n1= ScriptHelper.evalInt("string1.length", this);
			int n2= ScriptHelper.evalInt("string2.length", this);
			if (n1 == n2)
			{
				int i= 0;
				while (n1-- != 0)
				{
					ScriptHelper.put("i", i, this);
					boolean flag= ScriptHelper.evalBoolean("string1.charAt(i) == string2.charAt(i)", this);
					if (!flag)
						return false;
					i++;
				}
				return true;
			}
		}
		return false;
	}

	/**
	 * Compares this String to another String, ignoring case considerations.
	 */
	public boolean equalsIgnoreCase(String other)
	{
		if (other == null)
			return false;
		ScriptHelper.put("other", other, this);
		return ScriptHelper.evalBoolean("this.toLowerCase() == other.toLowerCase()", this);
	}

	/**
	 * Compares two strings lexicographically.
	 */
	public int compareTo(String other)
	{
		ScriptHelper.put("other", other, this);
		return ScriptHelper.evalInt("this<other?-1:(this>other?1:0)", this);
	}

	/**
	 *   Tests if two string regions are equal.
	 */
	public boolean regionMatches(boolean ignoreCase, int toffset, String other, int ooffset, int len)
	{
		if (ignoreCase)
		{
			return this.substring(toffset).equalsIgnoreCase(other.substring(ooffset));
		}
		else
		{
			return this.substring(toffset).equals(other.substring(ooffset));
		}
	}

	/**
	 *  Tests if this string starts with the specified prefix beginning a specified index.
	 */
	public boolean startsWith(String prefix, int offset)
	{
		return substring(offset, prefix.length()).equals(prefix);
	}

	/**
	 * Tests if this string starts with the specified prefix.
	 */
	public boolean startsWith(String prefix)
	{
		return startsWith(prefix, 0);
	}

	/**
	 * Tests if this string ends with the specified suffix.
	 */
	public boolean endsWith(String suffix)
	{
		return substring(length() - suffix.length()).equals(suffix);
	}

	private static String leftPadding(int width, int number)
	{
		String sign= "";
		if (number < 0)
		{
			sign= "-";
			number= -number;
		}
		String s= String.valueOf(number);
		for (int i= 0; i < width - s.length(); i++)
			s= "0" + s;
		return sign + s;
	}

	/**
	 * Returns a formatted string using the specified format string and arguments.
	 *
	 * For example:
	 * <pre>String.format("%1$ta, %1$td %1$tb %1$tY %1$tT %1$tZ", new Date())</pre>
	 * may return
	 * <pre>Wed, 18 Apr 2007 20:08:05 CEST</pre>
	 * <p>
	 * Currently supported are only the following 't' conversions:
	 * <tt>a, d, b, Y, H, M, S, T, z</tt>
	 * </p>
	 */
	public static String format(String format, Object... args)
	{
		StringBuilder sb= new StringBuilder();
		String[] parts= format.split("%");

		sb.append(parts[0]);

		for (int i= 1; i < parts.length; i++)
		{
			String[] daysOfWeek= new String[] { "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat" };
			String[] months= new String[] { "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dez" };

			String part= parts[i];
			int j= part.indexOf("$");
			int argIndex= Integer.parseInt(part.substring(0, j));
			Object arg= args[argIndex - 1];
			char type= part.charAt(j + 1);
			char conv= part.charAt(j + 2);
			String chunk= null;
			if (type == 't')
			{
				Date d= (Date) arg;
				Calendar cal= Calendar.getInstance();
				cal.setTime(d);
				if (conv == 'a')
				{
					//System.out.println(cal.get(Calendar.DAY_OF_WEEK));
					chunk= daysOfWeek[cal.get(Calendar.DAY_OF_WEEK) - 1];
				}
				else if (conv == 'd')
				{
					chunk= String.valueOf(cal.get(Calendar.DAY_OF_MONTH));
				}
				else if (conv == 'b')
				{
					chunk= months[cal.get(Calendar.MONTH)];
				}
				else if (conv == 'Y')
				{
					chunk= leftPadding(4, cal.get(Calendar.YEAR));
				}
				else if (conv == 'H')
				{
					chunk= leftPadding(2, cal.get(Calendar.HOUR_OF_DAY));
				}
				else if (conv == 'M')
				{
					chunk= leftPadding(2, cal.get(Calendar.MINUTE));
				}
				else if (conv == 'S')
				{
					chunk= leftPadding(2, cal.get(Calendar.SECOND));
				}
				else if (conv == 'z')
				{
					chunk= leftPadding(4, cal.get(Calendar.ZONE_OFFSET));
				}
				else if (conv == 'T')
				{
					chunk= String.format("%1$tH:%1$tM:%1$tS", d);
				}
			}
			if (chunk == null)
				throw new RuntimeException("Illegal format at: " + part);
			sb.append(chunk);
			sb.append(part.substring(j + 3));
		}

		return sb.toString();
	}

	/**
	 * Returns a hash code for this string.
	 */
	public int hashCode()
	{
		if (hashCode == 0)
			hashCode= ScriptHelper.evalInt("this.split(\"\").reduce(function(a,b){a=((a<<5)-a)+b.charCodeAt(0);return a&a},0)", this);
		
		return hashCode;
	}

	/**
	 * Returns the index within this string of the last occurrence of the specified character.
	 */
	public int lastIndexOf(int ch)
	{
		ScriptHelper.put("str", String.valueOf((char) ch), this);
		return ScriptHelper.evalInt("this.lastIndexOf(str)", this);
	}

	/**
	 *  Returns the index within this string of the last occurrence of the specified character, searching backward starting at the specified index.
	 */
	public int lastIndexOf(int ch, int position)
	{
		ScriptHelper.put("str", String.valueOf((char) ch), this);
		ScriptHelper.put("position", position, this);
		return ScriptHelper.evalInt("this.lastIndexOf(str, position)", this);
	}

	/**
	 * Returns the index within this string of the first occurrence of the specified substring.
	 */
	public int indexOf(String str)
	{
		return indexOf(str, 0);
	}

	/**
	 *  Returns the index within this string of the first occurrence of the specified substring, starting at the specified index.
	 */
	public int indexOf(String str, int position)
	{
		ScriptHelper.put("str", str, this);
		ScriptHelper.put("position", position, this);
		return ScriptHelper.evalInt("this.indexOf(str, position)", this);
	}

	/**
	 *  Returns the index within this string of the first occurrence of the specified substring, starting at the specified index.
	 */
	public int indexOf(int strr, int position)
	{ //FIXME dragome is making char as a int when using charAt( ) so this method is needed or it will give error by saying method dont exist.
		ScriptHelper.put("strr", strr, this);
		ScriptHelper.evalNoResult("var str = String.fromCharCode(strr)", this);
		ScriptHelper.put("position", position, this);
		return ScriptHelper.evalInt("this.indexOf(str, position)", this);
	}

	/**
	 *  Returns the index within this string of the first occurrence of the specified character.
	 */
	public int indexOf(int ch)
	{
		ScriptHelper.put("ch", ch, this);
		return ScriptHelper.evalInt("this.indexOf(String.fromCharCode(ch), 0)", this);
	}

	/**
	 * Returns a new string that is a substring of this string.
	 */
	public String substring(int beginIndex)
	{
		return substring(beginIndex, length());
	}

	/**
	 * Returns a new string that is a substring of this string.
	 */
	public String substring(int beginIndex, int endIndex)
	{
		ScriptHelper.put("beginIndex", beginIndex, this);
		ScriptHelper.put("endIndex", endIndex, this);
		return (String) ScriptHelper.eval("this.substring(beginIndex, endIndex)", this);
	}

	/**
	 *   Concatenates the specified string to the end of this string.
	 */
	public String concat(String str)
	{
		ScriptHelper.put("str", str, this);
		return (String) ScriptHelper.eval("this + str", this);
	}

	/**
	 *  Returns a new string resulting from replacing all occurrences of oldChar in this string with newChar.
	 */
	public String replace(char oldChar, char newChar)
	{
		ScriptHelper.put("oldChar", String.valueOf(oldChar), this);
		ScriptHelper.put("newChar", String.valueOf(newChar), this);
		return (String) ScriptHelper.eval("this.replace(new RegExp(oldChar, 'g'), newChar)", this);
	}

	/**
	 * Converts all of the characters in this String to lower case using the rules of the default locale.
	 */
	public String toLowerCase()
	{
		return (String) ScriptHelper.eval("this.toLowerCase()", this);
	}

	/**
	 *  Converts all of the characters in this String to upper case using the rules of the default locale.
	 */
	public String toUpperCase()
	{
		return (String) ScriptHelper.eval("this.toUpperCase()", this);
	}

	/**
	 * Returns a copy of the string, with leading and trailing whitespace omitted.
	 */
	public String trim()
	{
		return (String) ScriptHelper.eval("this.replace(/^\\s+/, '').replace(/\\s+$/, '')", this);
	}

	/**
	 *  This object (which is already a string!) is itself returned.
	 */
	public String toString()
	{
		return this;
	}

	/**
	 * Converts this string to a new character array.
	 */
	public char[] toCharArray()
	{
		char[] data= new char[length()];
		for (int i= 0; i < data.length; i++)
		{
			data[i]= charAt(i);
		}
		return data;
	}

	/**
	 *  Returns the string representation of the Object argument.
	 */
	public static String valueOf(Object obj)
	{
		if (obj == null)
			return "null";
		return obj.toString();
	}

	/**
	 * Returns the string representation of the char array argument.
	 */
	public static String valueOf(char[] data)
	{
		return valueOf(data, 0, data.length);
	}

	/**
	 * Returns the string representation of a specific subarray of the char array argument.
	 */
	public static String valueOf(char[] data, int offset, int count)
	{
		StringBuffer sb= new StringBuffer();
		for (int i= 0; i < count; i++)
		{
			sb.append(data[offset + i]);
		}
		return sb.toString();
	}

	/**
	 *  Returns the string representation of the boolean argument.
	 */
	public static String valueOf(boolean b)
	{
		return Boolean.toString(b);
	}

	/**
	 * Returns the string representation of the char argument.
	 */
	public static String valueOf(char c)
	{
		ScriptHelper.put("c", c, null);
		return (String) ScriptHelper.eval("String.fromCharCode(c)", null);
	}

	/**
	 *  Returns the string representation of the int argument.
	 */
	public static String valueOf(int i)
	{
		return Integer.toString(i);
	}

	/**
	 *   Returns the string representation of the long argument.
	 */
	public static String valueOf(long l)
	{
		ScriptHelper.put("l", l, null);
		return (String) ScriptHelper.eval("String(l)", null);
	}

	/**
	 *  Returns the string representation of the float argument.
	 */
	public static String valueOf(float f)
	{
		ScriptHelper.put("f", f, null);
		return (String) ScriptHelper.eval("String(f)", null);
	}

	/**
	 *  Returns the string representation of the double argument.
	 */
	public static String valueOf(double d)
	{
		ScriptHelper.put("d", d, null);
		return (String) ScriptHelper.eval("String(d)", null);
	}

	/**
	 *    Splits this string around matches of the given regular expression.
	 */
	public String[] split(String regex)
	{
		return split(regex, 0);
	}

	public String[] split(String regex, int maxMatch)
	{ // from GWT
	  // The compiled regular expression created from the string
		ScriptHelper.put("regex", regex, this);
		Object compiled= ScriptHelper.eval("new RegExp(regex, 'g');", this);
		ScriptHelper.put("compiled", compiled, this);
		// the Javascipt array to hold the matches prior to conversion
		String[] out= new String[0];
		ScriptHelper.put("out", out, this);
		// how many matches performed so far
		int count= 0;
		// The current string that is being matched; trimmed as each piece matches
		String trail= this;
		// used to detect repeated zero length matches
		// Must be null to start with because the first match of "" makes no
		// progress by intention
		String lastTrail= null;
		// We do the split manually to avoid Javascript incompatibility
		while (true)
		{
			// None of the information in the match returned are useful as we have no
			// subgroup handling

			ScriptHelper.put("trail", trail, this);
			Object matchObj= ScriptHelper.eval("compiled.exec(trail)", this);
			if (matchObj == null || trail == "" || (count == (maxMatch - 1) && maxMatch > 0))
			{
				out[count]= trail;
				break;
			}
			else
			{
				out[count]= trail.substring(0, getMatchIndex(matchObj));
				trail= trail.substring(getMatchIndex(matchObj) + getMatchLength(matchObj, 0), trail.length());
				// Force the compiled pattern to reset internal state
				ScriptHelper.evalNoResult("compiled.lastIndex = 0", this);
				// Only one zero length match per character to ensure termination
				if (lastTrail == trail)
				{
					out[count]= trail.substring(0, 1);
					trail= trail.substring(1);
				}
				lastTrail= trail;
				count++;
			}
		}
		// all blank delimiters at the end are supposed to disappear if maxMatch == 0;
		// however, if the input string is empty, the output should consist of a
		// single empty string
		if (maxMatch == 0 && this.length() > 0)
		{
			int lastNonEmpty= out.length;
			while (lastNonEmpty > 0 && out[lastNonEmpty - 1] == "")
			{
				--lastNonEmpty;
			}
			if (lastNonEmpty < out.length)
			{
				ScriptHelper.put("lastNonEmpty", lastNonEmpty, this);
				ScriptHelper.evalNoResult("out.length = lastNonEmpty", this);
			}
		}
		return out;
	}

	private static int getMatchIndex(Object matchObject)
	{
		ScriptHelper.put("matchObject", matchObject, null);
		return ScriptHelper.evalInt("matchObject.index", null);
	};

	private static int getMatchLength(Object matchObject, int index)
	{
		ScriptHelper.put("matchObject", matchObject, null);
		ScriptHelper.put("index", index, null);
		return ScriptHelper.evalInt("matchObject[index].length", null);
	};

	/**
	 * Tells whether or not this string matches the given regular expression.
	 */
	public boolean matches(String regex)
	{
		ScriptHelper.put("regex", "^" + regex + "$", this);
		return ScriptHelper.evalBoolean("this.match(new RegExp(regex))!=null", this);
	}

	private String replace(String regex, String flags, String replacement)
	{
		// \a -> a
		// \\ -> \
		// \" -> "
		// \n -> newline
		// \newline -> newline

		ScriptHelper.put("regex", regex, this);
		ScriptHelper.put("flags", flags, this);
		ScriptHelper.put("replacement", replacement, this);
		return (String) ScriptHelper.eval("this.replace(new RegExp(regex, flags), dragomeJs.unquote(replacement) )", this);
	}

	/**
	 * Replaces the first substring of this string that matches the given regular expression with the given replacement.
	 */
	public String replaceFirst(String regex, String replacement)
	{
		return replace(regex, "", replacement);
	}

	/**
	 * Replaces each substring of this string that matches the given regular expression with the given replacement.
	 */
	public String replaceAll(String regex, String replacement)
	{
		return replace(regex, "g", replacement);
	}

	/* (non-Javadoc)
	 * @see java.lang.CharSequence#subSequence(int, int)
	 */
	public CharSequence subSequence(int start, int end)
	{
		return substring(start, end);
	}

	//	public boolean contains(String string)
	//	{//TODO revisar!!!
	//		return indexOf(string) != -1;
	//	}

	public boolean contains(CharSequence s)
	{
		return indexOf(s.toString()) != -1;
	}

	public int lastIndexOf(String str)
	{
		ScriptHelper.put("str", str, this);
		return ScriptHelper.evalInt("this.lastIndexOf(str)", this);
	}

	public String replace(CharSequence target, CharSequence replacement)
	{
		ScriptHelper.put("string", target, this);
		ScriptHelper.put("string2", replacement, this);
		return (String) ScriptHelper.eval("this.replaceAll(string, string2)", this);
	}

	public boolean isEmpty()
	{
		return length() == 0;
	}

	public static String copyValueOf(char data[], int offset, int count)
	{
		return new String(data, offset, count);
	}

	public String toUpperCase(Locale root)
	{
		return toUpperCase();
	}

	public static String join(CharSequence delimiter, Iterable<? extends CharSequence> elements)
	{
		StringJoiner joiner= new StringJoiner(delimiter);
		for (CharSequence cs : elements)
		{
			joiner.add(cs);
		}
		return joiner.toString();
	}
}
