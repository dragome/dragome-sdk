/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.dragome.callbackevictor.enhancers;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.dragome.commons.ProxyRelatedInvocationHandler;
import com.dragome.utils.MethodHolder;

/**
 * Stack to store the frame information along the invocation trace.
 */
public class Stack implements Serializable
{
	private static final long serialVersionUID= 2L;

	private int[] istack;
	private float[] fstack;
	private double[] dstack;
	private long[] lstack;
	private Object[] ostack;
	private Object[] rstack;
	private int iTop, fTop, dTop, lTop, oTop, rTop;
	private Runnable runnable;

	public Stack()
	{
	}
	public Stack(Runnable pRunnable)
	{
		setRunnable(pRunnable);
	}

	public Stack(final Stack pParent)
	{

		if (pParent.istack != null)
		{
			iTop= pParent.iTop;
			istack= new int[pParent.istack.length];
			System.arraycopy(pParent.istack, 0, istack, 0, iTop);
		}
		if (pParent.lstack != null)
		{
			lTop= pParent.lTop;
			lstack= new long[pParent.lstack.length];
			System.arraycopy(pParent.lstack, 0, lstack, 0, lTop);
		}
		if (pParent.dstack != null)
		{
			dTop= pParent.dTop;
			dstack= new double[pParent.dstack.length];
			System.arraycopy(pParent.dstack, 0, dstack, 0, dTop);
		}
		if (pParent.fstack != null)
		{
			fTop= pParent.fTop;
			fstack= new float[pParent.fstack.length];
			System.arraycopy(pParent.fstack, 0, fstack, 0, fTop);
		}
		if (pParent.ostack != null)
		{
			oTop= pParent.oTop;
			ostack= new Object[pParent.ostack.length];
			System.arraycopy(pParent.ostack, 0, ostack, 0, oTop);
		}
		if (pParent.rstack != null)
		{
			rTop= pParent.rTop;
			rstack= new Object[pParent.rstack.length];
			System.arraycopy(pParent.rstack, 0, rstack, 0, rTop);
		}

		setRunnable(pParent.getRunnable());
	}

	public boolean hasDouble()
	{
		return dTop > 0;
	}

	public double popDouble()
	{
		if (dTop == 0)
		{
			throw new EmptyStackException("pop double");
		}

		final double d= dstack[--dTop];
		return d;
	}

	public boolean hasFloat()
	{
		return fTop > 0;
	}

	public float popFloat()
	{
		if (fTop == 0)
		{
			throw new EmptyStackException("pop float");
		}

		final float f= fstack[--fTop];
		return f;
	}

	public boolean hasInt()
	{
		return iTop > 0;
	}

	public int popInt()
	{
		if (iTop == 0)
		{
			throw new EmptyStackException("pop int");
		}

		final int i= istack[--iTop];
		return i;
	}

	public boolean hasLong()
	{
		return lTop > 0;
	}

	public long popLong()
	{
		if (lTop == 0)
		{
			throw new EmptyStackException("pop long");
		}

		final long l= lstack[--lTop];
		return l;
	}

	public boolean hasObject()
	{
		return oTop > 0;
	}

	public Object popObject()
	{
		if (oTop == 0)
		{
			throw new EmptyStackException("pop object");
		}

		final Object o= ostack[--oTop];
		ostack[oTop]= null; // avoid unnecessary reference to object

		return o;
	}

	public boolean hasReference()
	{
		return rTop > 0;
	}

	public Object popReference()
	{
		if (rTop == 0)
		{
			throw new EmptyStackException("pop reference");
		}

		Object o= rstack[--rTop];
		rstack[rTop]= null; // avoid unnecessary reference to object

		return o;
	}

	public void pushDouble(double d)
	{
		if (dstack == null)
			dstack= new double[5];

		if (dTop == dstack.length)
		{
			double[] hlp= new double[Math.max(8, dstack.length * 2)];
			System.arraycopy(dstack, 0, hlp, 0, dstack.length);
			dstack= hlp;
		}
		dstack[dTop++]= d;
	}

	public void pushFloat(float f)
	{
		if (fstack == null)
			fstack= new float[5];

		if (fTop == fstack.length)
		{
			float[] hlp= new float[Math.max(8, fstack.length * 2)];
			System.arraycopy(fstack, 0, hlp, 0, fstack.length);
			fstack= hlp;
		}
		fstack[fTop++]= f;
	}

	public void pushInt(int i)
	{
		if (istack == null)
			istack= new int[10];

		if (iTop == istack.length)
		{
			int[] hlp= new int[Math.max(8, istack.length * 2)];
			System.arraycopy(istack, 0, hlp, 0, istack.length);
			istack= hlp;
		}
		istack[iTop++]= i;
	}

	public void pushLong(long l)
	{
		if (lstack == null)
			lstack= new long[5];

		if (lTop == lstack.length)
		{
			long[] hlp= new long[Math.max(8, lstack.length * 2)];
			System.arraycopy(lstack, 0, hlp, 0, lstack.length);
			lstack= hlp;
		}
		lstack[lTop++]= l;
	}

	public void pushObject(Object o)
	{
		if (ostack == null)
			ostack= new Object[10];

		if (oTop > 0 && ostack[oTop - 1] instanceof MethodHolder)
		{
			o= null;
		}
		//		o= replaceInvocationHandler(o, false);

		if (oTop == ostack.length)
		{
			Object[] hlp= new Object[Math.max(8, ostack.length * 2)];
			System.arraycopy(ostack, 0, hlp, 0, ostack.length);
			ostack= hlp;
		}
		ostack[oTop++]= o;
	}

