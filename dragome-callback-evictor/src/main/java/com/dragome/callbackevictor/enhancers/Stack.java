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
import java.lang.reflect.Proxy;

import com.dragome.commons.ProxyRelatedInvocationHandler;

/**
 * Stack to store the frame information along the invocation trace.
 */
public class Stack implements Serializable
{
	private String id;
	private static final long serialVersionUID= 2L;

	private int[] istack;
	private float[] fstack;
	private double[] dstack;
	private long[] lstack;
	private Object[] ostack;
	private Object[] rstack;
	private int iTop, fTop, dTop, lTop, oTop, rTop;
	private Runnable runnable;

	public Stack(Runnable pRunnable)
	{
		istack= new int[10];
		lstack= new long[5];
		dstack= new double[5];
		fstack= new float[5];
		ostack= new Object[10];
		rstack= new Object[5];
		setRunnable(pRunnable);
	}

	public Stack(final Stack pParent)
	{
		istack= new int[pParent.istack.length];
		lstack= new long[pParent.lstack.length];
		dstack= new double[pParent.dstack.length];
		fstack= new float[pParent.fstack.length];
		ostack= new Object[pParent.ostack.length];
		rstack= new Object[pParent.rstack.length];
		iTop= pParent.iTop;
		fTop= pParent.fTop;
		dTop= pParent.dTop;
		lTop= pParent.lTop;
		oTop= pParent.oTop;
		rTop= pParent.rTop;
		System.arraycopy(pParent.istack, 0, istack, 0, iTop);
		System.arraycopy(pParent.fstack, 0, fstack, 0, fTop);
		System.arraycopy(pParent.dstack, 0, dstack, 0, dTop);
		System.arraycopy(pParent.lstack, 0, lstack, 0, lTop);
		System.arraycopy(pParent.ostack, 0, ostack, 0, oTop);
		System.arraycopy(pParent.rstack, 0, rstack, 0, rTop);
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
		//		if (o instanceof ProxyRelatedInvocationHandler)
		//		{
		//			ProxyRelatedInvocationHandler proxyRelatedInvocationHandler= (ProxyRelatedInvocationHandler) o;
		//			o= proxyRelatedInvocationHandler.getProxy();
		//		}

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
		popReference();
		pushReference(o);
	}

