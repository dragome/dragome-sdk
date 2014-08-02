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

package org.xmlvm.proc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.xmlvm.Log;
import org.xmlvm.util.universalfile.UniversalFile;
import org.xmlvm.util.universalfile.UniversalFileCreator;

/**
 * This cache is about storing and retrieving resources that are produced by
 * XMLVM processes.
 */
public class ResourceCache
{
	private static final String TAG= ResourceCache.class.getSimpleName();
	private static final String cachePath= ".cache/pcache/";
	private static Map<String, ResourceCache> caches= new HashMap<String, ResourceCache>();
	private final String processName;
	private Map<String, UniversalFile> cachedResources= new HashMap<String, UniversalFile>();
	private Map<String, byte[]> cachedResourceBytes= new HashMap<String, byte[]>();

	/**
	 * Returns a cache instance for the process with the given name.
	 * 
	 * @param processName
	 *            The name of the process which wants to access its cache. This
	 *            is usually the full class name.
	 * @return The {@link ResourceCache} that can be used to store and retrieve
	 *         resources produces by the process from cache.
	 */
	public static ResourceCache getCache(String processName)
	{
		if (!caches.containsKey(processName))
		{
			caches.put(processName, new ResourceCache(processName));
		}
		return caches.get(processName);
	}

	/**
	 * Creates a resource cache instance for the process with the given name.
	 */
	private ResourceCache(String processName)
	{
		this.processName= processName;
		init();
	}

	/**
	 * Returns whether the given resource exists in the cache.
	 * 
	 * @param resourceName
	 *            The name of the resource to retrieve from cache.
	 * @param lastModified
	 *            The time stamp of when the resource was changed the last time.
	 */
	public boolean contains(String resourceName, long lastModified)
	{
		return cachedResources.containsKey(createCacheKey(resourceName, lastModified));
	}

	/**
	 * Puts the given data into the cache under the given resource name.
	 * 
	 * @param resourceName
	 *            The name of the resource to retrieve from cache.
	 * @param lastModified
	 *            The time stamp of when the resource was changed the last time.
	 */
	public void put(String resourceName, long lastModified, byte[] data)
	{
		File file= new File(getCacheFileName(resourceName, lastModified));
		file.getParentFile().mkdirs();

		try
		{
			FileOutputStream fos= new FileOutputStream(file);
			fos.write(data);
			fos.close();
		}
		catch (FileNotFoundException e)
		{
			Log.error(TAG, e.getMessage());
			return;
		}
		catch (IOException e)
		{
			Log.error(TAG, e.getMessage());
			return;
		}

		String cacheKey= createCacheKey(resourceName, lastModified);
		cachedResources.put(cacheKey, UniversalFileCreator.createFile(file));
		cachedResourceBytes.put(cacheKey, data);
	}

	/**
	 * Returns the contents of the cache for the given resource.
	 * 
	 * @param resourceName
	 *            The name of the resource to retrieve from cache.
	 * @param lastModified
	 *            The time stamp of when the resource was changed the last time.
	 * @return The data or <code>null</code>, if it doesn't exist.
	 */
	public byte[] get(String resourceName, long lastModified)
	{
		String cacheKey= createCacheKey(resourceName, lastModified);

		// First check whether we have the bytes cached.
		if (cachedResourceBytes.containsKey(cacheKey))
		{
			return cachedResourceBytes.get(cacheKey);
		}

		// If we don't have the bytes cached in memory, try to read it from
		// disk.
		UniversalFile file= cachedResources.get(cacheKey);
		if (file == null)
		{
			return null;
		}
		byte[] bytes= file.getFileAsBytes();
		cachedResourceBytes.put(cacheKey, bytes);
		return bytes;
	}

	private void init()
	{
		String processCachePath= getProcessCachePath();
		File processCache= new File(processCachePath);
		if (!processCache.exists())
		{
			return;
		}

		if (!processCache.isDirectory())
		{
			Log.error(TAG, "Cache path is not a directory: " + processCachePath);
			return;
		}

		for (File file : processCache.listFiles())
		{
			if (file.isFile())
			{
				cachedResources.put(file.getName(), UniversalFileCreator.createFile(file));
			}
		}
	}

	private String getCacheFileName(String resourceName, long lastModified)
	{
		String cacheKey= createCacheKey(resourceName, lastModified);
		return getProcessCachePath() + File.separator + cacheKey;

	}

	private String getProcessCachePath()
	{
		return cachePath + processName;
	}

	private static String createCacheKey(String resourceName, long lastModified)
	{
		return "C" + resourceName.replace("/", "-").replace("\\", "-").replace(":", "-") + "-" + lastModified;
	}
}
