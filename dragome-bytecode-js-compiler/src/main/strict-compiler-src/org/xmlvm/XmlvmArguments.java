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
 * This class parses the arguments given in a string array and makes them easily
 * accessible for the application to use.
 * 
 * TODO(Sascha): Look at other open source argument parsing libraries to
 * replace this.
 * 
 */
@Deprecated
public class XmlvmArguments
{
	// The arguments that are given by the user on the command line.
	public static final String ARG_JS= "--js";
	public static final String ARG_CPP= "--cpp";
	public static final String ARG_OBJC= "--objc";
	public static final String ARG_OBJC_HEADER= "--objc-header=";
	public static final String ARG_IPHONE_APP= "--iphone-app=";
	public static final String ARG_ANDROID_TO_IPHONE= "--android2iphone=";
	public static final String ARG_PYTHON= "--python";
	public static final String ARG_DFA= "--dfa";
	public static final String ARG_JVM= "--jvm";
	public static final String ARG_CLR= "--clr";
	public static final String ARG_EXE= "--exe";
	public static final String ARG_API= "--api";
	public static final String ARG_JAVA= "--java";
	public static final String ARG_CONSOLE= "--console";
	public static final String ARG_OUT= "--out=";
	public static final String ARG_IMPORT= "--import";
	public static final String ARG_RECURSIVE= "--recursive";
	// public static final String ARG_FILE = "--file=";

	// The parsed values will be stored here.
	private boolean option_js= false;
	private boolean option_cpp= false;
	private boolean option_objc= false;
	private String option_objc_header= null;
	private String option_iphone_app= null;
	private boolean option_android2iphone= false;
	private boolean option_python= false;
	private boolean option_dfa= false;
	private boolean option_jvm= false;
	private boolean option_clr= false;
	private boolean option_exe= false;
	private boolean option_api= false;
	private boolean option_java= false;
	private boolean option_console= false;
	private String option_out= null;
	private boolean option_import= false;
	private boolean option_recursive= false;
	private String option_class= null;

	/**
	 * Prints usage information and exits the applications.
	 * 
	 * @param error
	 *            An additional error message to be printed before the usage
	 *            table is printed.
	 */
	private static void usage(String error)
	{
		String[] msg= { "Usage: xmlvm [--js|--cpp] [--import] [--recursive] [--console|--out=<file>] --file <inputfile>", "  --js            : Generate JavaScript", "  --cpp           : Generate C++", "  --import        : Generate import list of referenced externals", "  --console       : Output is to be written to the console.", "  --out           : Output directory.", "  --recursive     : Recursivley scan through the referenced externals", "  --file          : Input file", "  <file>          : Byte code to be translated. If <class> ends on '.exe',", "                    the bytecode is assumed to the a .NET executable file", "                    with the same name. If <class> ends on '.class', the", "                    bytecode is assumed to be of JVM format in a file with", "                    the same name. Otherwise, <class> is looked up via CLASSPATH.", "  If neither --js nor --cpp is specified, the output will be XMLVM.",
		        "  If the option --console is not given, the output will be written to a", "  file with the same name as <class> and suffix one of .xmlvm, .js, or .cpp" };

		System.err.println("Error: " + error);
		for (int i= 0; i < msg.length; i++)
		{
			System.err.println(msg[i]);
		}
	}

	/**
	 * Creates a new instance that will parse the arguments of the given array.
	 */
	public XmlvmArguments(String[] argv)
	{
		// Read command line arguments
		for (int i= 0; i < argv.length; i++)
		{
			String arg= argv[i];
			if (arg.startsWith(ARG_JS))
			{
				option_js= true;
			}
			else if (arg.startsWith(ARG_CPP))
			{
				option_cpp= true;
			}
			else if (arg.startsWith(ARG_OBJC))
			{
				option_objc= true;
			}
			else if (arg.startsWith(ARG_OBJC_HEADER))
			{
				option_objc_header= arg.substring(ARG_OBJC_HEADER.length());
			}
			else if (arg.startsWith(ARG_IPHONE_APP))
			{
				option_objc= true;
				option_iphone_app= arg.substring(ARG_IPHONE_APP.length());
			}
			else if (arg.startsWith(ARG_ANDROID_TO_IPHONE))
			{
				option_objc= true;
				option_iphone_app= arg.substring(ARG_ANDROID_TO_IPHONE.length());
				option_android2iphone= true;
			}
			else if (arg.equals(ARG_PYTHON))
			{
				option_python= true;
			}
			else if (arg.equals(ARG_DFA))
			{
				option_dfa= true;
			}
			else if (arg.equals(ARG_JVM))
			{
				option_jvm= true;
			}
			else if (arg.equals(ARG_CLR))
			{
				option_clr= true;
			}
			else if (arg.equals(ARG_EXE))
			{
				option_exe= true;
			}
			else if (arg.equals(ARG_API))
			{
				option_api= true;
			}
			else if (arg.equals(ARG_JAVA))
			{
				option_java= true;
			}
			else if (arg.equals(ARG_CONSOLE))
			{
				option_console= true;
			}
			else if (arg.startsWith(ARG_OUT))
			{
				option_out= arg.substring(ARG_OUT.length());
			}
			else if (arg.equals(ARG_IMPORT))
			{
				option_import= true;
			}
			else if (arg.equals(ARG_RECURSIVE))
			{
				option_recursive= true;
			}
			else if (option_class != null)
			{
				usage("Unknown parameter: " + arg);
				System.exit(-1);
			}
			else
			{
				option_class= arg;
			}
		}

		// Sanity check command line arguments
		if (option_js && option_cpp)
			usage("Cannot specify --js and --cpp at the same time");
		if (option_class == null)
			usage("No input file specified");
		if (option_java && option_console)
			usage("Cannot output class file to console.  Must specify --out=<file>");
	}

	public boolean js()
	{
		return option_js;
	}

	public boolean option_js()
	{
		return option_js;
	}

	public boolean option_cpp()
	{
		return option_cpp;
	}

	public boolean option_objc()
	{
		return option_objc;
	}

	public String option_objc_header()
	{
		return option_objc_header;
	}

	public String option_iphone_app()
	{
		return option_iphone_app;
	}

	public boolean option_android2iphone()
	{
		return option_android2iphone;
	}

	public boolean option_python()
	{
		return option_python;
	}

	public boolean option_dfa()
	{
		return option_dfa;
	}

	public boolean option_jvm()
	{
		return option_jvm;
	}

	public boolean option_clr()
	{
		return option_clr;
	}

	public boolean option_exe()
	{
		return option_exe;
	}

	public boolean option_api()
	{
		return option_api;
	}

	public boolean option_java()
	{
		return option_java;
	}

	public boolean option_console()
	{
		return option_console;
	}

	public String option_out()
	{
		return option_out;
	}

	public boolean option_import()
	{
		return option_import;
	}

	public boolean option_recursive()
	{
		return option_recursive;
	}

	public String option_class()
	{
		return option_class;
	}
}
