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

package org.xmlvm.main;

import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import java.util.Set;
import java.util.StringTokenizer;
import org.xmlvm.Log;
import org.xmlvm.proc.lib.Libraries;

/**
 * This class parses the arguments given in a string array and makes them easily
 * accessible for the application to use.
 */
public class Arguments
{
	// The arguments that are given by the user on the command line.

	public static final String ARG_IN= "--in=";
	public static final String ARG_OUT= "--out=";
	public static final String ARG_TARGET= "--target=";
	public static final String ARG_RESOURCE= "--resource=";
	public static final String ARG_LIB= "--lib=";
	public static final String ARG_DEPS= "--deps=";
	public static final String ARG_APP_NAME= "--app-name=";
	public static final String ARG_QX_MAIN= "--qx-main=";
	public static final String ARG_QX_DEBUG= "--qx-debug";
	public static final String ARG_QX_NO_GENERATE= "--qx-no-generate";
	public static final String ARG_DEBUG= "--debug=";
	public static final String ARG_VERSION= "--version";
	public static final String ARG_GEN_NATIVE_SKELETONS= "--gen-native-skeletons";
	public static final String ARG_HELP= "--help";
	public static final String ARG_SKELETON= "--skeleton=";
	// These are obsolete arguments, being here for compatibility reasons
	public static final String ARG_IPHONE_APP= "--iphone-app=";
	public static final String ARG_QX_APP= "--qx-app=";
	public static final String ARG_QUIET= "--quiet";
	// This is just temporary for activating the new DEX processing.
	public static final String ARG_USE_JVM= "--use-jvm";
	// Enables/disables the dependency resolution feature.
	public static final String ARG_LOAD_DEPENDENCIES= "--load-dependencies";
	public static final String ARG_DISABLE_LOAD_DEPENDENCIES= "--disable-load-dependencies";
	public static final String ARG_REDLIST= "--redlist=";
	public static final String ARG_GREENLIST= "--greenlist=";
	public static final String ARG_REFLECTION_CLASS_LIST= "--reflection-class-list=";
	// Enables reference counting for DEX input.
	public static final String ARG_ENABLE_REF_COUNTING= "--enable-ref-counting";
	// Enables a debug counter for measuring execution time.
	public static final String ARG_ENABLE_TIMER= "--enable-timer";
	public static final String ARG_C_SOURCE_EXTENSION= "--c-source-extension=";
	public static final String ARG_NO_CACHE= "--no-cache";
	public static final String ARG_MOBILE= "--mobile";
	// This argument will store various properties to XMLVM
	// An example of these values can be found in the long help
	public static final String ARG_PROPERTY= "-D";

	public static final String ARG_XMLVM_NEW_IOS_API= "--xmlvm-new-ios-api";

	// The parsed values will be stored here.
	private List<String> option_in= new ArrayList<String>();
	private String option_out= null;
	private Targets option_target= Targets.NONE;
	private boolean option_gen_native_skeletons= false;
	private Set<String> option_resource= new HashSet<String>();
	private Set<String> option_lib= new HashSet<String>();
	private Set<String> option_deps= new HashSet<String>();
	private String option_app_name= null;
	private String option_qx_main= null;
	private boolean option_qx_debug= false;
	private boolean option_qx_no_generate= false;
	private Log.Level option_debug= Log.Level.WARNING;
	private String option_skeleton= null;
	private boolean option_use_jvm= false;
	private boolean option_load_dependencies= false;
	private boolean option_disable_load_dependencies= false;
	private String option_redlist= null;
	private String option_greenlist= null;
	private String option_reflection_class_list= null;
	private boolean option_enable_ref_counting= false;
	private boolean option_enable_timer= false;
	private String option_c_source_extension= "c";
	private boolean option_no_cache= false;
	private boolean option_mobile= false;
	private Map<String, String> option_property= new HashMap<String, String>();
	private boolean option_xmlvm_new_ios_api= false;

