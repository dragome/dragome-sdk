package java.util;

import com.dragome.commons.javascript.ScriptHelper;

public class Arrays
{

	/**
	 * Returns a fixed-size list backed by the specified array.
	 * @param a the array by which the list will be backed.
	 * @return list view of the specified array.
	 */
    	public static <T> List<T> asList(T... a)     
	{
		ArrayList<T> result= new ArrayList<T>();
		for (T t : a)
		    result.add(t);
		
		return result;
	}

	/**
	 * Returns true if the two specified arrays of Objects are equal to one another.
	 */
	public static boolean equals(Object[] a1, Object[] a2)
	{
		if (a1 == null && a2 == null)
			return true;
		if (a1 == null || a2 == null || a1.length != a2.length)
			return false;

		int count= a1.length;
		for (int i= 0; i < count; i++)
		{
			Object e1= a1[i];
			Object e2= a2[i];
			if (!(e1 == null ? e2 == null : e1.equals(e2)))
				return false;
		}

		return true;
	}

	/**
	 *  Sorts the specified array of objects into ascending order, according to the natural ordering of its elements.
	 *  <br/>
	 *  <b>Important</b>: This is a restriction of the Java API sort(Object[]) function.
	 */
	public static void sort(Object[] array)
	{
		ScriptHelper.put("array", array, null);
		ScriptHelper.eval("array.sort()", null);
	}

	/**
	 * Sorts the specified array of objects according to the order induced by the specified comparator.
	 */
	public static <T> void sort(T[] array, Comparator<? super T> c)
	{
		ScriptHelper.put("array", array, null);
		if (c == null)
		{
			c= new Comparator<T>()
			{
				public int compare(T o1, T o2)
				{
					return ((Comparable<T>) o1).compareTo(o2);
				}
			};
		}
		ScriptHelper.put("c", c, null);
		ScriptHelper.eval("array.sort(function(o1, o2) {return c.$compare___java_lang_Object__java_lang_Object$int(o1, o2)})", null);
	}

	public static int hashCode(Object a[])
	{
		if (a == null)
			return 0;

		int result= 1;

		for (Object element : a)
			result= 31 * result + (element == null ? 0 : element.hashCode());

		return result;
	}

	public static void sort(double[] a)
	{//No implementado
		throw new NotImplementedMethod("Arrays.sort");
	}

	public static boolean deepEquals0(Object a, Object b)
    {
	    // TODO Auto-generated method stub
	    return false;
    }
}
