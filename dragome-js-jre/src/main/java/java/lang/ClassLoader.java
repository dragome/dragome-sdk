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
package java.lang;

import java.net.URL;

public abstract class ClassLoader
{
	public abstract Class<?> loadClass(String name) throws ClassNotFoundException;
	
	public URL getResource(String name)
    {
		return null;
    }
}
