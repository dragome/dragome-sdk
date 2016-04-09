package com.dragome.commons.compiler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import com.dragome.compiler.DragomeJsCompiler;
import com.dragome.compiler.utils.Log;

public class ClassPath
{
	private List<ClasspathFile> entries= new ArrayList<ClasspathFile>();

	public ClassPath()
	{
	}

	public ClassPath(ClasspathFile entry)
	{
		entries.add(entry);
	}

	public void addEntry(ClasspathFile classPathEntry)
	{
		entries.add(classPathEntry);
	}

	public String[] getEntries()
	{
		return entries.toArray(new String[0]);
	}

	public String toString()
	{
		return J7Helper.join(";", entries.toArray());
	}

	public void setEntries(ClasspathFile[] entries)
	{
		this.entries= Arrays.asList(entries);
	}

	public void sortClassPath(Comparator<ClasspathFile> comparator)
	{
		J7Helper.sort(entries, comparator);
	}

	public void sortByPriority(final PrioritySolver prioritySolver)
	{
		sortClassPath(new Comparator<ClasspathFile>()
		{
			public int compare(ClasspathFile o1, ClasspathFile o2)
			{
				return prioritySolver.getPriorityOf(o2) - prioritySolver.getPriorityOf(o1);
			}
		});
	}

	public void addEntries(List<ClasspathFile> extraClasspath)
	{

	}


//	if (!file.exists())
//	{
//		DragomeJsCompiler.errorCount++;
//		Log.getLogger().error("Cannot find resource on class path: " + file.getAbsolutePath());
//		continue;
//	}

}