	private static final String[] shortUsage= { "Usage: ", "xmlvm [--in=<path> [--out=<dir>]]", "      [--deps=<dep1,dep2,...>]", "      [--target=[xmlvm|dexmlvm|jvm|clr|dfa|class|exe|dex|js|java|c|python|objc|iphone|qooxdoo|vtable|webos|csharp]]", "      [--skeleton=<type>]", "      [--lib=<name>", "      [--app-name=<app-name>]", "      [--resource=<path>]", "      [--qx-main=<main-class> [--qx-debug]]", "      [--debug=[none|error|warning|all]]", "      [--version] [--help]" };
	private static final String[] longUsage= { "Detailed usage:", "===============", "", " --in=<path>       Pathname of input files. Can be *.class *.exe and *.xmlvm", "", " --out=<dir>       Directory of output files (defaults to \".\")", "", " --target=<target> Desired target, could be one of the following:", "    xmlvm            XMLVM output, depending on the input (default)", "    dexmlvm          XMLVM_dex output", "    jvm              XMLVM_jvm output", "    clr              XMLVM_clr output", "    dfa              Data Flow Analysis on input files", "    class            Java class bytecode", "    exe              .NET executable", "    dex              DEX bytecode", "    js               JavaScript", "    java             Java source code", "    c                C source code", "    gen-c-wrappers   Generates C wrappers while preserving hand-written code from overridden files in the 'out' directory.", "    python           Python",
	        "    objc             Objective C source code", "    iphone           iPhone Objective-C", "    qooxdoo          JavaScript Qooxdoo web application", "    vtable           Vtable calculation (pre-step for e.g. C generation)", "    webos            WebOS JavaScript Project", "    csharp           C# source code", "", " --deps=<dep1, ...>  Additional dependencies such as libraries your app is depending on.", "                     Only the classes your app is depending on are actually cross-compiled.", "                     A dependency can be a classpath folder on the file system or a JAR file.", "", "--gen-native-skeletons Generates skeletons for Java native methods in the target", "                   language (currently only available for --target=c", "", " --skeleton=<type> Skeleton to create a new template project:", "    iphone           iPhone project skeleton",
	        "    iphone-hybrid    iPhone project skeleton with a cross compiled part (in 'src-common') and a native part ('in 'src-ios')", "    android          Android/iPhone project skeleton", "    android:migrate  Migrate an existing Android project to XMLVM (needs project created by 'android create project' command)", "    iphone:update    Update an existing XMLVM/iPhone project to latest build scripts", "    android:update   Update an existing XMLVM/Android project to latest build scripts", "", " --lib=<libraries> Comma separated list of extra libraries required for the specified target. Use a tilde at the and of the library name, to mark it as 'Weak'.", "    android          Support of android applications", "    <LIB>.dylib      iPhone dynamic library <LIB>", "    <LIB>.Framework  iPhone framework <LIB>", "", " --app-name=<name> Application name, required for iphone, android-based and qooxdoo targets", "",
	        " --resource=<path> " + (File.pathSeparatorChar == ':' ? "Colon" : "Semicolon") + " separated list of external non parsable files and directories. Used in iphone-based templates to register auxilliary files. If this argument ends with '/', then the contents of this directory will be copied. If it is a directory and does not end with '/', then a verbatim copy of the directory will be performed. This argument can also be used to add extra C/C++/Obj-C source files in the produced Xcode project.", "", " --qx-main=<class> Entry point of Qooxdoo application", "", " --qx-debug        Create debug information of Qooxdoo target", "", " --qx-no-generate  Don't invoke the Qooxdoo 'generate.py' script. Use this option if you need to copy hand written Qooxdoo JavaScript into place before performing a Qooxdoo build.", "", " -Dkey=value       Set an Xcode property", "   XcodeProject       Template to use for Xcode project:", "     iphone             iPhone project skeleton",
	        "     ipad               iPad project skeleton", "     ios                iPhone and iPad project skeleton", "     iphone3            Legacy iPhone 3.1 project skeleton", "   BundleIdentifier   The value of CFBundleIdentifier in Info.plist", "   BundleVersion      The value of CFBundleVersion in Info.plist", "   BundleDisplayName  The value of CFBundleDisplayName in Info.plist", "   MainNib            The value of NSMainNibFile in Info.plist", "   MainStoryboard     The value of UIMainStoryboardFile in Info.plist", "   PrerenderedIcon    The iPhone application icon is already pre-rendered", "   FileSharingEnabled If iTunes file sharing should be enabled (value is 'true') or not (value is 'false')", "   StatusBarHidden    Hide (value is 'true') or display (value is 'false' status bar", "   ApplicationExits   Application does not run in background on suspend",
	        "   InterfaceOrientation Initial interface orientation. Should be one of: UIInterfaceOrientationPortrait UIInterfaceOrientationPortraitUpsideDown UIInterfaceOrientationLandscapeLeft UIInterfaceOrientationLandscapeRight", "   SupportedInterfaceOrientations Colon seperated list of supported interface orientations. See property InterfaceOrientation.", "   AppFonts           Colon separated list of custom fonts", "--mobile    Applicable only to the qooxdoo target - creates a qooxdoo mobile application rather than a desktop one.", "   InjectedInfoPlist  Raw XML that will be injected into Info.plist", "", " --debug=<level>   Debug information level", "    none             Be completely quiet, no information is printed", "    error            Only errors will be printed", "    warning          Warning and errors will be printed", "    all              All debug information (including errors and warnings)", "", " --version         Display version information", "",
	        " --help            This message", "" };
	private static final String[] Version= { "XMLVM 2", "Note: Not all command line arguments activated yet." };
	private boolean performSanityChecks= true;

