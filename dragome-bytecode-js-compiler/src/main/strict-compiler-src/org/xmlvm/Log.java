/* Copyright (c) 2002-2011 by XMLVM.org
 *
 * Project Info:  http://www.xmlvm.org
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301,
 * USA.
 */

package org.xmlvm;

import java.io.PrintStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Basic logging functionality.
 */
public class Log
{

	/**
	 * The different logging levels.
	 */
	public enum Level
	{
		NONE("          ", System.out), ERROR("   ERROR: ", System.err), WARNING(" WARNING: ", System.out), ALL("   DEBUG: ", System.out);

		private final String prefix;
		private final PrintStream stream;

		Level(String prefix, PrintStream stream)
		{
			this.prefix= prefix;
			this.stream= stream;
		}

		/**
		 * Flushes the output stream.
		 */
		public void flush()
		{
			stream.flush();
		}

		public static Level getLevel(String level)
		{
			try
			{
				return Level.valueOf(level.toUpperCase());
			}
			catch (IllegalArgumentException ex)
			{
			}
			return null;
		}
	}

	private static final String DATE_FORMAT= "MM/dd/yy HH:mm:ss.SSS";
	private static final DateFormat dateFormat= new SimpleDateFormat(DATE_FORMAT);
	private static Level level= Level.ALL;

	/**
	 * Display log message, if it is in the correct log level
	 * 
	 * @param l
	 *            desired logging level
	 * @param message
	 *            message to display
	 */
	private static void display(Level l, String message)
	{
		if (l.compareTo(level) <= 0 && message != null)
		{
			l.stream.println("[" + getDate() + "] " + l.prefix + message);
		}
	}

	/**
	 * Logs an error message
	 * 
	 * @param tag
	 *            a tag displayed in front of the message
	 * @param message
	 *            the error message
	 */
	public static void error(String tag, String message)
	{
		display(Level.ERROR, tag + ": " + message);
	}

	/**
	 * Logs an error message
	 * 
	 * @param message
	 *            the error message
	 */
	public static void error(String message)
	{
		display(Level.ERROR, message);
	}

	/**
	 * Logs a debug message
	 * 
	 * @param tag
	 *            a tag displayed in front of the message
	 * @param message
	 *            the debug message
	 */
	public static void debug(String tag, String message)
	{
		display(Level.ALL, tag + ": " + message);
	}

	/**
	 * Logs a debug message
	 * 
	 * @param message
	 *            the debug message
	 */
	public static void debug(String message)
	{
		display(Level.ALL, message);
	}

	/**
	 * Logs a warning to {@link System.out}. If quiet mode is enabled, this
	 * message will not be logged.
	 * 
	 * @param tag
	 *            a tag displayed in front of the message
	 * @param message
	 *            the warning message
	 */
	public static void warn(String tag, String message)
	{
		display(Level.WARNING, tag + ": " + message);
	}

	/**
	 * Logs a warning to {@link System.out}. If quiet mode is enabled, this
	 * message will not be logged.
	 * 
	 * @param message
	 *            the warning message
	 */
	public static void warn(String message)
	{
		display(Level.WARNING, message);
	}

	private static String getDate()
	{
		return dateFormat.format(new Date());
	}

	public static void setLevel(Level newLevel)
	{
		if (newLevel != null)
			level= newLevel;
	}
}
