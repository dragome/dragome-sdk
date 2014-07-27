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

package org.xmlvm.util.universalfile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;

import org.xmlvm.Log;

/**
 * The abstract universal file class. This class may represent an actual file
 * system file or directory or a file or JAR archive within a JAR resource.
 */
public abstract class UniversalFile
{

	private static final String TAG= UniversalFile.class.getSimpleName();

	/**
	 * Returns the name of this file. The returned value is analog to
	 * {@link File#getName()}.
	 */
	public String getName()
	{
		String path= getAbsolutePath();

		if (path.isEmpty())
		{
			return "";
		}

		// Check for both Unix- and Window-style separators.
		int startAt= Math.max(path.lastIndexOf('/'), path.lastIndexOf('\\')) + 1;
		if (startAt <= 0)
		{
			return "";
		}
		return path.substring(startAt);
	}

	/**
	 * Returns the absolute path of this {@link UniversalFile}. The returned
	 * value is analog to {@link File#getAbsolutePath()}.
	 */
	public abstract String getAbsolutePath();

	public String getRelativePath(String basePath)
	{
		String fullPath= getAbsolutePath();

		if (!fullPath.startsWith(basePath))
		{
			Log.error("'" + basePath + "' is not a base path of '" + fullPath);
			return null;
		}
		String result= fullPath.substring(basePath.length());
		if (result.startsWith(File.separator))
		{
			result= result.substring(1);
		}
		return result;
	}

	/**
	 * Returns whether this file is a directory.
	 */
	public abstract boolean isDirectory();

	/**
	 * Returns whether this file is a file.
	 */
	public abstract boolean isFile();

	/**
	 * Returns whether this file exists.
	 */
	public abstract boolean exists();

	/**
	 * Returns the timestamp when this file has been changed the last time.
	 */
	public abstract long getLastModified();

	/**
	 * If this universal file is a directory, this returns the files contained
	 * in it. This only lists file on one level, and can thus return files and
	 * sub-directories.
	 */
	public abstract UniversalFile[] listFiles();

	/**
	 * If this file entry is a directory, this returns the files contained in
	 * it, if they match the given filter.
	 * 
	 * @param filter
	 *            a filter for selecting a particular set of files from the list
	 */
	public UniversalFile[] listFiles(UniversalFileFilter filter)
	{
		UniversalFile[] allFiles= listFiles();
		List<UniversalFile> result= new ArrayList<UniversalFile>();

		for (UniversalFile file : allFiles)
		{
			if (filter.accept(file))
			{
				result.add(file);
			}
		}

		return result.toArray(new UniversalFile[0]);
	}

	/**
	 * If this universal file is is a directory, it returns a list of all files
	 * contained in it and within all sub-directories. This method will not
	 * return any directories.
	 * 
	 * @param filter
	 *            a filter for selecting a particular set of files from the list
	 */
	public UniversalFile[] listFilesRecursively(UniversalFileFilter filter)
	{
		UniversalFile[] all= listFilesRecursively();
		List<UniversalFile> result= new ArrayList<UniversalFile>();

		for (UniversalFile file : all)
		{
			if (filter.accept(file))
			{
				result.add(file);
			}
		}
		return result.toArray(new UniversalFile[0]);
	}

	/**
	 * If this universal file is is a directory, it returns a list of all files
	 * contained in it and within all sub-directories. This method will not
	 * return any directories.
	 */
	public UniversalFile[] listFilesRecursively()
	{
		if (!isDirectory())
		{
			return new UniversalFile[0];
		}

		List<UniversalFile> result= new ArrayList<UniversalFile>();
		for (UniversalFile file : listFiles())
		{
			if (file.isFile())
			{
				result.add(file);
			}
			else if (file.isDirectory())
			{
				result.addAll(Arrays.asList(file.listFilesRecursively()));
			}
		}
		return result.toArray(new UniversalFile[0]);
	}

	/**
	 * If this universal file is a directory, this method returns a file or
	 * directory contained in it that matches the given name. Else, returns
	 * null.
	 * 
	 * @param name
	 *            the name of the file or directory.
	 * @return the matched universal file or <code>null</code>.
	 */
	public UniversalFile getEntry(String name)
	{
		if (!isDirectory())
		{
			return null;
		}
		UniversalFile[] allFiles= listFiles();

		for (UniversalFile file : allFiles)
		{
			if (file.getName().equals(name))
			{
				return file;
			}
		}
		return null;
	}