	public static void printVersion()
	{
		printText(Version, System.out);
		System.exit(0);
	}

	private static void longUsage()
	{
		System.out.println("XMLVM: a flexible and extensible cross-compiler toolchain");
		printText(Version, System.out);
		System.out.println();
		printText(shortUsage, System.out);
		System.out.println();
		printText(longUsage, System.out);
		System.exit(0);
	}

	private static void parseError(String error)
	{
		System.err.println("Error: " + error);
		printText(shortUsage, System.err);
		System.err.println("Give --help parameter to see more detailed command line instructions.");
		System.exit(-1);
	}

	/**
	 * Creates a new instance that will parse the arguments of the given array.
	 */
	public Arguments(String[] argv)
	{
		this(argv, true);
	}

	/**
	 * Creates a new instance that will parse the arguments of the given array.
	 */
	public Arguments(String[] argv, boolean performSanityChecks)
	{
		// Add default properties
		option_property.put("xcodeproject", "iphone");
		option_property.put("bundleidentifier", "org.xmlvm.iphone.XMLVM_APP");
		option_property.put("bundleversion", "1.0");
		option_property.put("bundledisplayname", "XMLVM_APP");
		option_property.put("statusbarhidden", "false");
		option_property.put("prerenderedicon", "false");
		option_property.put("applicationexits", "true");
		option_property.put("interfaceorientation", "UIInterfaceOrientationPortrait");
		option_property.put("filesharingenabled", "false");
		option_property.put("injectedinfoplist", "");
		// Add default libraries
		option_lib.add("Foundation.framework");
		option_lib.add("UIKit.framework");
		option_lib.add("CoreGraphics.framework");
		option_lib.add("AVFoundation.framework~");
		option_lib.add("OpenGLES.framework~");
		option_lib.add("QuartzCore.framework~");
		option_lib.add("MessageUI.framework~");
		option_lib.add("MediaPlayer.framework~");
		option_lib.add("StoreKit.framework~");
		option_lib.add("CoreLocation.framework~");
		option_lib.add("MapKit.framework~");
		option_lib.add("GameKit.framework~");
		option_lib.add("iAd.framework~");
		option_lib.add("AudioToolbox.framework~");
		option_lib.add("CoreMotion.framework");
		option_lib.add("QuickLook.framework~");
		option_lib.add("CoreMedia.framework~");

		// Read command line arguments
		for (int i= 0; i < argv.length; i++)
		{
			String arg= argv[i];
			if (arg.startsWith(ARG_IN))
			{
				option_in.add(arg.substring(ARG_IN.length()));
			}
			else if (arg.startsWith(ARG_OUT))
			{
				if (option_out != null)
					parseError("--out can only be used once");
				option_out= arg.substring(ARG_OUT.length());
			}
			else if (arg.startsWith(ARG_TARGET))
			{
				if (option_target != Targets.NONE)
					parseError("--target can only be specified once");
				String target= arg.substring(ARG_TARGET.length());
				option_target= Targets.getTarget(target);
				if (option_target == null)
					parseError("Unkown target: " + target);
				if (option_target.affinity != Targets.Affinity.TARGET)
					parseError("Not valid target: " + target + ". Consider using --skeleton argument.");
			}
			else if (arg.startsWith(ARG_RESOURCE))
			{
				parseListArgument(arg.substring(ARG_RESOURCE.length()), option_resource, File.pathSeparator);
			}
			else if (arg.equals(ARG_GEN_NATIVE_SKELETONS))
			{
				option_gen_native_skeletons= true;
			}
			else if (arg.startsWith(ARG_LIB))
			{
				parseListArgument(arg.substring(ARG_LIB.length()), option_lib, ",");
			}
			else if (arg.startsWith(ARG_DEPS))
			{
				parseListArgument(arg.substring(ARG_DEPS.length()), option_deps, ",");
			}
			else if (arg.startsWith(ARG_APP_NAME))
			{
				option_app_name= arg.substring(ARG_APP_NAME.length());
			}
			else if (arg.startsWith(ARG_QX_MAIN))
			{
				option_qx_main= arg.substring(ARG_QX_MAIN.length());
			}
			else if (arg.equals(ARG_QX_DEBUG))
			{
				option_qx_debug= true;
			}
			else if (arg.equals(ARG_QX_NO_GENERATE))
			{
				option_qx_no_generate= true;
			}
			else if (arg.equals(ARG_VERSION))
			{
				printVersion();
			}
			else if (arg.startsWith(ARG_DEBUG))
			{
				option_debug= Log.Level.getLevel(arg.substring(ARG_DEBUG.length()));
			}
			else if (arg.equals(ARG_HELP))
			{
				longUsage();
			}
			else if (arg.startsWith(ARG_SKELETON))
			{
				if (option_skeleton != null)
					parseError("--skeleton can only be specified once");
				option_skeleton= arg.substring(ARG_SKELETON.length()).toLowerCase();
				// Obsolete arguments
			}
			else if (arg.startsWith(ARG_IPHONE_APP))
			{
				option_app_name= arg.substring(ARG_IPHONE_APP.length());
			}
			else if (arg.startsWith(ARG_QX_APP))
			{
				option_app_name= arg.substring(ARG_QX_APP.length());
			}
			else if (arg.equals(ARG_QUIET))
			{
				option_debug= Log.Level.ERROR;
			}
			else if (arg.equals(ARG_USE_JVM))
			{
				option_use_jvm= true;
			}
			else if (arg.equals(ARG_LOAD_DEPENDENCIES))
			{
				option_load_dependencies= true;
			}
			else if (arg.equals(ARG_DISABLE_LOAD_DEPENDENCIES))
			{
				option_disable_load_dependencies= true;
			}
			else if (arg.startsWith(ARG_REDLIST))
			{
				option_redlist= arg.substring(ARG_REDLIST.length());
			}
			else if (arg.startsWith(ARG_GREENLIST))
			{
				option_greenlist= arg.substring(ARG_GREENLIST.length()); // TODO: Allow multiple greenlists
			}
			else if (arg.startsWith(ARG_REFLECTION_CLASS_LIST))
			{
				option_reflection_class_list= arg.substring(ARG_REFLECTION_CLASS_LIST.length());
			}
			else if (arg.equals(ARG_ENABLE_TIMER))
			{
				option_enable_timer= true;
			}
			else if (arg.equals(ARG_ENABLE_REF_COUNTING))
			{
				option_enable_ref_counting= true;
			}
			else if (arg.startsWith(ARG_C_SOURCE_EXTENSION))
			{
				option_c_source_extension= arg.substring(ARG_C_SOURCE_EXTENSION.length());
			}
			else if (arg.equals(ARG_NO_CACHE))
			{
				option_no_cache= true;
			}
			else if (arg.startsWith(ARG_PROPERTY))
			{
				String value= arg.substring(ARG_PROPERTY.length());
				int equal= value.indexOf("=");
				if (equal < 1)
				{
					parseError("Unable to parse kay/value: " + value);
				}
				option_property.put(value.substring(0, equal).toLowerCase(), value.substring(equal + 1));
			}
			else if (arg.equals(ARG_XMLVM_NEW_IOS_API))
			{
				option_xmlvm_new_ios_api= true;
			}
			else if (arg.equals(ARG_MOBILE))
			{
				option_mobile= true;
			}
			else if (arg.length() == 0)
			{
				// Ignore empty arguments
			}
			else
			{
				parseError("Unknown parameter: " + arg);
			}
		}

		this.performSanityChecks= performSanityChecks;
		if (performSanityChecks)
		{
			// Sanity check command line arguments
			performSanityChecks();
		}

		// Add additional libraries the app might depend on.
		for (String library : option_deps)
		{
			if (!Libraries.addLibrary(library))
			{
				System.exit(-1);
			}
		}
	}

