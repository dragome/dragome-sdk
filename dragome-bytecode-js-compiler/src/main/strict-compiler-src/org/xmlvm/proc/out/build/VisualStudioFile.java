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

package org.xmlvm.proc.out.build;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.xmlvm.main.Arguments;
import org.xmlvm.proc.BundlePhase2;
import org.xmlvm.proc.out.OutputFile;
import org.xmlvm.util.universalfile.UniversalFile;
import org.xmlvm.util.universalfile.UniversalFileCreator;

public class VisualStudioFile extends BuildFile
{

	private static final String VISUAL_STUDIO_IN_JAR_RESOURCE= "/wp7";
	private static final String VISUAL_STUDIO_PATH= "var/wp7";

	/* Templates */
	private static final String TEMPL_APPNAME= "__APP_NAME__";
	private static final String TEMPL_SRCLIST= "__SRC_LIST__";
	private static final String TEMPL_RESOURCES= "__RESOURCES__";

	private BundlePhase2 bundle;

	/**
	 * 
	 */
	public VisualStudioFile(BundlePhase2 bundle)
	{
		this.bundle= bundle;
	}

	public OutputFile composeBuildFiles(Arguments arguments)
	{
		String projname= arguments.option_app_name();

		// Search and load VisualStudio template.
		try
		{
			VisualStudioProject proj= new VisualStudioProject(projname, bundle);
			proj.finalizeObject(arguments);
		}
		catch (IOException ex)
		{
			ex.printStackTrace();
		}

		return null;
	}

	private class VisualStudioProject
	{

		/* */
		UniversalFile data;
		String name;

		private String source_list;
		private String resource_list;
		private ArrayList<String> placeholderFiles;
		private BundlePhase2 bundle;

		private VisualStudioProject(String name, BundlePhase2 bundle) throws IOException
		{
			data= UniversalFileCreator.createDirectory(VISUAL_STUDIO_IN_JAR_RESOURCE, VISUAL_STUDIO_PATH);
			if (data == null)
				throw new RuntimeException("Visual studio template not found");

			if (bundle == null)
				throw new RuntimeException("Null files given to VisualStudioProject");
			this.bundle= bundle;

			this.placeholderFiles= new ArrayList<String>();
			placeholderFiles.add("AssemblyInfo.cs");
			placeholderFiles.add("WMAppManifest.xml");
			placeholderFiles.add("__project__.csproj");
			placeholderFiles.add("__project__.sln");

			this.name= name;
		}

		private void finalizeObject(Arguments arguments)
		{
			constructSources();
			constructResources();

			for (UniversalFile file : data.listFilesRecursively())
			{
				OutputFile outputFile= null;
				if (containsPlaceholder(file))
				{
					String content= file.getFileAsString();
					content= content.replace(TEMPL_APPNAME, name);
					content= content.replace(TEMPL_SRCLIST, source_list);
					content= content.replace(TEMPL_RESOURCES, resource_list);

					outputFile= new OutputFile(content.getBytes());
				}
				else
				{
					outputFile= new OutputFile(file);
				}

				//Path
				String path= file.getRelativePath(data.getAbsolutePath());
				if (path.indexOf(File.separatorChar) >= 0)
				{
					path= path.substring(0, path.lastIndexOf(File.separator));
					path= path.replaceAll("__project__", name);
				}
				else
				{
					path= "";
				}
				outputFile.setLocation(arguments.option_out() + File.separator + path);

				//Name
				if (file.getName().contains("__project__"))
				{
					String newName= file.getName().replaceAll("__project__", name);
					outputFile.setFileName(newName);
				}
				else
				{
					outputFile.setFileName(file.getName());
				}

				bundle.addOutputFile(outputFile);
			}
		}

		private boolean containsPlaceholder(UniversalFile file)
		{
			return placeholderFiles.contains(file.getName());
		}

		private void constructSources()
		{
			StringBuilder compiles= new StringBuilder();
			Set<String> inserted= new HashSet<String>();

			for (OutputFile file : bundle.getOutputFiles())
			{
				if (file.getFileName().endsWith(".cs") && !inserted.contains(file.getFullPath()))
				{
					String compile= "    <Compile Include=\"";
					compile+= file.getFullPath();
					compile+= "\" />";
					compiles.append(compile + "\n");
					inserted.add(file.getFullPath());
				}
			}

			this.source_list= compiles.toString();
		}

		private void constructResources()
		{
			StringBuilder compiles= new StringBuilder();

			for (OutputFile file : bundle.getOutputFiles())
			{
				if (!file.getFileName().endsWith(".cs"))
				{
					String compile= "    <Content Include=\"";
					compile+= file.getFullPath();
					compile+= "\">\n" + "      <CopyToOutputDirectory>PreserveNewest</CopyToOutputDirectory>\n" + "    </Content>";
					compiles.append(compile + "\n");
				}
			}

			this.resource_list= compiles.toString();
		}

	}

}
