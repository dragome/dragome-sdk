/*******************************************************************************
 * Copyright (c) 2011-2014 Fernando Petrola
 * 
 *  This file is part of Dragome SDK.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package java.lang;

import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;

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
public class Throwable
{
	protected String message;
	protected Throwable cause;
	protected String stackTrace;

	public Throwable()
	{
	}

	public Throwable(String newMessage)
	{
		message= newMessage;
	}

	public Throwable(Throwable cause)
	{
		this.cause= cause;

		ScriptHelper.put("cause", cause, this);
		ScriptHelper.eval("this.stack= cause.stack", this);

		message= cause.getMessage();
	}

	public Throwable(String message, Throwable cause)
	{
		this(cause);
		this.message= message;
	}

	public String getMessage()
	{
		return message;
	}

	public String toString()
	{
		String s= this.getClass().getName();
		if (message != null)
		{
			s+= ": " + message;
		}
		return s;
	}

	public void printStackTrace()
	{
		printStackTrace(System.err);
	}

	public void printStackTrace(PrintStream stream)
	{
		printStackTrace(new PrintWriter(new OutputStreamWriter(stream)));
	}

	/**
	 * Prints this throwable and its backtrace to the specified print writer.
	 */
	public void printStackTrace(PrintWriter printWriter)
	{
		printWriter.print(toString());
		printWriter.println(stackTrace);

		if (cause != null)
		{
			printWriter.println("Caused by:");
			cause.printInnerStackTrace(printWriter);
		}
		else
			printInnerStackTrace(printWriter);
	}

	public void printInnerStackTrace(PrintWriter printWriter)
	{
		if (ScriptHelper.evalBoolean("this.stack", this))
		{
			String stack= (String) ScriptHelper.eval("this.stack", this);
			printWriter.print(stack);
		}
	}

	/**
	 * Returns the cause of this throwable or null if the cause is nonexistent or unknown.
	 */
	public Throwable getCause()
	{
		return cause;
	}

	/**
	 * Fills in the execution stack trace.
	 */
	public Throwable fillInStackTrace()
	{
		stackTrace= (String) ScriptHelper.eval("stackTrace.toString()", this);
		return this;
	}

	public StackTraceElement[] getStackTrace()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public void setStackTrace(StackTraceElement[] stackTrace2)
	{
		// TODO Auto-generated method stub

	}

}
