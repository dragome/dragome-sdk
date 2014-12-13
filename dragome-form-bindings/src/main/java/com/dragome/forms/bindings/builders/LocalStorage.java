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

import com.dragome.commons.javascript.ScriptHelper;
import com.dragome.services.ServiceLocator;

public class LocalStorage
{
	public <T> T load(String aKey)
	{
		ScriptHelper.put("key", aKey, this);
		String serializedValue= (String) ScriptHelper.eval("localStorage.getItem(key)", this);
		if (serializedValue != null && !serializedValue.isEmpty())
			return (T) ServiceLocator.getInstance().getSerializationService().deserialize(serializedValue);
		else
			return null;
	}

	public <T> void save(String aKey, T aValue)
	{
		String serializedValue= ServiceLocator.getInstance().getSerializationService().serialize(aValue);
		ScriptHelper.put("key", aKey, this);
		ScriptHelper.put("serialized", serializedValue, this);
		ScriptHelper.eval("localStorage.setItem(key, serialized)", this);
	}
}
