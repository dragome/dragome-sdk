/*
 * Copyright (c) 2011-2014 Fernando Petrola
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
