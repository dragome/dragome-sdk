package org.xmlvm.util;

import java.util.ArrayList;
import java.util.List;

import org.xmlvm.proc.XmlvmResource.XmlvmInvokeInstruction;
import org.xmlvm.proc.XmlvmResource.XmlvmMethod;

/**
 * Class {@link Vtable} represents the Vtable for one class or interface. It is
 * basically a list of {@link org.xmlvm.proc.XmlvmResource.XmlvmMethod} that
 * includes all methods of that class that have an entry in the Vtable. Note
 * that only public and protected methods have entries in the Vtable. Also note
 * that the Vtable includes all eligible methods from all base classes. The
 * Vtable index of a method corresponds to the list index.
 * 
 */
public class Vtable
{

	private List<XmlvmMethod> virtualMethods;

	/**
	 * Constructs an empty {@link Vtable}.
	 */
	public Vtable()
	{
		virtualMethods= new ArrayList<XmlvmMethod>();
	}

	/**
	 * Constructs a new {@link Vtable} based on an existing Vtable.
	 * 
	 * @param vtable
	 *            Initial {@link Vtable}. A deep copy is performed on
	 *            <code>vtable</code>.
	 */
	public Vtable(Vtable vtable)
	{
		virtualMethods= new ArrayList<XmlvmMethod>(vtable.virtualMethods);
	}

	public Vtable clone()
	{
		return new Vtable(this);
	}

	/**
	 * Determine the Vtable index of a method.
	 * 
	 * @param method
	 *            Method for which the Vtable index is to be determined.
	 * @return Vtable index (&gt;= 0) of this method or -1 if method has no
	 *         entry in the Vtable.
	 */
	public int getVtableIndex(XmlvmMethod method)
	{
		for (int i= 0; i < virtualMethods.size(); i++)
		{
			if (virtualMethods.get(i).doesOverrideMethod(method))
			{
				return i;
			}
		}
		return -1;
	}

	/**
	 * Determines the Vtable index that an
	 * <code>&lt;dex:invoke-virtual&gt;</code> instruction should use.
	 * 
	 * @param instruction
	 *            <code>&lt;dex:invoke-virtual&gt;</code> instruction for which
	 *            to determine the Vtable index.
	 * @return Vtable index (&gt;= 0) for this instruction or -1 if instruction
	 *         has no entry in the Vtable (which indicates an internal error).
	 */
	public int getVtableIndex(XmlvmInvokeInstruction instruction)
	{
		for (int i= 0; i < virtualMethods.size(); i++)
		{
			if (virtualMethods.get(i).doesOverrideMethod(instruction))
			{
				return i;
			}
		}
		return -1;
	}

	/**
	 * Adds a method to the Vtable.
	 * 
	 * @param method
	 *            Method to be added.
	 */
	public void addMethod(XmlvmMethod method)
	{
		int idx= virtualMethods.size();
		method.setVtableIndex(idx);
		virtualMethods.add(method);
	}

	/**
	 * @param vtable
	 */
	public void addVtable(Vtable vtable)
	{
		for (XmlvmMethod method : vtable.virtualMethods)
		{
			if (getVtableIndex(method) == -1)
			{
				addMethod(method);
			}
		}
	}

	/**
	 * Determines the size of the Vtable.
	 * 
	 * @return Size of Vtable.
	 */
	public int getVtableSize()
	{
		return virtualMethods.size();
	}

}
