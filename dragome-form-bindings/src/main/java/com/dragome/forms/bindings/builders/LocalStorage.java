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
