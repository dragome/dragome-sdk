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
package com.dragome.guia.components;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.dragome.guia.components.interfaces.VisualListBox;
import com.dragome.model.interfaces.Renderer;
import com.dragome.model.interfaces.TakesValueEditor;
import com.dragome.model.interfaces.ValueChangeEvent;
import com.dragome.model.interfaces.ValueChangeHandler;
import com.dragome.model.interfaces.list.ListModel;
import com.dragome.model.interfaces.list.ListModelChangedEvent;
import com.dragome.model.interfaces.list.ListModelChangedHandler;
import com.dragome.model.interfaces.list.MutableListModel;
import com.dragome.model.pectin.ComponentWithValueAndRendererImpl;

public class VisualListBoxImpl<T> extends ComponentWithValueAndRendererImpl<T> implements VisualListBox<T>
{
	private List<T> acceptableValues= new ArrayList<T>();
	private int selectedIndex;
	private boolean multipleItems;
	private Collection<T> selectedValues= new ArrayList<T>();
	private MutableListModel<T> listModel;

	public MutableListModel<T> getListModel()
	{
		return listModel;
	}

	public Collection<T> getSelectedValues()
	{
		return selectedValues;
	}

	public boolean isMultipleItems()
	{
		return multipleItems;
	}

	public void setMultipleItems(boolean multipleItems)
	{
		this.multipleItems= multipleItems;
	}

	public VisualListBoxImpl()
	{
		super("", new SimpleRenderer<T>());
	}

	public VisualListBoxImpl(String name, Renderer<T> renderer, Iterable<T> acceptableValues)
	{
		super(name, renderer);
		setAcceptableValues(acceptableValues);
	}

	public VisualListBoxImpl(String name, Iterable<T> acceptableValues)
	{
		super(name, new SimpleRenderer<T>());
		setAcceptableValues(acceptableValues);
	}

	public TakesValueEditor<T> asEditor()
	{
		return null;
	}

	public Collection<T> getAcceptableValues()
	{
		return acceptableValues;
	}

	public T getValue()
	{
		return acceptableValues.get(selectedIndex);
	}

	public void setAcceptableValues(Iterable<T> values)
	{
		this.acceptableValues.clear();

		for (T t : values)
			this.acceptableValues.add(t);
	}

	public void setValue(T value, boolean fireEvents)
	{   int lastSelectedIndex= selectedIndex;
	
		for (int i= 0; i < acceptableValues.size(); i++)
		{
			if (acceptableValues.get(i).equals(value))
				selectedIndex= i;
		}

		if (lastSelectedIndex != selectedIndex)
		{
    		if (this.listModel != null)
    			this.listModel.setElements(Arrays.asList(value));
    
    		
    		if (fireEvents)
    		{
    			fireValueChange(value);
    		}
		}
	}

	public void setAcceptableValues(Collection<T> values)
	{
		setAcceptableValues((Iterable) values);
	}

	public void setSelectedValues(Iterable<T> selectedValues)
	{
		this.listModel.setElements((Collection<? extends T>) selectedValues);
	}

	public void setListModel(MutableListModel<T> listModel)
	{
		this.listModel= listModel;
		this.listModel.addListModelChangedHandler(new ListModelChangedHandler<T>()
		{
			public void onListDataChanged(ListModelChangedEvent<T> event)
			{
				List<T> list= ((ListModel<T>) event.getSource()).asUnmodifiableList();
				fireListChangedEvent(list);
			}

		});
	}

	private void fireListChangedEvent(List<T> list)
	{
		ValueChangeHandler listener= getListener(ValueChangeHandler.class);
		if (listener != null)
			listener.onValueChange(new ValueChangeEvent<Collection<T>>(list));
	}
}
