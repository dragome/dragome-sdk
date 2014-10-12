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
package com.dragome.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import com.dragome.helpers.collections.CollectionHandler;
import com.dragome.helpers.collections.ItemInvoker;
import com.dragome.model.interfaces.MultiSelectable;
import com.dragome.model.listeners.MultipleSelectionListener;

public class DefaultMultiSelectable implements MultiSelectable
{
	protected List<Object> selectedElements= new Vector<Object>();
	protected CollectionHandler<MultipleSelectionListener> multiSelectionListener= new CollectionHandler<MultipleSelectionListener>();
	protected List<?> elements;

	public DefaultMultiSelectable()
	{
		this(new ArrayList<Object>());
		selectedElements= new Vector<Object>();
	}

	public DefaultMultiSelectable(List<?> anElementsContainer)
	{
		this.elements= anElementsContainer;
	}

	public boolean isSelectedIndex(int anIndex)
	{
		Object elementAt= elements.get(anIndex);

		if (elementAt != null)
			return selectedElements.contains(elementAt);

		return false;
	}

	public void clearSelection()
	{
		selectedElements.clear();
		fireSelection();
	}

	public int getSelectedIndex()
	{
		Set selectedIndices= getSelectedIndices();

		if (selectedIndices.isEmpty())
			return -1;
		else
			return ((Integer) selectedIndices.toArray()[0]).intValue();
	}

	public void setSelectedIndex(int aNewSelectedIndex)
	{
		selectedElements.clear();
		if (aNewSelectedIndex != -1)
		{
			selectedElements.add(elements.get(aNewSelectedIndex));
		}
		fireSelection();
	}

	public Set getSelectedIndices()
	{
		int x= 0;
		Set selectedIndices= new HashSet();
		for (Iterator i= elements.iterator(); i.hasNext(); x++)
		{
			Object element= i.next();

			if (selectedElements.contains(element))
				selectedIndices.add(new Integer(x));
		}

		return selectedIndices;
	}

	public void setSelectedIndices(Set selectedIndices)
	{
		selectedElements.clear();
		for (Iterator i= selectedIndices.iterator(); i.hasNext();)
		{
			Integer index= (Integer) i.next();
			selectedElements.add(elements.get(index.intValue()));
		}

		fireSelection();
	}
	protected void fireSelection()
	{
		multiSelectionListener.forAll(new ItemInvoker<MultipleSelectionListener>()
		{
			public void invoke(MultipleSelectionListener item)
			{
				item.multipleSelectionPerformed(DefaultMultiSelectable.this);
			}
		});
	}

	public void addSelectionListener(MultipleSelectionListener aListener)
	{
		multiSelectionListener.add(aListener);
	}

	public void removeSelectionListener(MultipleSelectionListener aListener)
	{
		multiSelectionListener.remove(aListener);
	}

	public List getSelectedElements()
	{
		return selectedElements;
	}

	public void setSelectedElements(List aSelectedElements)
	{
		if (!selectedElements.equals(aSelectedElements))
		{
			selectedElements= aSelectedElements;
			fireSelection();
		}
	}

	public Object getSelectedElement()
	{
		return elements.get(getSelectedIndex());
	}

	public void setSelectedElement(Object anElement)
	{
		selectedElements.clear();
		selectedElements.add(anElement);

		fireSelection();
	}

	public void removeSelectionListeners()
	{
		multiSelectionListener.removeAll();
	}

	public List<?> getElements()
	{
		return elements;
	}

	public void setElements(List<?> elements)
	{
		this.elements= elements;
	}
}
