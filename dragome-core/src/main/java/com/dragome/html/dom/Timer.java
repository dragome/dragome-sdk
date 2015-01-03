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
package com.dragome.html.dom;

import com.dragome.commons.javascript.ScriptHelper;

public class Timer
{
	private Object id;

	public Timer setInterval(final Runnable runnable, int milliseconds)
	{
		Runnable runnableForDebugging= Window.wrapRunnableForDebugging(runnable);
		ScriptHelper.put("ra", runnableForDebugging, this);
		ScriptHelper.put("milliseconds", milliseconds, this);
		id= ScriptHelper.eval("setInterval(function() {ra.$run$void();}, milliseconds)", this);
		return this;
	}

	public Timer setTimeout(final Runnable runnable, int milliseconds)
	{
		Runnable runnableForDebugging= Window.wrapRunnableForDebugging(runnable);
		ScriptHelper.put("ra", runnableForDebugging, this);
		ScriptHelper.put("milliseconds", milliseconds, this);
		id= ScriptHelper.eval("setTimeout(function() {ra.$run$void();}, milliseconds)", this);
		return this;
	}

	public void clearInterval()
	{
		ScriptHelper.put("interval", id, this);
		ScriptHelper.eval("clearInterval(interval)", this);
	}

}