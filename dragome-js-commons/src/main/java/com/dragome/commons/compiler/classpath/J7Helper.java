package com.dragome.commons.compiler.classpath;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class J7Helper
{
	public static String join(String s, Object... a)
	{
		StringBuilder o= new StringBuilder();
		for (Iterator<Object> i= Arrays.asList(a).iterator(); i.hasNext();)
			o.append(i.next()).append(i.hasNext() ? s : "");
		return o.toString();
	}

	static <T> void sort(List<T> list, Comparator<T> comparator)
	{
		Object[] a= list.toArray();
		Arrays.sort(a, (Comparator) comparator);
		ListIterator<T> i= list.listIterator();
		for (Object e : a)
		{
			i.next();
			i.set((T) e);
		}
	}
}
