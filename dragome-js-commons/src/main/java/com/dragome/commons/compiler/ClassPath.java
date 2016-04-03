package com.dragome.commons.compiler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class ClassPath
{
	private List<String> entries= new ArrayList<String>();

	public ClassPath()
	{
	}

	public ClassPath(String entry)
	{
		entries.add(entry);
	}

	public void addEntry(String classPathEntry)
	{
		entries.add(classPathEntry);
	}

	public String[] getEntries()
	{
		return entries.toArray(new String[0]);
	}

	public String toString()
	{
		return String.join(";", entries);
	}

	public void setEntries(String[] entries)
	{
		this.entries= Arrays.asList(entries);
	}

	public void sortClassPath(Comparator<String> comparator)
	{
		entries.sort(comparator);
	}

	public void sortByPriority(final PrioritySolver prioritySolver)
	{
		sortClassPath(new Comparator<String>()
		{
			public int compare(String o1, String o2)
			{
				return prioritySolver.getPriorityOf(o2) - prioritySolver.getPriorityOf(o1);
			}
		});
	}
}