	private void performSanityChecks()
	{
		if (option_skeleton != null && option_target != Targets.NONE)
		{
			parseError("Only one argument of '--target' or '--skeleton' is allowed");
		}
		if (option_gen_native_skeletons && option_target != Targets.C && option_target != Targets.GENCWRAPPERS && option_target != Targets.CSHARP && option_target != Targets.GENCSHARPWRAPPERS)
		{
			parseError("--gen-native-skeletons only available for targets 'c','gen-c-wrappers','csharp' and 'gen-csharp-wrappers'.");
		}

		if ((option_target == Targets.POSIX || option_target == Targets.IPHONE || option_target == Targets.IPHONEC || option_target == Targets.WEBOS || option_skeleton != null) && option_app_name == null)
		{
			option_app_name= guessAppName();
			if (option_app_name == null)
				parseError("Required parameter: --app-name");
		}

		if ("".equals(option_skeleton)) // empty existing skeleton definition
			parseError("--skeleton option is empty");
		if (option_skeleton != null)
		{
			option_target= Targets.getTarget(option_skeleton + "template");
			if (option_target == null)
				parseError("Unknown skeleton: " + option_skeleton);
			// Clearing all inputs will force EmptyInputProcess to be used.
			option_in.clear();
		}

		if (option_target == Targets.NONE)
			option_target= Targets.XMLVM;

		if (option_lib.contains("android"))
		{
			option_lib.remove("android");
			if (option_target == Targets.IPHONE)
			{
				parseError("The ObjC-based Android to iPhone conversion is no longer supported.");
				return;
			}
			else if (option_target == Targets.IPHONEC)
			{
				option_target= Targets.IPHONECANDROID;
			}
			else if (option_target == Targets.WEBOS)
			{
			}
			else
			{
				parseError("--lib=android is meaningless when not --target=[iphone|iphonec|webos]");
			}
		}
		// // Due to default libraries provided, this check can not be performed
		// if (option_lib.size() > 0 && option_target != Targets.IPHONE)
		// parseError("--lib=" + option_lib.iterator().next() +
		// " is not supported for this target");

		// Only skeleton creation mode supports empty inputs.
		if ((option_skeleton == null || option_skeleton.equals("")) && option_in.isEmpty())
			parseError("Need at least one --in argument");
		if (option_target == Targets.QOOXDOO && option_app_name != null && option_qx_main == null)
			parseError("--target=qooxdoo with --qx-app requires --qx-main");
		if (option_debug == null)
			parseError("Unknown --debug level");

		// We need to enforce reference counting for these targets.
		if (option_target == Targets.OBJC || option_target == Targets.IPHONE)
		{
			if (!option_enable_ref_counting)
			{
				option_enable_ref_counting= true;
				Log.debug("Forcing " + ARG_ENABLE_REF_COUNTING + " for target " + option_target);
			}
		}

		if (option_target == Targets.IPHONE || option_target == Targets.IPHONEC || option_target == Targets.IPHONECANDROID)
		{
			option_c_source_extension= "m";
		}

		// Enables the dependency loading for the specified targets.
		if (option_target == Targets.POSIX || option_target == Targets.IPHONEC || option_target == Targets.IPHONECANDROID || option_target == Targets.CSHARP || option_target == Targets.WP7 || option_target == Targets.WP7ANDROID || option_target == Targets.SDLANDROID)
		{
			if (!option_disable_load_dependencies)
			{
				option_load_dependencies= true;
			}
		}
	}

