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
package java.net;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.security.ProtectionDomain;

public class URLClassLoader extends ClassLoader implements Closeable
{
	public URLClassLoader(URL[] urls, ClassLoader fixNullParent)
    {
	    // TODO Auto-generated constructor stub
    }

	public void close() throws IOException
	{
	}

	public Class<?> loadClass(String name) throws ClassNotFoundException
	{
		// TODO Auto-generated method stub
		return null;
	}

	public URL[] getURLs()
	{
		// TODO Auto-generated method stub
		return null;
	}
	
	public ClassLoader getParent()
    {
		return null;
	    // TODO Auto-generated method stub

    }
	
	public static ClassLoader getSystemClassLoader()
    {
		return null;
	    // TODO Auto-generated method stub

    }
	
	public Class<?> findLoadedClass(String classname)
    {
		return null;
	    // TODO Auto-generated method stub

    }

	public Class<?> resolveClass(Class<?> classname)
    {
		return null;
	    // TODO Auto-generated method stub

    }
	
	public Package getPackage(String packageName)
    {
		return null;
	    // TODO Auto-generated method stub

    }
	
	public void definePackage(String packageName, Object object, Object object2, Object object3, Object object4, Object object5, Object object6, Object object7)
    {
	    // TODO Auto-generated method stub

    }
	
	public Object defineClass(String classname, byte[] newData, int i, int length, ProtectionDomain domain)
    {
		return domain;
	    // TODO Auto-generated method stub

    }
	
	public InputStream getResourceAsStream(String classFileName)
    {
		return null;
	    // TODO Auto-generated method stub

    }
	

	public URL findResource(String name)
    {
		return null;
	    // TODO Auto-generated method stub

    }
	
	

	
}