	public void replaceReference(Object o)
	{
		if (o instanceof MethodHolder)
		{
			MethodHolder methodHolder= (MethodHolder) o;
			pushReference(methodHolder.getMethod());
		}
		else
		{
			popReference();
			pushReference(o);
		}
	}

	public void pushReference(Object o)
	{
		if (rstack == null)
			rstack= new Object[5];

		o= replaceInvocationHandler(o, true);

		if (rTop == rstack.length)
		{
			Object[] hlp= new Object[Math.max(8, rstack.length * 2)];
			System.arraycopy(rstack, 0, hlp, 0, rstack.length);
			rstack= hlp;
		}
		rstack[rTop++]= o;
	}

	private Object replaceInvocationHandler(Object o, boolean bypass)
	{
		if (o instanceof ProxyRelatedInvocationHandler)
		{
			ProxyRelatedInvocationHandler proxyRelatedInvocationHandler= (ProxyRelatedInvocationHandler) o;
			if (bypass || !proxyRelatedInvocationHandler.getClass().getSimpleName().contains("InvocationHandlerForLambdas"))
				o= proxyRelatedInvocationHandler.getProxy();
			else
				System.out.println("dzgdsag");
		}
		return o;
	}

	public boolean isSerializable()
	{
		for (int i= 0; i < rTop; i++)
		{
			final Object r= rstack[i];
			if (!(r instanceof Serializable))
			{
				return false;
			}
		}
		for (int i= 0; i < oTop; i++)
		{
			final Object o= ostack[i];
			if (!(o instanceof Serializable))
			{
				return false;
			}
		}
		return true;
	}

	protected boolean isEmpty()
	{
		return iTop == 0 && lTop == 0 && dTop == 0 && fTop == 0 && oTop == 0 && rTop == 0;
	}

	public Runnable getRunnable()
	{
		return runnable;
	}

	public void setRunnable(Runnable runnable)
	{
		this.runnable= runnable;
	}

	public List<Object> getStacks()
	{
		ArrayList<Object> result= new ArrayList<>();
		List<Integer> asList= Arrays.asList(iTop, fTop, dTop, lTop, oTop, rTop);
		result.addAll(asList);

		if (iTop != 0)
			result.addAll(convertIntArray());
		if (fTop != 0)
			result.addAll(convertFloatArray());
		if (dTop != 0)
			result.addAll(convertDoubleArray());
		if (lTop != 0)
			result.addAll(convertLongArray());
		if (oTop != 0)
			result.addAll(convertObjectArray(ostack, oTop));
		if (rTop != 0)
			result.addAll(convertObjectArray(rstack, rTop));
		return result;
	}

	private List<Object> convertIntArray()
	{
		List<Object> output= new ArrayList<>();
		for (int value : istack)
			output.add(value);

		return output.subList(0, iTop);
	}

	private List<Object> convertFloatArray()
	{
		List<Object> output= new ArrayList<>();
		for (int value : istack)
			output.add(value);

		return output.subList(0, iTop);
	}
	private List<Object> convertDoubleArray()
	{
		List<Object> output= new ArrayList<>();
		for (int value : istack)
			output.add(value);

		return output.subList(0, iTop);
	}
	private List<Object> convertLongArray()
	{
		List<Object> output= new ArrayList<>();
		for (int value : istack)
			output.add(value);

		return output.subList(0, iTop);
	}
	private List<Object> convertObjectArray(Object[] array, int top)
	{
		List<Object> output= new ArrayList<>();
		for (Object value : array)
			output.add(value);

		return output.subList(0, top);
	}

	public void setStacks(List<Object> tops)
	{
		iTop= (int) tops.get(0);
		fTop= (int) tops.get(1);
		dTop= (int) tops.get(2);
		lTop= (int) tops.get(3);
		oTop= (int) tops.get(4);
		rTop= (int) tops.get(5);

		if (iTop > 0)
			istack= new int[iTop];
		if (fTop > 0)
			fstack= new float[fTop];
		if (dTop > 0)
			dstack= new double[dTop];
		if (lTop > 0)
			lstack= new long[lTop];
		if (oTop > 0)
			ostack= new Object[oTop];
		if (rTop > 0)
			rstack= new Object[rTop];

		int i= 0;
		for (int j= 6; j < tops.size(); j++)
		{
			if (i < iTop)
				istack[i]= (int) tops.get(j);
			else if (i < iTop + fTop)
				fstack[i - iTop]= (float) tops.get(j);
			else if (i < iTop + fTop + dTop)
				dstack[i - iTop - fTop]= (double) tops.get(j);
			else if (i < iTop + fTop + dTop + lTop)
				lstack[i - iTop - fTop - dTop]= (long) tops.get(j);
			else if (i < iTop + fTop + dTop + lTop + oTop)
				ostack[i - iTop - fTop - dTop - lTop]= tops.get(j);
			else if (i < iTop + fTop + dTop + lTop + oTop + rTop)
				rstack[i - iTop - fTop - dTop - lTop - oTop]= tops.get(j);
			i++;
		}
		
		System.out.println("fsdgh");
	}
}
