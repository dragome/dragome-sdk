/*******************************************************************************
 * Copyright (c) 2011-2014 Fernando Petrola
 * 
 *  This file is part of Dragome SDK.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
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

package org.xmlvm.util.skeleton;

import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class ClassSkeleton
{

	private static final String COPYRIGHT= "/*\n" + " * Copyright (c) 2002-2011 by XMLVM.org\n" + " *\n" + " * Project Info:  http://www.xmlvm.org\n" + " *\n" + " * This program is free software; you can redistribute it and/or modify it\n" + " * under the terms of the GNU Lesser General Public License as published by\n" + " * the Free Software Foundation; either version 2.1 of the License, or\n" + " * (at your option) any later version.\n" + " *\n" + " * This library is distributed in the hope that it will be useful, but\n" + " * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY\n" + " * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public\n" + " * License for more details.\n" + " *\n" + " * You should have received a copy of the GNU Lesser General Public\n" + " * License along with this library; if not, write to the Free Software\n" + " * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301,\n" + " * USA.\n" + " */\n\n";
	private static final String LINE= "// ----------------------------------------------------------------------------\n";
	private static final String INIT= "__init_";
	private static final char DOT= '_';
	private static final String PARAM= "___";
	/* */
	private StringBuilder hfile;
	private StringBuilder mfile;
	private StringBuilder test;
	private final Class c;

	public ClassSkeleton(Class c)
	{
		this.c= c;
		hfile= new StringBuilder();
		mfile= new StringBuilder();
		test= new StringBuilder();

		String jclass= c.getName();
		String classname= fixName(jclass);
		String shortname= getShort();

		/* Create .h header */
		hfile.append(COPYRIGHT);
		hfile.append("// ").append(shortname).append('\n');
		hfile.append(LINE);
		hfile.append("typedef ").append(shortname).append(' ').append(classname).append(";\n\n");
		hfile.append("@interface ").append(shortname).append(" (cat_").append(classname).append(");\n");

		/* Create .m header */
		mfile.append(COPYRIGHT);
		mfile.append("#import \"").append(classname).append(".h\"\n\n");
		mfile.append("// ").append(shortname).append('\n');
		mfile.append(LINE);
		mfile.append("@implementation ").append(shortname).append(" (cat_").append(classname).append(");\n\n");

		/* Create .h * .m body */
		for (Constructor constr : c.getConstructors())
			buildMethod(INIT + classname, constr.getParameterTypes(), null, c.getModifiers());
		for (Method meth : c.getMethods())
			if (meth.getDeclaringClass().getName().equals(jclass))
				buildMethod(meth.getName(), meth.getParameterTypes(), meth.getReturnType(), meth.getModifiers());

		/* Create footers */
		hfile.append("@end\n");
		mfile.append("@end\n");

		storeBuffer("out/" + classname + ".h", hfile);
		storeBuffer("out/" + classname + ".m", mfile);
	}

	private void buildMethod(String method, Class[] parameters, Class returntype, int type)
	{
		if ((type & Modifier.PUBLIC) == 0)
			return; // Only public elements!

		String decl= getMethodDecleration(method, parameters, returntype, type);
		hfile.append(decl).append(";\n");
		mfile.append(decl).append("\n{\n  // TODO: implement code\n}\n\n");
	}

	private String getMethodDecleration(String method, Class[] parameters, Class returntype, int type)
	{
		StringBuilder name= new StringBuilder();
		StringBuilder params= new StringBuilder();

		name.append((type & Modifier.STATIC) == 0 ? "- (" : "+ (");
		name.append(getTypeRef(returntype)).append(") ");

		name.append(fixName(method));
		for (int i= 0; i < parameters.length; i++)
		{
			Class cc= parameters[i];
			String pname= fixName(cc.getName());
			name.append(PARAM).append(pname);

			params.append(" :").append("(").append(getTypeRef(cc)).append(")").append("a").append(i + 1);
		}
		name.append(params);
		return name.toString();
	}

	private String getTypeRef(Class type)
	{
		if (type == null)
			return "void";
		if (type.isPrimitive())
			return type.toString();
		else
			return fixName(type.getName()) + "*";
	}

	private String fixName(String oldname)
	{
		return oldname.replace('.', DOT);
	}

	private String getShort()
	{
		String classname= c.getName();
		int lastdot= classname.lastIndexOf('.');
		return classname.substring(lastdot + 1, classname.length());
	}

	private boolean storeBuffer(String filename, StringBuilder buffer)
	{
		FileWriter out= null;
		try
		{
			out= new FileWriter(filename);
			out.append(buffer);
			out.close();
			return true;
		}
		catch (IOException ex)
		{
		}
		finally
		{
			try
			{
				out.close();
			}
			catch (IOException ex)
			{
			}
		}
		return false;
	}
}
