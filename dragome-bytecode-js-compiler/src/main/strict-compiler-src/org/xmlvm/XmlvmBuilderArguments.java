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

/**
 * This class parses the arguments for the XmlvmBuilder given in a string array
 * and makes them easily accessible for the application to use.
 * <p>
 * NOTE: This class will go away soon as well as {@link XmlvmBuilder} itself.
 */
@Deprecated
public class XmlvmBuilderArguments
{
	// The arguments that are given by the user on the command line.
	public static final String ARG_JSBUILD= "--jsbuild";
	public static final String ARG_DESTINATION= "--destination=";
	public static final String ARG_CLASSPATH= "--classpath=";
	public static final String ARG_EXEPATH= "--exepath=";
	public static final String ARG_JSRESOURCE= "--javascriptresource=";
	public static final String ARG_RESOURCE= "--includeresource=";
	public static final String ARG_MAIN= "--main=";
	public static final String ARG_QXSOURCEBUILD= "--qxsourcebuild";

	// The parsed values will be stored here.
	private String option_destination= "";
	private String option_classpath= "";
	private String option_exepath= "";
	private String option_javascriptresource= "";
	private String option_includeresource= "";
	private String option_main= "";
	private boolean option_qxsourcebuild= false;

	/**
	 * Prints out usage information for XmlvmBuilder's parameters and exit the
	 * application.
	 * 
	 * @param error
	 *            An optional error message that is printed along with the usage
	 *            information.
	 */
	private static void usage(String error)
	{
		System.err.println(error + '\n');
		// TODO(haeberling): Complete.
		String[] msg= { "Usage: XmlvmBuilder ..... TODO", "  --destination=        : Destination path", "  --classpath=          : Path where class files are picked up", "  --exepath=            : Path where exe files are picked up", "  --javascriptresource= : TODO                 ", "  --includeresource=    : TODO                 ", "  --main=               : <(package.)ClassName>.[main|Main]", "  --qxsourcebuild       : Creates a 'source' instead of a 'build' package.", };
		for (int i= 0; i < msg.length; i++)
		{
			System.err.println(msg[i]);
		}
	}

	/**
	 * Returns whether the XmlvmBuilder should be called, based on the arguments
	 * given by the user.
	 * 
	 * @param argv
	 *            The argument vector.
	 */
	public static boolean shoulCallXmlvmBuilder(String argv[])
	{
		for (String argument : argv)
		{
			if (argument.equals(ARG_JSBUILD))
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * Creates a new instance that will parse the command lines arguments.
	 */
	public XmlvmBuilderArguments(String argv[])
	{
		// Read command line arguments
		for (int i= 0; i < argv.length; i++)
		{
			String arg= argv[i];
			if (arg.startsWith(ARG_DESTINATION))
			{
				option_destination= arg.substring(ARG_DESTINATION.length());
			}
			else if (arg.startsWith(ARG_CLASSPATH))
			{
				option_classpath= arg.substring(ARG_CLASSPATH.length());
			}
			else if (arg.startsWith(ARG_EXEPATH))
			{
				option_exepath= arg.substring(ARG_EXEPATH.length());
			}
			else if (arg.startsWith(ARG_JSRESOURCE))
			{
				option_javascriptresource= arg.substring(ARG_JSRESOURCE.length());
			}
			else if (arg.startsWith(ARG_RESOURCE))
			{
				option_includeresource= arg.substring(ARG_RESOURCE.length());
			}
			else if (arg.startsWith(ARG_MAIN))
			{
				option_main= arg.substring(ARG_MAIN.length());
			}
			else if (arg.startsWith(ARG_QXSOURCEBUILD))
			{
				option_qxsourcebuild= true;
			}
			else if (arg.startsWith(ARG_JSBUILD))
			{
				// Ignore.
			}
			else
			{
				usage("Unknown parameter: " + arg);
				System.exit(-1);
			}
		}
	}

	public String option_destination()
	{
		return option_destination;
	}

	public String option_classpath()
	{
		return option_classpath;
	}

	public String option_exepath()
	{
		return option_exepath;
	}

	public String option_javascriptresource()
	{
		return option_javascriptresource;
	}

	public String option_includeresource()
	{
		return option_includeresource;
	}

	public String option_main()
	{
		return option_main;
	}

	public boolean option_qxsourcebuild()
	{
		return option_qxsourcebuild;
	}
}
