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
package com.dragome.commons;

import java.net.URL;
import java.util.HashSet;
import java.util.Set;

public class InstrumentationDragomeConfigurator extends DefaultDragomeConfigurator
{
	protected Set<String> includedPaths= new HashSet<String>();
	protected Set<String> loadedFromParent= new HashSet<String>();
	protected boolean enabled= true;

	public InstrumentationDragomeConfigurator()
	{
	}

	public ClassLoader getNewClassloaderInstance(ClassLoader parent, ClassLoader current)
	{
		return new DragomeInstrumentationClassLoader(new URL[0], parent, current, getBytecodeTransformer(), getLoadedFromParent());
	}

	public Set<String> getIncludedPaths()
	{
		return includedPaths;
	}

	public void setIncludedPaths(Set<String> includedPaths)
	{
		this.includedPaths= includedPaths;
	}

	public Set<String> getLoadedFromParent()
	{
		return loadedFromParent;
	}

	public void setLoadedFromParent(Set<String> loadedFromParent)
	{
		this.loadedFromParent= loadedFromParent;
	}

	public boolean isEnabled()
	{
		return enabled;
	}

	public void setEnabled(boolean enabled)
	{
		this.enabled= enabled;
	}
}