	private static void parseListArgument(String argument, Set<String> option, String separator)
	{
		if (argument == null || option == null || separator == null)
		{
			return;
		}
		StringTokenizer tk= new StringTokenizer(argument, separator);
		while (tk.hasMoreTokens())
		{
			String entry= tk.nextToken().trim();
			if (!entry.equals(""))
			{
				boolean status= true;
				if (entry.startsWith("+"))
				{
					entry= entry.substring(1);
				}
				else if (entry.startsWith("-"))
				{
					status= false;
					entry= entry.substring(1);
				}
				if (!entry.equals(""))
				{
					if (status)
					{
						option.add(entry);
					}
					else
					{
						option.remove(entry);
					}
				}
			}
		}
	}

	public List<String> option_in()
	{
		return option_in;
	}

	public String option_out()
	{
		// Lazy definition of "smart" option_out parameter, so the warning will
		// appear only when needed
		if (option_out == null)
		{
			if (option_app_name == null)
				option_out= ".";
			else
			{
				option_out= option_app_name;
			}
			if (performSanityChecks)
			{
				Log.warn("Using '" + option_out + "' as output directory");
			}
		}
		return option_out;
	}

	public Set<String> option_resource()
	{
		return option_resource;
	}

	public Targets option_target()
	{
		return option_target;
	}

