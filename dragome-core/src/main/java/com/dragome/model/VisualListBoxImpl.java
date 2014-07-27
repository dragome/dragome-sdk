/*******************************************************************************
 * Copyright (c) 2011-2014 Fernando Petrola
 * 
 * This file is part of Dragome SDK.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package com.dragome.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.dragome.model.interfaces.Renderer;
import com.dragome.model.interfaces.TakesValueEditor;
import com.dragome.model.interfaces.ValueChangeEvent;
import com.dragome.model.interfaces.ValueChangeHandler;
import com.dragome.model.interfaces.VisualListBox;
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
	{
		for (int i= 0; i < acceptableValues.size(); i++)
		{
			if (acceptableValues.get(i).equals(value))
				selectedIndex= i;
		}

		if (this.listModel != null)
			this.listModel.setElements(Arrays.asList(value));

		if (fireEvents)
		{
			fireValueChange(value);
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