	/**
	 * Reads the file represented by this source and returns it as bytes.
	 * <p>
	 * This should only be called if {@link #isDirectory()} is {@code false}.
	 * 
	 * @return The contents of the file as bytes.
	 */
	public abstract byte[] getFileAsBytes();

	/**
	 * Reads the file represented by this resource and returns it as a String.
	 * <p>
	 * This should only be called if {@link #isDirectory()} is {@code false}.
	 * 
	 * @return The contents of the file as String.
	 */
	public abstract String getFileAsString();

	/**
	 * Saves this universal file to the given file system path (in this case the
	 * path should contains the file name itself, too). If it represents a file,
	 * it stores the file, if it is a directory, it stores multiple files
	 * recursively, preserving the original file hierarchy.
	 * 
	 * @param path
	 *            the destination to where this resource is copied to
	 * @return Whether the operation was successful.
	 */
	public boolean saveAs(String path)
	{
		if (isFile())
		{
			return saveFileAs(path);
		}
		else if (isDirectory())
		{
			return saveDirectoryAs(path);
		}
		return false;
	}

	/**
	 * Archives this universal file to the given archive file. If the archive
	 * file already exists, it will be replaced.
	 * <p>
	 * If this universal file is a directory, this stores all files recursively
	 * in the given destination archive file. Paths in the destination are
	 * relative to the this file.
	 * 
	 * @param destination
	 *            the archive in which the file will be stored
	 * @param pathPrefix
	 *            the path inside the archive, where the files are put into
	 * @return Whether the operation was successful.
	 */
	public boolean archiveTo(String destination, String pathPrefix)
	{
		if (!pathPrefix.isEmpty() && !pathPrefix.endsWith("/"))
		{
			pathPrefix+= "/";
		}
		if (isFile())
		{
			return archiveFileTo(destination, pathPrefix);
		}
		else if (isDirectory())
		{
			return archiveDirectoryTo(destination, pathPrefix);
		}
		return false;
	}

	/**
	 * If this is a file, this function returns whether it doesn't exist or has
	 * no content. This always returns <code>true</code> for directories, if
	 * they exist.
	 */
	public boolean isEmpty()
	{
		if (!exists())
		{
			return true;
		}
		if (isDirectory())
		{
			return false;
		}
		return getFileAsBytes().length == 0;
	}

