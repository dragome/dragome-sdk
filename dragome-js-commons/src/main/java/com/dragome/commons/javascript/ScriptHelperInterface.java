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
package com.dragome.commons.javascript;

public interface ScriptHelperInterface
{
	public void put(String s, Object value, Object caller);

	public void put(String s, boolean value, Object caller);

	public void put(String s, double value, Object caller);

	public Object eval(String script, Object caller);

	public int evalInt(String jsCode, Object caller);

	public long evalLong(String jsCode, Object caller);

	public float evalFloat(String jsCode, Object caller);

	public double evalDouble(String jsCode, Object caller);

	public char evalChar(String jsCode, Object caller);

	public boolean evalBoolean(String jsCode, Object caller);

	public void evalNoResult(String script, Object callerInstance);
}
