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
package java.lang;

import com.dragome.commons.javascript.ScriptHelper;

/*
 * Copyright (c) 2005 j2js.com,
 *
 * All Rights Reserved. This work is distributed under the j2js Software License [1]
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.
 *
 * [1] http://www.j2js.com/license.txt
 */
public class Runtime
{

	/**
	 * Returns the runtime object associated with the current Java application.
	 */
	public static Runtime getRuntime()
	{
		return new Runtime();
	}

	/**
	 * Enables/Disables tracing of method calls.
	 */
	public void traceMethodCalls(boolean on)
	{
		ScriptHelper.put("tracing", on, this);
		ScriptHelper.eval("dragomeJs.tracing = tracing", this);
	}

	public Process exec(java.lang.String cmdline)
	{
		// TODO Auto-generated method stub
		return null;
	}

}