	public boolean option_gen_native_skeletons()
	{
		return option_gen_native_skeletons;
	}

	public String option_app_name()
	{
		return option_app_name;
	}

	public String option_qx_main()
	{
		return option_qx_main;
	}

	public boolean option_qx_debug()
	{
		return option_qx_debug;
	}

	public boolean option_qx_no_generate()
	{
		return option_qx_no_generate;
	}

	public Log.Level option_debug()
	{
		return option_debug;
	}

	public boolean option_use_jvm()
	{
		return option_use_jvm;
	}

	public boolean option_load_dependencies()
	{
		return option_load_dependencies;
	}

	public boolean option_disable_load_dependencies()
	{
		return option_disable_load_dependencies;
	}

	public String option_redlist()
	{
		return option_redlist;
	}

	public String option_greenlist()
	{
		return option_greenlist;
	}

	public String option_reflection_class_list()
	{
		return option_reflection_class_list;
	}

	public boolean option_enable_ref_counting()
	{
		return option_enable_ref_counting;
	}

	public boolean option_enable_timer()
	{
		return option_enable_timer;
	}

	public String option_c_source_extension()
	{
		return option_c_source_extension;
	}

	public boolean option_no_cache()
	{
		return option_no_cache;
	}

	public Set<String> option_lib()
	{
		return option_lib;
	}

	public Set<String> option_deps()
	{
		return option_deps;
	}

	public String option_property(String key)
	{
		return option_property.get(key);
	}

	public boolean option_xmlvm_new_ios_api()
	{
		return option_xmlvm_new_ios_api;
	}

	public boolean option_mobile()
	{
		return option_mobile;
	}

	private static void printText(String[] txt, PrintStream out)
	{
		for (int i= 0; i < txt.length; i++)
			out.println(txt[i]);
	}

	private String guessAppName()
	{
		if (option_out == null)
			return null;
		File outfile= new File(option_out).getAbsoluteFile();
		if (outfile.exists() && outfile.isFile())
			outfile= outfile.getParentFile();
		while (outfile != null && (outfile.getName().equals("..") || outfile.getName().equals(".") || outfile.getName().equals("/")))
			outfile= outfile.getParentFile();
		if (outfile == null)
			return null;
		String guess= outfile.getName();
		if (guess.isEmpty())
			return null;
		Log.warn("Using " + guess + " as application name");
		return guess;
	}
}
