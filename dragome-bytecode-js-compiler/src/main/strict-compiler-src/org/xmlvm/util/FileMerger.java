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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.xmlvm.Log;
import org.xmlvm.proc.out.OutputFile;
import org.xmlvm.util.universalfile.UniversalFile;

/**
 * This is a utility function to merge two files. It is not a traditional
 * merging tool, though. Instead it uses files that have section which are
 * described by special XMLVM markup.
 * <p>
 * Sections start with {@code //XMLVM_BEGIN} and end with {@code //XMLVM_END}.
 * These markers have to be the first non-whitespace characters on the line.
 * They can be followed by an arbitrary string, which is ignored for the end
 * marker. For the begin marker though this trailing string is a key for this
 * section. Sections must have unique keys within one file.
 * <p>
 * This utility takes file pairs which are merged like this: All sections are
 * extracted from the source and destination file and stored with their key. If
 * a section is contained in both source- and destination file then the contents
 * of the section in the source file replace the contents of that same section
 * in the destination file.
 */
public class FileMerger
{
	private static final String TAG= FileMerger.class.getSimpleName();
	private static final String BEGIN_MARKER= "//XMLVM_BEGIN";
	private static final String END_MARKER= "//XMLVM_END";

	private final Collection<OutputFile> skeletons;
	private final String skeletonBasePath;
	private final Collection<UniversalFile> implementations;
	private final String implementationBasePath;

	/**
	 * Instantiates a new FileMerger.
	 * 
	 * @param skeletons
	 *            These are the files that contain the general structure of the
	 *            new files. Their content will be enriched with matching
	 *            implementations.
	 * @param skeletonBasePath
	 *            The base path in which the skeleton files are (or will be).
	 *            This is important so that the relative path can be used to
	 *            correctly as the key to match implementation files.
	 * @param implementations
	 *            These are the files that contain manual implementations that
	 *            should be preserved for all sections that are contained in the
	 *            matching wrapper file.
	 * @param implementationBasePath
	 *            The path in which the implementation files are in. This is
	 *            important so that the relative path can be used to match
	 *            skeleton files.
	 */
	public FileMerger(Collection<OutputFile> skeletons, String skeletonBasePath, Collection<UniversalFile> implementations, String implementationBasePath)
	{
		this.skeletons= skeletons;
		this.skeletonBasePath= skeletonBasePath;
		this.implementations= implementations;
		this.implementationBasePath= implementationBasePath;
	}

	/**
	 * Instantiates a new FileMerger. Use this constructor if relatives paths
	 * should not be included in the matching of files. Only the name of the
	 * file will be matched. A typical use case for this is when all skeleton
	 * files are in only one directory and the implementation files are in one
	 * directory without sub-directories as well.
	 * 
	 * @param skeletons
	 *            These are the files that contain the general structure of the
	 *            new files. Their content will be enriched with matching
	 *            implementations.
	 * @param implementations
	 *            These are the files that contain manual implementations that
	 *            should be preserved for all sections that are contained in the
	 *            matching wrapper file.
	 */
	public FileMerger(Collection<OutputFile> skeletons, Collection<UniversalFile> implementations)
	{
		this.skeletons= skeletons;
		this.skeletonBasePath= null;
		this.implementations= implementations;
		this.implementationBasePath= null;
	}

	public void process()
	{
		// Extracts all existing sections from all files and keys them by
		// relative file name.
		Map<String, Map<String, String>> existingSections= extractAllSections();

		// We patch the existing content into the skeletons.
		injectAllSections(existingSections);
	}

	/**
	 * From the given destination directory, this method extracts all blocks
	 * that are enclosed within XMLVM_BEGIN and XMLVM_END blocks. It keys them
	 * first by file name and then by section name.
	 * 
	 * @param implementations
	 *            The files that contain implementations that should be
	 *            preserved.
	 * @param basePath
	 *            The base path of the implementation files. This is used to
	 *            calculate the relative path of the files as a key.
	 * @return The section contents keyed by filename and section name.
	 */
	private Map<String, Map<String, String>> extractAllSections()
	{
		Map<String, Map<String, String>> output= new HashMap<String, Map<String, String>>();
		for (UniversalFile existingFile : implementations)
		{
			if (existingFile.isFile() && !existingFile.isEmpty())
			{
				String key= implementationBasePath != null ? existingFile.getRelativePath(implementationBasePath) : existingFile.getName();
				Map<String, String> sections= extractSections(existingFile);
				output.put(key, sections);
			}
		}
		return output;
	}

	/**
	 * Extracts the sections that are enclosed within XMLVM_BEGIN and XMLVM_END
	 * blocks from the given file.
	 * 
	 * @param file
	 *            the file to parse
	 * @return A map that contains the contents of the blocks, keyed by block
	 *         name
	 */
	private Map<String, String> extractSections(UniversalFile file)
	{
		Map<String, String> sections= new HashMap<String, String>();
		BufferedReader reader= new BufferedReader(new StringReader(file.getFileAsString()));

		String line;
		StringBuilder section= null;
		String currentKey= null;
		try
		{
			while ((line= reader.readLine()) != null)
			{
				if (line.contains(BEGIN_MARKER))
				{
					section= new StringBuilder();
					currentKey= line.trim();
					continue;
				}

				if (line.contains(END_MARKER))
				{
					if (section == null)
					{
						Log.error(TAG, "Found end marker without matching starting marker: " + line);
						continue;
					}
					sections.put(currentKey, section.toString());
					section= null;
				}

				if (section != null)
				{
					section.append(line);
					section.append('\n');
				}
			}
		}
		catch (IOException e)
		{
			Log.error(TAG, "Could not read file: " + e.getMessage());
		}
		return sections;
	}

	/**
	 * Injects the given sections to the given files, if their relative file
	 * name matches and they contain matching sections.
	 * 
	 * @param sections
	 *            the previously extracted section contents which are keyed by
	 *            relative file name and section name
	 */
	private void injectAllSections(Map<String, Map<String, String>> sections)
	{
		for (OutputFile file : skeletons)
		{
			String key= skeletonBasePath != null ? file.getRelativePath(skeletonBasePath) : file.getFileName();
			if (sections.containsKey(key))
			{
				Log.debug(TAG, "Injecting contents into " + file.getFullPath());
				injectSections(sections.get(key), file);
			}
		}
	}

	/**
	 * Injects the given section contents, keyed by section name, into the given
	 * file. If the file has sections that match the ones in the given map,
	 * their content is replaced.
	 * 
	 * @param sections
	 *            the section contents, keyed by section name
	 * @param file
	 *            The file to parse and possibly replace the section contents
	 *            in.
	 */
	private void injectSections(Map<String, String> sections, OutputFile file)
	{
		BufferedReader reader= new BufferedReader(new StringReader(file.getDataAsString()));

		try
		{
			StringBuilder fileContent= new StringBuilder();
			String line;
			boolean doNotAdd= false;
			while ((line= reader.readLine()) != null)
			{
				if (!doNotAdd)
				{
					fileContent.append(line);
					fileContent.append('\n');
				}

				if (line.contains(BEGIN_MARKER))
				{
					String sectionKey= line.trim();
					if (sections.containsKey(sectionKey))
					{
						fileContent.append(sections.get(sectionKey));
						doNotAdd= true;
					}
				}

				if (line.contains(END_MARKER))
				{
					if (doNotAdd)
					{
						fileContent.append(line);
						fileContent.append('\n');
					}
					doNotAdd= false;
				}
			}
			file.setData(fileContent.toString());
		}
		catch (IOException e)
		{
			Log.error(TAG, "Could not read file: " + e.getMessage());
		}
	}
}
