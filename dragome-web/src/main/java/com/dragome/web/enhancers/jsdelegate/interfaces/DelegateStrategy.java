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

import java.lang.reflect.Method;

public interface DelegateStrategy
{
	/**
	 * This makes you have a custom javascript code for interface methods. For a custom code you can return javascript code in a string or use the rawCode param and return null. <br>
	 * Returning null without touching rawCode will use a generic code. Javascript parameters is already added for you. You access them by using $1, $2 and so on.
	 */
	String createMethodCall(Method method, String params);

	String getSubTypeExtractorFor(Class<?> interface1, String methodName);

	Class<? extends SubTypeFactory> getSubTypeFactoryClassFor(Class<?> interface1, String methodName);

	String createReturnExpression(Method method);
}
