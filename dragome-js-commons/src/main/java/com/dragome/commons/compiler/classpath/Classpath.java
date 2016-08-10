package com.dragome.commons.compiler.classpath;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import com.dragome.commons.compiler.PrioritySolver;
import com.dragome.commons.compiler.classpath.serverside.FolderClasspathEntry;
import com.dragome.commons.compiler.classpath.serverside.JarClasspathEntry;

public class Classpath
{
	private List<ClasspathEntry> entries= new ArrayList<ClasspathEntry>();

	public Classpath()
	{
	}

	public Classpath(ClasspathEntry entry)
	{
		entries.add(entry);
	}

	public void addEntry(String classPathEntry)
	{
		if (classPathEntry.contains(".jar"))
			entries.add(JarClasspathEntry.createFromPath(classPathEntry));
		else
			entries.add(FolderClasspathEntry.createFromPath(classPathEntry));
	}

	public List<ClasspathEntry> getEntries()
	{
		return entries;
	}

	public String toString()
	{
		return J7Helper.join(";", entries.toArray());
	}

	public void setEntries(ClasspathEntry[] entries)
	{
		this.entries= Arrays.asList(entries);
	}

	public void sortClassPath(Comparator<ClasspathEntry> comparator)
	{
		J7Helper.sort(entries, comparator);
	}

	public void sortByPriority(final PrioritySolver prioritySolver)
	{
		sortClassPath(new Comparator<ClasspathEntry>()
		{
			public int compare(ClasspathEntry o1, ClasspathEntry o2)
			{
				return prioritySolver.getPriorityOf(o2) - prioritySolver.getPriorityOf(o1);
			}
		});
	}

	public void addEntries(List<ClasspathEntry> extraClasspath)
	{
		entries.addAll(extraClasspath);
	}

	//	if (!file.exists())
	//	{
	//		DragomeJsCompiler.errorCount++;
	//		Log.getLogger().error("Cannot find resource on class path: " + file.getAbsolutePath());
	//		continue;
	//	}

}
