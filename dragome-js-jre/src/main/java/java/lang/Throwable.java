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

import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Objects;

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

	public Throwable(java.lang.String message2, java.lang.Throwable cause2, boolean enableSuppression, boolean writableStackTrace)
	{
		// TODO Auto-generated constructor stub
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
		StackTraceElement[] stackTraceElements= new StackTraceElement[0];
		Object calle= ScriptHelper.eval("arguments.callee", this);

		int i= 0;
		while (calle != null && i < 100)
		{
			String displayName= (String) ScriptHelper.eval("calle.displayName || ''", this);

			String[] split= displayName.split("\\.prototype\\.");
			if (split.length == 2)
			{
				StackTraceElement stackTraceElement= new StackTraceElement(split[0].replace("_", "."), split[1], "file1", 13);
				stackTraceElements[i++]= stackTraceElement;
			}
			calle= ScriptHelper.eval("calle.caller", this);
			ScriptHelper.put("calle", calle, this);
		}
		return stackTraceElements;
	}

	public void setStackTrace(StackTraceElement[] stackTrace2)
	{
		// TODO Auto-generated method stub

	}

	public synchronized Throwable initCause(Throwable cause)
	{
		if (this.cause != this)
			throw new IllegalStateException("Can't overwrite cause with " + Objects.toString(cause, "a null"), this);
		if (cause == this)
			throw new IllegalArgumentException("Self-causation not permitted", this);
		this.cause= cause;
		return this;
	}

}
