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

package org.xmlvm.proc.out.templates;

import java.io.File;
import java.util.ArrayList;

import org.xmlvm.Log;
import org.xmlvm.main.Arguments;
import org.xmlvm.proc.BundlePhase1;
import org.xmlvm.proc.BundlePhase2;
import org.xmlvm.proc.XmlvmProcessImpl;
import org.xmlvm.proc.in.InputProcess.EmptyInputProcess;
import org.xmlvm.proc.out.OutputFile;

/**
 * Creates a template project.
 */
public abstract class TemplateOutputProcess extends XmlvmProcessImpl
{

	private static final String TEMPL_PROJNAME= "__PROJNAME__";
	private static final String TEMPL_PACKNAME= "__PACKNAME__";
	private static final String TEMPL_SAFENAME= "__SAFENAME__";
	private static final String TEMPL_TRIMSEED= "__XMLVMTRIMMERSEED__";
	private static final String TEMPL_XVMLSDK= "__XMLVMSDK__";
	//
	protected String safe_name;
	protected String pack_name;
	//
	protected final boolean migrate;

	public TemplateOutputProcess(Arguments arguments, boolean migrate)
	{
		super(arguments);
		addSupportedInput(EmptyInputProcess.class);
		this.migrate= migrate;
	}

	public boolean processPhase1(BundlePhase1 resources)
	{
		return true;
	}

	public boolean processPhase2(BundlePhase2 resources)
	{
		String projname= arguments.option_app_name();
		String outpath= arguments.option_out() + "/";
		safe_name= getSafeName(projname);
		if (safe_name.length() < 1)
		{
			Log.error("Project name should contain at least one ASCII letter");
			return false;
		}
		pack_name= "my." + safe_name;

		Log.debug("Size is " + getTemplateList().size());
		for (TemplateFile file : getTemplateList())
		{
			if (!addFile(file.source, file.dest, outpath + file.path, projname, file.mode, resources))
			{
				return false;
			}
		}
		return true;
	}

	private boolean addFile(String source, String dest, String path, String projname, TemplateFile.Mode mode, BundlePhase2 resources)
	{

		if (mode == TemplateFile.Mode.IGNORE)
		{
			return true;
		}

		String outpath= path + (path.equals("") ? "" : "/") + dest;
		File destfileref= new File(path, dest);
		if (destfileref.exists())
		{
			switch (mode)
			{
				case ABORT:
					Log.error("Destination already contains file " + source);
					return false;
				case KEEP:
					Log.debug("Keeping already existing file " + source);
					return true;
				case BACKUP:
					String backupname= dest + ".back";
					Log.warn("Renaming " + outpath + " to " + outpath + ".back");
					destfileref.renameTo(new File(path, backupname));
					break;
				case NEWFILE:
					Log.warn("Creating new version of file " + outpath);
					dest+= ".new";
					break;
				case DELETE:
					Log.warn("Deleting obsolete file " + outpath);
					destfileref.delete();
					return true;
				case OVERWRITE:
				default:
					Log.debug("Overwriting already existing file " + source);
					break;
			}
		}
		else
		{
			switch (mode)
			{
				case DELETE:
					return true;
			}
		}

		OutputFile file= new OutputFile();
		file.setFileName(dest);
		file.setLocation(path);
		Log.debug("Adding template file " + source + " to destination " + outpath);

		//        if (!file.setDataFromStream(JarUtil.getStream(getTemplateLocation() + source),
		//                System.currentTimeMillis())) {
		//            Log.error("Unable to find input file " + source);
		//            return false;
		//        }
		//        if (!source.endsWith(".png")) {
		//            file.setData(file.getDataAsString().replace(TEMPL_PROJNAME, projname)
		//                    .replace(TEMPL_PACKNAME, pack_name).replace(TEMPL_SAFENAME, safe_name));
		//        }
		//        if (source.endsWith(".properties") || source.endsWith(".classpath")) {
		//            file.setData(file.getDataAsString()
		//                    .replace(TEMPL_TRIMSEED, String.valueOf(new Random().nextLong()))
		//                    .replace(TEMPL_XVMLSDK, JarUtil.findSelfJar()));
		//        }
		resources.addOutputFile(file);
		return true;
	}

	private String getSafeName(String appname)
	{
		String safe= "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
		String others= "1234567890_";
		StringBuilder b= new StringBuilder();
		for (char c : appname.toCharArray())
		{
			if (b.length() > 0)
			{
				if (safe.indexOf(c) >= 0 || others.indexOf(c) >= 0)
				{
					b.append(c);
				}
			}
			else
			{
				if (safe.indexOf(c) >= 0)
				{
					b.append(c);
				}
			}
		}
		return b.toString().toLowerCase();
	}

	abstract ArrayList<TemplateFile> getTemplateList();

	abstract String getTemplateLocation();
}