	public void pushReference(Object o)
	{
		if (o instanceof ProxyRelatedInvocationHandler)
		{
			ProxyRelatedInvocationHandler proxyRelatedInvocationHandler = (ProxyRelatedInvocationHandler) o;
			o = proxyRelatedInvocationHandler.getProxy();
		}

		if (rTop == rstack.length)
		{
			Object[] hlp= new Object[Math.max(8, rstack.length * 2)];
			System.arraycopy(rstack, 0, hlp, 0, rstack.length);
			rstack= hlp;
		}
		rstack[rTop++]= o;
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

	public boolean isEmpty()
	{
		return iTop == 0 && lTop == 0 && dTop == 0 && fTop == 0 && oTop == 0 && rTop == 0;
	}

	private String getStats()
	{
		final StringBuffer sb= new StringBuffer();
		sb.append("i[").append(iTop).append("],");
		sb.append("l[").append(lTop).append("],");
		sb.append("d[").append(dTop).append("],");
		sb.append("f[").append(fTop).append("],");
		sb.append("o[").append(oTop).append("],");
		sb.append("r[").append(rTop).append("]");
		return sb.toString();
	}

	public Runnable getRunnable()
	{
		return runnable;
	}

	public void setRunnable(Runnable runnable)
	{
		this.runnable= runnable;
	}

	public String getId()
	{
		return id;
	}

	public void setId(String id)
	{
		this.id= id;
	}
	public int[] getIstack()
	{
		return istack;
	}

	public void setIstack(int[] istack)
	{
		this.istack= istack;
	}

	public float[] getFstack()
	{
		return fstack;
	}

	public void setFstack(float[] fstack)
	{
		this.fstack= fstack;
	}

	public double[] getDstack()
	{
		return dstack;
	}

	public void setDstack(double[] dstack)
	{
		this.dstack= dstack;
	}

	public long[] getLstack()
	{
		return lstack;
	}

	public void setLstack(long[] lstack)
	{
		this.lstack= lstack;
	}

	public Object[] getOstack()
	{
		return ostack;
	}

	public void setOstack(Object[] ostack)
	{
		this.ostack= ostack;
	}

	public Object[] getRstack()
	{
		return rstack;
	}

	public void setRstack(Object[] rstack)
	{
		this.rstack= rstack;
	}

	public int getITop()
	{
		return iTop;
	}

	public void setITop(int top)
	{
		iTop= top;
	}

	public int getFTop()
	{
		return fTop;
	}

	public void setFTop(int top)
	{
		fTop= top;
	}

	public int getDTop()
	{
		return dTop;
	}

	public void setDTop(int top)
	{
		dTop= top;
	}

	public int getLTop()
	{
		return lTop;
	}

	public void setLTop(int top)
	{
		lTop= top;
	}

	public int getOTop()
	{
		return oTop;
	}

	public void setOTop(int top)
	{
		oTop= top;
	}

	public int getRTop()
	{
		return rTop;
	}

	public void setRTop(int top)
	{
		rTop= top;
	}

	//    private String getContent() {
	//        final StringBuffer sb = new StringBuffer();
	//        sb.append("i[").append(iTop).append("]\n");
	//        sb.append("l[").append(lTop).append("]\n");
	//        sb.append("d[").append(dTop).append("]\n");
	//        sb.append("f[").append(fTop).append("]\n");
	//        sb.append("o[").append(oTop).append("]\n");
	//        for(int i=0; i<oTop;i++) {
	//            sb.append(' ').append(i).append(": ");
	//            sb.append(ReflectionUtils.getClassName(ostack[i])).append('/').append(ReflectionUtils.getClassLoaderName(ostack[i]));
	//            sb.append('\n');
	//        }
	//        sb.append("r[").append(rTop).append("]\n");
	//        for(int i=0; i<rTop;i++) {
	//            sb.append(' ').append(i).append(": ");
	//            sb.append(ReflectionUtils.getClassName(rstack[i])).append('/').append(ReflectionUtils.getClassLoaderName(rstack[i]));
	//            sb.append('\n');
	//        }
	//        
	//        return sb.toString();
	//    }

	//    public String toString() {
	//        return getContent();
	//    }

	//    private void writeObject(ObjectOutputStream s) throws IOException {
	//        s.writeInt(iTop);
	//        for( int i=0; i<iTop; i++ ) {
	//            s.writeInt(istack[i]);
	//        }
	//
	//        s.writeInt(lTop);
	//        for( int i=0; i<lTop; i++ ) {
	//            s.writeLong(lstack[i]);
	//        }
	//
	//        s.writeInt(dTop);
	//        for( int i=0; i<dTop; i++ ) {
	//            s.writeDouble(dstack[i]);
	//        }
	//
	//        s.writeInt(fTop);
	//        for( int i=0; i<fTop; i++ ) {
	//            s.writeDouble(fstack[i]);
	//        }
	//
	//        s.writeInt(oTop);
	//        for( int i=0; i<oTop; i++ ) {
	//            s.writeObject(ostack[i]);
	//        }
	//
	//        s.writeInt(rTop);
	//        for( int i=0; i<rTop; i++ ) {
	//            s.writeObject(rstack[i]);
	//        }
	//
	//        s.writeObject(runnable);
	//    }
	//
	//    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
	//        iTop = s.readInt();
	//        istack = new int[iTop];
	//        for( int i=0; i<iTop; i++ ) {
	//            istack[i] = s.readInt();
	//        }
	//
	//        lTop = s.readInt();
	//        lstack = new long[lTop];
	//        for( int i=0; i<lTop; i++ ) {
	//            lstack[i] = s.readLong();
	//        }
	//
	//        dTop = s.readInt();
	//        dstack = new double[dTop];
	//        for( int i=0; i<dTop; i++ ) {
	//            dstack[i] = s.readDouble();
	//        }
	//
	//        fTop = s.readInt();
	//        fstack = new float[fTop];
	//        for( int i=0; i<fTop; i++ ) {
	//            fstack[i] = s.readFloat();
	//        }
	//
	//        oTop = s.readInt();
	//        ostack = new Object[oTop];
	//        for( int i=0; i<oTop; i++ ) {
	//            ostack[i] = s.readObject();
	//        }
	//
	//        rTop = s.readInt();
	//        rstack = new Object[rTop];
	//        for( int i=0; i<rTop; i++ ) {
	//            rstack[i] = s.readObject();
	//        }
	//
	//        runnable = (Runnable)s.readObject();
	//    }

}
