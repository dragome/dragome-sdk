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

import java.util.HashSet;
import java.util.Set;

import com.dragome.forms.bindings.client.value.MutableValueModel;
import com.dragome.model.interfaces.HandlerRegistration;
import com.dragome.model.interfaces.ValueChangeHandler;

public class NullMutableValueModel<V> implements MutableValueModel<V>
{
	private Set<MethodVisitedEvent> methodVisitedEvents= new HashSet<MethodVisitedEvent>();

	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<V> handler)
	{
		return null;
	}
	public V getValue()
	{
		startRecording();
		V value= getDelegatedValue();
		stopRecording();
		return value;
	}

	public V getDelegatedValue()
	{
		return null;
	}

	public void setValue(V value)
	{
	}

	protected void startRecording()
	{
		if (BindingSync.recordingFor.isEmpty())
			BindingSync.recordingFor.push(this);
	}

	protected void stopRecording()
	{
		if (BindingSync.recordingFor.size() == 1)
			BindingSync.recordingFor.pop();
	}
	
	public Set<MethodVisitedEvent> getMethodVisitedEvents()
	{
		return methodVisitedEvents;
	}
	public void addMethodVisitedEvent(MethodVisitedEvent methodVisitedEvent)
    {
		methodVisitedEvents.add(methodVisitedEvent);
    }

}
