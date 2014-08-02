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

package org.xmlvm.util;

/**
 * A simple timer that can be used e.g. for debugging.
 */
public class Timer
{
	private final String name;
	private long start;
	private long end;

	/**
	 * Initializes the timer with the given name.
	 * 
	 * @param name
	 *            the name of the timer
	 */
	public Timer(String name)
	{
		this.name= name;
	}

	/**
	 * Starts the timer.
	 */
	public void start()
	{
		start= System.currentTimeMillis();
	}

	/**
	 * Stops the timer.
	 * 
	 * @return Returns itself.
	 */
	public Timer stop()
	{
		end= System.currentTimeMillis();
		return this;
	}

	/**
	 * Returns a string with the name of the timer and the milliseconds between
	 * {@link #start()} and {@link #stop()}.
	 */

	public String toString()
	{
		return name + ":" + (end - start) + " ms.";
	}
}
