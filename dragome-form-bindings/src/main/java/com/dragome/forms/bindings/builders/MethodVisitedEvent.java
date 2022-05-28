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
package com.dragome.forms.bindings.builders;

public class MethodVisitedEvent
{
	private Object instance;
	private String name;
	private String propertyName;

	public MethodVisitedEvent(Object instance, String name)
	{
		this.instance= instance;
		this.name= name;
		this.propertyName= getPropertyName(name);
	}

	public Object getInstance()
	{
		return instance;
	}

	public String getName()
	{
		return name;
	}

	public int hashCode()
	{
		return name.hashCode();
	}

	public boolean equals(Object obj)
	{
		MethodVisitedEvent methodVisitedEvent= (MethodVisitedEvent) obj;
		return instance == methodVisitedEvent.instance && name.equals(methodVisitedEvent.name);
	}

	private static String getPropertyName(String methodName)
	{
		if (methodName.length() > 3 && (methodName.startsWith("get") || methodName.startsWith("set")))
			return methodName.toLowerCase().charAt(3) + methodName.substring(4);
		else if (methodName.length() > 2 && methodName.startsWith("is"))
			return methodName.toLowerCase().charAt(2) + methodName.substring(3);

		return "";
	}

	public boolean isSameProperty(MethodVisitedEvent event)
	{
		return propertyName.equals(event.propertyName) && instance == event.instance;
	}

}