	/**
	 * Returns whether the contents of this file is different from the given
	 * destination. If the destination doesn't exist, this method returns
	 * <code>true</code>.
	 */
	public boolean isDifferentFromExisting(String destinationPath)
	{
		UniversalFile destination= UniversalFileCreator.createFile(new File(destinationPath));
		if (!destination.exists() || !destination.isFile())
		{
			return true;
		}

		byte[] newData= getFileAsBytes();
		byte[] existingData= destination.getFileAsBytes();

		if (newData.length != existingData.length)
		{
			return true;
		}

		for (int i= 0; i < newData.length; ++i)
		{
			if (newData[i] != existingData[i])
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * Stores this universal file to the given file system path (which includes
	 * the file name itself).
	 * 
	 * @param path
	 *            the destination to where this resource is copied to
	 * @return Whether the operation was successful.
	 */
	private boolean saveFileAs(String path)
	{
		try
		{
			// Make sure the destination directory exists.
			File parent= (new File(path)).getParentFile();
			if (!parent.exists() && !parent.mkdirs())
			{
				return false;
			}
			FileOutputStream outputStream= new FileOutputStream(path);
			outputStream.write(getFileAsBytes());
			outputStream.close();
			return true;
		}
		catch (FileNotFoundException e)
		{
			Log.error(TAG, "Could not save file at: " + path + "(" + e.getMessage() + ")");
		}
		catch (IOException e)
		{
			Log.error(TAG, "Could not save file at: " + path + "(" + e.getMessage() + ")");
		}
		return false;
	}

	/**
	 * If this universal file is a directory, this stores all the files
	 * recursively in the given destination directory, preserving the directory
	 * structure.
	 * 
	 * @param destination
	 *            the directory where the files should be stored
	 * @return Whether the operation was successful.
	 */
	private boolean saveDirectoryAs(String destination)
	{
		File destinationFile= new File(destination);

		if (destinationFile.exists() && destinationFile.isFile())
		{
			Log.error(TAG, "Could not copy files to " + destination + ". Destination is a file.");
			return false;
		}

		// Lets make sure we have a properly formatted path to work with.
		String destinationStr= destinationFile.getAbsolutePath();

		for (UniversalFile file : listFiles())
		{
			if (file.isFile())
			{
				file.saveAs(destinationStr + File.separatorChar + file.getName());
			}
			else if (file.isDirectory())
			{
				String absolutePath= file.getAbsolutePath();
				String directoryName= absolutePath.substring(absolutePath.lastIndexOf(File.separatorChar) + 1);
				file.saveAs(destinationStr + File.separatorChar + directoryName);
			}
		}
		return true;
	}

	/**
	 * If this universal file is a file, this will store the file in the given
	 * archive. If the archive already exists, it will be replaced.
	 * 
	 * @param destination
	 *            the archive in which the file will be stored
	 * @param pathPrefix
	 *            the path inside the archive, where the files are put into
	 * @return Whether the operation was successful.
	 */
	private boolean archiveFileTo(String destination, String pathPrefix)
	{
		File destinationFile= prepareDestinationArchive(destination);
		if (destinationFile == null)
		{
			return false;
		}

		try
		{
			JarOutputStream outputStream= new JarOutputStream(new FileOutputStream(destinationFile));
			outputStream.putNextEntry(new ZipEntry(pathPrefix + getName()));
			outputStream.write(getFileAsBytes());
			outputStream.close();
			return true;
		}
		catch (FileNotFoundException e)
		{
			Log.error(TAG, "Could not create JarOutputStream: " + e.getMessage());
		}
		catch (IOException e)
		{
			Log.error(TAG, "Could not create JarOutputStream: " + e.getMessage());
		}
		return false;
	}

	/**
	 * If this universal file is a directory, this stores all files recursively
	 * in the given destination archive file. Paths in the destination are
	 * relative to the this file. If the archive already exists, it will be
	 * replaced.
	 * 
	 * @param destination
	 *            the archive in which the files will be stored
	 * @param pathPrefix
	 *            the path inside the archive, where the files are put into
	 * @return Whether the operation was successful.
	 */
	private boolean archiveDirectoryTo(String destination, String pathPrefix)
	{
		File destinationFile= prepareDestinationArchive(destination);
		if (destinationFile == null)
		{
			return false;
		}

		JarOutputStream outputStream;
		try
		{
			outputStream= new JarOutputStream(new FileOutputStream(destinationFile));
		}
		catch (FileNotFoundException e)
		{
			Log.error(TAG, "Could not create JarOutputStream: " + e.getMessage());
			return false;
		}
		catch (IOException e)
		{
			Log.error(TAG, "Could not create JarOutputStream: " + e.getMessage());
			return false;
		}
		String basePath= getAbsolutePath();
		UniversalFile[] filesToArchive= listFilesRecursively();
		try
		{
			for (UniversalFile fileToArchive : filesToArchive)
			{
				String path= fileToArchive.getAbsolutePath();

				// Paranoia check
				if (!path.startsWith(basePath))
				{
					Log.error(TAG, "Internal error: File in directory has wrong path:");
					Log.error(TAG, "Base path: " + basePath);
					Log.error(TAG, "File path: " + path);
					return false;
				}
				path= path.substring(basePath.length() + 1);
				String entryPath= (pathPrefix + path).replace('\\', '/');
				outputStream.putNextEntry(new ZipEntry(entryPath));
				outputStream.write(fileToArchive.getFileAsBytes());
			}
			outputStream.close();
			return true;
		}
		catch (IOException e)
		{
			Log.error(TAG, "Could not write to archive: " + e.getMessage());
		}
		return false;
	}

	/**
	 * Prepares the destination archive file. If the file exists, it will be
	 * deleted.
	 * 
	 * @param destination
	 *            the destination archive file
	 * @return The {@link File} object, if the operation was successful, or
	 *         <code>null</code> otherwise.
	 */
	private static File prepareDestinationArchive(String destination)
	{
		File destinationFile= new File(destination);
		if (destinationFile.exists())
		{
			if (destinationFile.isDirectory())
			{
				Log.error(TAG, "Cannot write archive, destination is a directory: " + destination);
				return null;
			}
			else
			{
				boolean deleted= destinationFile.delete();
				if (!deleted)
				{
					Log.error(TAG, "Unable to delete existing file: " + destination);
					return null;
				}
			}
		}
		File parentDirectory= destinationFile.getParentFile();
		if (!parentDirectory.exists())
		{
			boolean created= parentDirectory.mkdirs();
			if (!created)
			{
				Log.error(TAG, "Could not create directory: " + parentDirectory.getAbsolutePath());
				return null;
			}
		}
		return destinationFile;
	}

	/**
	 * Returns the absolute path of this file.
	 */

	public String toString()
	{
		return getAbsolutePath();
	}
}
