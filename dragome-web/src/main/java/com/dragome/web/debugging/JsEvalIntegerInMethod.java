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

public class JsEvalIntegerInMethod extends ScriptCrossExecutionCommand implements Serializable
{
	private static final long serialVersionUID= 7458264446673335341L;

	public JsEvalIntegerInMethod(ReferenceHolder referenceHolder, String script, String methodName)
	{
		super(referenceHolder, methodName, script);
	}
}
