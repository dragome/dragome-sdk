/*
 * Copyright 2009 Andrew Pietsch 
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you 
 * may not use this file except in compliance with the License. You may 
 * obtain a copy of the License at 
 *      
 *      http://www.apache.org/licenses/LICENSE-2.0 
 *
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or 
 * implied. See the License for the specific language governing permissions 
 * and limitations under the License. 
 */

package com.dragome.forms.bindings.client.form;

import com.dragome.forms.bindings.client.value.ValueModel;

/**
 * ValueModelProvider marks objects that vend {@link ValueModel} instances based on 
 * a simple name and type.  This interface is typically used to obtain value models
 * from sources such as bean properties.
 */
public interface ValueModelProvider<K>
{
	/**
	 * Gets the {@link ValueModel} of the specified type and identified by  'name'.
	 * @param key the key used to identify the value model.
	 * @param valueType the type held by the value model.
	 * @return a value model for the specified name and type.
	 */
	<T> ValueModel<T> getValueModel(K key, Class<T> valueType);
}
