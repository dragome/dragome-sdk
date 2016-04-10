/*******************************************************************************
 * Copyright (c) 2011-2014 Fernando Petrola
 *
 * This file is part of Dragome SDK.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package com.dragome.web.serverside.compile.watchers;

import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dragome.commons.compiler.classpath.Classpath;
import com.dragome.web.helpers.serverside.DragomeCompilerLauncher;

public class DirectoryWatcher
{
	ReschedulableTimer reschedulableTimer= new ReschedulableTimer();

	private final WatchService watcher;
	private final Map<WatchKey, Path> keys;
	private final boolean recursive;
	private boolean trace= false;

	public static long lastCompilation;

	private Classpath classPath;

	private String path;

	private static String lines;

	@SuppressWarnings("unchecked")
	static <T> WatchEvent<T> cast(WatchEvent<?> event)
	{
		return (WatchEvent<T>) event;
	}

	/**
	 * Register the given directory with the WatchService
	 */
	private void register(Path dir) throws IOException
	{
		WatchKey key= dir.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
		if (trace)
		{
			Path prev= keys.get(key);
			if (prev == null)
			{
				System.out.format("register: %s\n", dir);
			}
			else
			{
				if (!dir.equals(prev))
				{
					System.out.format("update: %s -> %s\n", prev, dir);
				}
			}
		}
		keys.put(key, dir);
	}

	/**
	 * Register the given directory, and all its sub-directories, with the
	 * WatchService.
	 */
	private void registerAll(final Path start) throws IOException
	{
		// register directory and sub-directories
		Files.walkFileTree(start, new SimpleFileVisitor<Path>()
		{
			@Override
			public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException
			{
				register(dir);
				return FileVisitResult.CONTINUE;
			}
		});
	}

	/**
	 * Creates a WatchService and registers the given directory
	 */
	DirectoryWatcher(Path dir, boolean recursive, Classpath classPath, String path) throws IOException
	{
		this.classPath= classPath;
		this.path= path;
		this.watcher= FileSystems.getDefault().newWatchService();
		this.keys= new HashMap<WatchKey, Path>();
		this.recursive= recursive;

		if (recursive)
		{
			System.out.format("Scanning %s ...\n", dir);
			registerAll(dir);
			//			System.out.println("Done.");
		}
		else
		{
			register(dir);
		}

		// enable trace after initial registration
		this.trace= true;
	}

	/**
	 * Process all events for keys queued to the watcher
	 */
	void processEvents()
	{
		for (;;)
		{

			// wait for key to be signalled
			WatchKey key;
			try
			{
				key= watcher.take();
			}
			catch (InterruptedException x)
			{
				return;
			}

			System.out.print(lines);
			lines= "";

			Path dir= keys.get(key);
			if (dir == null)
			{
				System.err.println("WatchKey not recognized!!");
				continue;
			}

			for (WatchEvent<?> event : key.pollEvents())
			{
				WatchEvent.Kind kind= event.kind();

				// TBD - provide example of how OVERFLOW event is handled
				if (kind == OVERFLOW)
				{
					continue;
				}

				// Context for directory entry event is the file name of entry
				WatchEvent<Path> ev= cast(event);
				Path name= ev.context();
				Path child= dir.resolve(name);

				// print out event
				System.out.format("%s: %s\n", humanReadable(event.kind().name()), child.getName(child.getNameCount() - 1));

				if (reschedulableTimer.isScheduling())
					reschedulableTimer.reschedule(500);
				else
					reschedulableTimer.schedule(new Runnable()
					{
						public void run()
						{
							compile(classPath, path);
						}

					}, 500);

				// if directory is created, and watching recursively, then
				// register it and its sub-directories
				if (recursive && (kind == ENTRY_CREATE))
				{
					try
					{
						if (Files.isDirectory(child, NOFOLLOW_LINKS))
						{
							registerAll(child);
						}
					}
					catch (IOException x)
					{
						// ignore to keep sample readbale
					}
				}
			}

			// reset key and remove from set if directory no longer accessible
			boolean valid= key.reset();
			if (!valid)
			{
				keys.remove(key);

				// all directories are inaccessible
				if (keys.isEmpty())
				{
					break;
				}
			}
		}
	}

	private String humanReadable(String string)
	{
		return string.replace(ENTRY_MODIFY.name(), "file modified").replace(ENTRY_DELETE.name(), "file deleted").replace(ENTRY_CREATE.name(), "file created");
	}

	static void usage()
	{
		System.err.println("usage: java WatchDir [-r] dir");
		System.exit(-1);
	}

	public static void startWatching(String[] args, Classpath classPath, String path)
	{
		try
		{
			if (args.length == 0 || args.length > 2)
				usage();
			boolean recursive= false;
			int dirArg= 0;
			if (args[0].equals("-r"))
			{
				if (args.length < 2)
					usage();
				recursive= true;
				dirArg++;
			}

			compile(classPath, path);
			Path dir= Paths.get(args[dirArg]);
			new DirectoryWatcher(dir, recursive, classPath, path).processEvents();
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}

	private static void compile(Classpath classPath, String path)
	{
		System.out.println("");
		System.out.println("----------------------------------------------------------------------");
		DragomeCompilerLauncher.compileWithMainClass(classPath, path);
		lastCompilation= System.currentTimeMillis();
		System.out.println("----------------------------------------------------------------------");
		System.out.println("DRAGOME BUILD SUCCESSFUL: js application ready");
		System.out.println("----------------------------------------------------------------------");
		System.out.println("Finished at: " + new SimpleDateFormat("EEE MMM d HH:mm:ss z YYYY").format(new Date()));
		System.out.println("======================================================================");
		lines= "\n\n\n\n==================Dragome has detected these changes==================\n";
	}
}
