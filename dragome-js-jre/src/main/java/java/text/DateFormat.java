/*
 * Copyright (c) 1996, 2010, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

/*
 * (C) Copyright Taligent, Inc. 1996 - All Rights Reserved
 * (C) Copyright IBM Corp. 1996 - All Rights Reserved
 *
 *   The original version of this source code and documentation is copyrighted
 * and owned by Taligent, Inc., a wholly-owned subsidiary of IBM. These
 * materials are provided under terms of a License Agreement between Taligent
 * and Sun. This technology is protected by multiple US and International
 * patents. This notice and attribution to Taligent may not be removed.
 *   Taligent is a registered trademark of Taligent, Inc.
 *
 */

package java.text;

import java.util.Date;
import java.util.Locale;

import com.dragome.commons.javascript.ScriptHelper;

/**
 * {@code DateFormat} is an abstract class for date/time formatting subclasses which
 * formats and parses dates or time in a language-independent manner.
 * The date/time formatting subclass, such as {@link SimpleDateFormat}, allows for
 * formatting (i.e., date -> text), parsing (text -> date), and
 * normalization.  The date is represented as a <code>Date</code> object or
 * as the milliseconds since January 1, 1970, 00:00:00 GMT.
 *
 * <p>{@code DateFormat} provides many class methods for obtaining default date/time
 * formatters based on the default or a given locale and a number of formatting
 * styles. The formatting styles include {@link #FULL}, {@link #LONG}, {@link #MEDIUM}, and {@link #SHORT}. More
 * detail and examples of using these styles are provided in the method
 * descriptions.
 *
 * <p>{@code DateFormat} helps you to format and parse dates for any locale.
 * Your code can be completely independent of the locale conventions for
 * months, days of the week, or even the calendar format: lunar vs. solar.
 *
 * <p>To format a date for the current Locale, use one of the
 * static factory methods:
 * <pre>
 *  myString = DateFormat.getDateInstance().format(myDate);
 * </pre>
 * <p>If you are formatting multiple dates, it is
 * more efficient to get the format and use it multiple times so that
 * the system doesn't have to fetch the information about the local
 * language and country conventions multiple times.
 * <pre>
 *  DateFormat df = DateFormat.getDateInstance();
 *  for (int i = 0; i < myDate.length; ++i) {
 *    output.println(df.format(myDate[i]) + "; ");
 *  }
 * </pre>
 * <p>To format a date for a different Locale, specify it in the
 * call to {@link #getDateInstance(int, Locale) getDateInstance()}.
 * <pre>
 *  DateFormat df = DateFormat.getDateInstance(DateFormat.LONG, Locale.FRANCE);
 * </pre>
 * <p>You can use a DateFormat to parse also.
 * <pre>
 *  myDate = df.parse(myString);
 * </pre>
 * <p>Use {@code getDateInstance} to get the normal date format for that country.
 * There are other static factory methods available.
 * Use {@code getTimeInstance} to get the time format for that country.
 * Use {@code getDateTimeInstance} to get a date and time format. You can pass in
 * different options to these factory methods to control the length of the
 * result; from {@link #SHORT} to {@link #MEDIUM} to {@link #LONG} to {@link #FULL}. The exact result depends
 * on the locale, but generally:
 * <ul><li>{@link #SHORT} is completely numeric, such as {@code 12.13.52} or {@code 3:30pm}
 * <li>{@link #MEDIUM} is longer, such as {@code Jan 12, 1952}
 * <li>{@link #LONG} is longer, such as {@code January 12, 1952} or {@code 3:30:32pm}
 * <li>{@link #FULL} is pretty completely specified, such as
 * {@code Tuesday, April 12, 1952 AD or 3:30:42pm PST}.
 * </ul>
 *
 * <p>You can also set the time zone on the format if you wish.
 * If you want even more control over the format or parsing,
 * (or want to give your users more control),
 * you can try casting the {@code DateFormat} you get from the factory methods
 * to a {@link SimpleDateFormat}. This will work for the majority
 * of countries; just remember to put it in a {@code try} block in case you
 * encounter an unusual one.
 *
 * <p>You can also use forms of the parse and format methods with
 * {@link ParsePosition} and {@link FieldPosition} to
 * allow you to
 * <ul><li>progressively parse through pieces of a string.
 * <li>align any particular field, or find out where it is for selection
 * on the screen.
 * </ul>
 *
 * <h4><a name="synchronization">Synchronization</a></h4>
 *
 * <p>
 * Date formats are not synchronized.
 * It is recommended to create separate format instances for each thread.
 * If multiple threads access a format concurrently, it must be synchronized
 * externally.
 *
 * @see          Format
 * @see          NumberFormat
 * @see          SimpleDateFormat
 * @see          java.util.Calendar
 * @see          java.util.GregorianCalendar
 * @see          java.util.TimeZone
 * @author       Mark Davis, Chen-Lieh Huang, Alan Liu
 */
public abstract class DateFormat extends Format
{

	public static final String LONG= null;
	public static final String MEDIUM= null;
	public static final String SHORT= null;
	private String pattern;

	public DateFormat(String pattern)
	{
		this.pattern= pattern;
	}

	@Override
	public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos)
	{
		return new StringBuffer(format((Date)obj));
	}
	public static DateFormat getDateTimeInstance(String l, String m)
	{
		// TODO Auto-generated method stub
		return null;
	}
	public static DateFormat getDateTimeInstance()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public static final DateFormat getDateTimeInstance(int dateStyle, int timeStyle)
	{
		return null;
	}

	public Date parse(String source) throws ParseException
	{
		ScriptHelper.put("customPattern", pattern, this);
		ScriptHelper.put("aDate", source, this);

		long eval= ScriptHelper.evalLong("JSJoda.convert(JSJoda.DateTimeFormatter.ofPattern(customPattern).parse(aDate).date).toDate().getTime()", this);
		return new Date(eval);
	}

	public String format(Date date)
	{
		ScriptHelper.put("aDate", date, this);
		ScriptHelper.put("customPattern", pattern, this);
		Object eval= ScriptHelper.eval("JSJoda.LocalDateTime.from(JSJoda.nativeJs(aDate.nativeDate)).format(JSJoda.DateTimeFormatter.ofPattern(customPattern))", this);
		return eval.toString();
	}
}
