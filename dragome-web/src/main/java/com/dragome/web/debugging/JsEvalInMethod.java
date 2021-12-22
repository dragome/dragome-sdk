/*******************************************************************************
 * Copyright (c) 2011-2014 Fernando Petrola
 * 
 * This file is part of Dragome SDK.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package com.dragome.web.debugging;

import java.io.Serializable;

public class JsEvalInMethod extends ScriptCrossExecutionCommand implements Serializable
{
	private static final long serialVersionUID= -3494375334388400089L;

	public JsEvalInMethod(ReferenceHolder referenceHolder, String script, String methodName)
	{
		super(referenceHolder, methodName, script);
	}
}
