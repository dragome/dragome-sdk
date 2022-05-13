
package com.dragome.callbackevictor.enhancers;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import com.dragome.commons.ProxyRelatedInvocationHandler;

public class Stack implements Serializable
{
	private static final int ANY_INITIAL_INDEX= 10;
	private String id;
	private static final long serialVersionUID= 2L;
	private Runnable runnable;

	private Object[] lstack;
	private int lTop= ANY_INITIAL_INDEX, rTop;

	public Stack()
	{
	}
	public Stack(Runnable pRunnable)
	{
		setRunnable(pRunnable);
	}

	public Stack(final Stack pParent)
	{
		if (pParent.lstack != null)
		{
			lTop= pParent.lTop;
			lstack= new Object[pParent.lTop + 2];
			System.arraycopy(pParent.lstack, 0, lstack, 0, lTop);
		}

		rTop= pParent.rTop;
		setRunnable(pParent.getRunnable());
	}

	public boolean isEmpty()
	{
		return lTop == ANY_INITIAL_INDEX && rTop == 0;
	}

	public double popDouble()
	{
		return (Double) pop(lstack, lTop--);
	}

	private Object pop(Object[] stack, int top)
	{
		if (top == ANY_INITIAL_INDEX)
			throw new EmptyStackException("pop double");

		return stack[--top];
	}

	public float popFloat()
	{
		return (Float) pop(lstack, lTop--);
	}

	public int popInt()
	{
		return (Integer) pop(lstack, lTop--);
	}

	public long popLong()
	{
		return (Long) pop(lstack, lTop--);
	}

	public Object popObject()
	{
		return pop(lstack, lTop--);
	}

	public Object popReference()
	{
		return pop(lstack, rTop--);
	}

	public void pushDouble(double d)
	{
		lstack= (Double[]) push(d, lstack, lTop++);
	}

	public void pushFloat(float f)
	{
		lstack= push(f, lstack, lTop++);
	}

	public void pushInt(int i)
	{
		lstack= push(i, lstack, lTop++);
	}

	public void pushLong(long l)
	{
		lstack= push(l, lstack, lTop++);
	}

	public void pushObject(Object o)
	{
		lstack= push(o, lstack, lTop++);
	}
	private Object[] push(Object o, Object[] array, int top)
	{
		if (array == null)
			array= new Object[20];

		if (top == array.length)
		{
			Object[] hlp= new Object[(int) Math.max(8, array.length * 1.5)];
			System.arraycopy(array, 0, hlp, 0, array.length);
			array= hlp;
		}
		array[top++]= o;
		return array;
	}

	public void pushReference(Object o)
	{
		if (rTop >= ANY_INITIAL_INDEX)
			throw new RuntimeException("Not implemented!");

		o= replaceInvocationHandler(o, true);
		lstack= push(o, lstack, rTop++);
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

	public boolean isSerializable()
	{
		if (lstack != null)
		{
			for (int i= 0; i < rTop; i++)
			{
				final Object r= lstack[i];
				if (!(r instanceof Serializable))
				{
					return false;
				}
			}
			for (int i= 0; i < lTop; i++)
			{
				final Object o= lstack[i];
				if (!(o instanceof Serializable))
				{
					return false;
				}
			}
		}
		return true;
	}

	public void replaceReference(Object o)
	{
		popReference();
		pushReference(o);
	}

	public List<Object> getLstack()
	{
		return lstack == null ? null : Arrays.asList(lstack);
	}
	public void setLstack(List<Object> lstack)
	{
		this.lstack= lstack == null ? null : lstack.toArray();
	}
	public int getlTop()
	{
		return lTop;
	}
	public void setlTop(int lTop)
	{
		this.lTop= lTop;
	}
	public int getrTop()
	{
		return rTop;
	}
	public void setrTop(int rTop)
	{
		this.rTop= rTop;
	}

}
