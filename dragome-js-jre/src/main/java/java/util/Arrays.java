package java.util;

import java.lang.reflect.Array;

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

	private static class NaturalOrder<T extends Comparable<Object>> implements Comparator<T>
	{
		public int compare(T o1, T o2)
		{
			return o1 != null ? o1.compareTo(o2) : o2 != null ? o2.compareTo(o1) : 0;
		}
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

	public static void sort(Object[] array, int fromIndex, int toIndex)
	{
		ScriptHelper.put("array", array, null);
		ScriptHelper.put("fromIndex", fromIndex, null);
		ScriptHelper.put("toIndex", toIndex, null);

		Object subarray= ScriptHelper.eval("array.slice(fromIndex, toIndex)", null);
		ScriptHelper.put("subarray", subarray, null);

		ScriptHelper.eval("subarray.sort()", null);
		ScriptHelper.eval("for (var i= fromIndex; i < toIndex; ++i)	array[i]= subarray[i - fromIndex]", null);
	}

	public static <T> void sort(T[] a, int fromIndex, int toIndex, Comparator<? super T> comparator)
	{
		T[] subarray= (T[]) new Object[toIndex - fromIndex];

		for (int i= fromIndex; i < toIndex; ++i)
			subarray[i - fromIndex]= a[i];

		sort(subarray, comparator);

		for (int i= fromIndex; i < toIndex; ++i)
			a[i]= subarray[i - fromIndex];
	}

	/**
	 * Sorts the specified array of objects according to the order induced by the specified comparator.
	 */
	public static <T> void sort(T[] array, Comparator<? super T> comparator)
	{
		ScriptHelper.put("array", array, null);
		if (comparator == null)
			comparator= new NaturalOrder();

		ScriptHelper.put("c", comparator, null);
		ScriptHelper.eval("array.sort(function(o1, o2) {return c.$compare___java_lang_Object__java_lang_Object$int(o1, o2)})", null);
	}

	public static int hashCode(Object array[])
	{
		if (array == null)
			return 0;

		int result= 1;

		for (Object element : array)
			result= 31 * result + (element == null ? 0 : element.hashCode());

		return result;
	}

	public static void sort(double[] a)
	{
		sort(a);
	}

	public static boolean deepEquals0(Object a, Object b)
	{
		// TODO Auto-generated method stub
		return false;
	}

    public static <T,U> T[] copyOf(U[] original, int newLength, Class<? extends T[]> newType) {
        @SuppressWarnings("unchecked")
        T[] copy = ((Object)newType == (Object)Object[].class)
            ? (T[]) new Object[newLength]
            : (T[]) Array.newInstance(newType.getComponentType(), newLength);
        System.arraycopy(original, 0, copy, 0,
                         Math.min(original.length, newLength));
        return copy;
    }

    public static void fill(Object[] a, int fromIndex, int toIndex, Object val) {
        for (int i = fromIndex; i < toIndex; i++)
            a[i] = val;
    }
}
