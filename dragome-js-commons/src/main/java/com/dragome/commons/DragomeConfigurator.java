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

import com.dragome.commons.compiler.BytecodeTransformer;

public interface DragomeConfigurator
{
	public ClassLoader getNewClassloaderInstance(ClassLoader parent, ClassLoader current);
	public BytecodeTransformer getBytecodeTransformer();
	public ExecutionHandler getExecutionHandler();
	public boolean filterClassPath(String classpathEntry);
}
