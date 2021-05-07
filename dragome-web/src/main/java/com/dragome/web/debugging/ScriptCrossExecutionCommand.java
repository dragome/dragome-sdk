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

public class ScriptCrossExecutionCommand extends CrossExecutionCommandImpl
{
	protected String script;

	public ScriptCrossExecutionCommand()
	{
		super();
	}

	public ScriptCrossExecutionCommand(ReferenceHolder callerHolder, String methodName, String script)
	{
		super(callerHolder, methodName);
		this.script= script.replace("\"", "\\\"");
	}

	public String getScript()
	{
		return script;
	}

	public void setScript(String script)
	{
		this.script= script;
	}
}
