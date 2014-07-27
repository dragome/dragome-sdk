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
