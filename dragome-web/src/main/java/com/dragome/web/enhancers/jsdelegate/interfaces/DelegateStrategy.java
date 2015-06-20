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

package com.dragome.web.enhancers.jsdelegate.interfaces;

import javassist.CtMethod;

public interface DelegateStrategy
{
	boolean isPropertyWriteMethod(CtMethod method);

	boolean isPropertyReadMethod(CtMethod method);

	String getSubTypeExtractorFor(Class<?> interface1, String methodName);

	Class<? extends SubTypeFactory> getSubTypeFactoryClassFor(Class<?> interface1, String methodName);
}
