/* Copyright (c) 2002-2011 by XMLVM.org
 *
 * Project Info:  http://www.xmlvm.org
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301,
 * USA.
 */

package org.xmlvm.proc;

import java.util.ArrayList;
import java.util.List;

/**
 * The XMLVM class representation.
 */
public class XmlvmClass extends XmlvmEntity
{
	private String packageName;
	private String superClassname;
	private List<String> interfaces= new ArrayList<String>();
	private List<XmlvmField> fields= new ArrayList<XmlvmField>();
	private List<XmlvmMethod> methods= new ArrayList<XmlvmMethod>();

	public String getPackageName()
	{
		return packageName;
	}

	public void setPackageName(String packageName)
	{
		this.packageName= packageName;
	}

	public String getSuperClassname()
	{
		return superClassname;
	}

	public void setSuperClassname(String superClassname)
	{
		this.superClassname= superClassname;
	}

	public String[] getInterfaces()
	{
		return interfaces.toArray(new String[0]);
	}

	public void addInterface(String interfaceName)
	{
		interfaces.add(interfaceName);
	}

	public XmlvmField[] getFields()
	{
		return fields.toArray(new XmlvmField[0]);
	}

	public void addField(XmlvmField field)
	{
		fields.add(field);
	}

	public XmlvmMethod[] getMethods()
	{
		return methods.toArray(new XmlvmMethod[0]);
	}

	public void addMethod(XmlvmMethod method)
	{
		methods.add(method);
	}

	/**
	 * The XMLVM member representation.
	 */
	public static class XmlvmField extends XmlvmEntity
	{
		private String type;

		public String getType()
		{
			return type;
		}

		public void setType(String type)
		{
			this.type= type;
		}
	}

	/**
	 * The XMLVM method representation.
	 */
	public static class XmlvmMethod extends XmlvmEntity
	{
		private int maxStack= 0;
		private int maxLocals= 0;
		private List<String> argumentTypes= new ArrayList<String>();
		private String nativeMethodName;

		public int getMaxStack()
		{
			return maxStack;
		}

		public void setMaxStack(int maxStack)
		{
			this.maxStack= maxStack;
		}

		public int getMaxLocals()
		{
			return maxLocals;
		}

		public void setMaxLocals(int maxLocals)
		{
			this.maxLocals= maxLocals;
		}

		public void addArgumentType(String type)
		{
			argumentTypes.add(type);
		}

		public String[] getArgumentTypes()
		{
			return argumentTypes.toArray(new String[0]);
		}

		public String getNativeMethodName()
		{
			return nativeMethodName;
		}

		public void setNativeMethodName(String nativeMethodName)
		{
			this.nativeMethodName= nativeMethodName;
		}
	}
}
