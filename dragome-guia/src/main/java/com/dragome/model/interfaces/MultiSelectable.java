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
package com.dragome.model.interfaces;

import java.util.List;
import java.util.Set;

import com.dragome.model.listeners.MultipleSelectionListener;

public interface MultiSelectable
{
	public boolean isSelectedIndex(int anIndex);
	public void clearSelection();
	public int getSelectedIndex();
	public void setSelectedIndex(int anIndex);
	public Set<?> getSelectedIndices();
	public void setSelectedIndices(Set<?> selectedIndices);
	public void addSelectionListener(MultipleSelectionListener aListener);
	public void removeSelectionListener(MultipleSelectionListener aListener);
	public List<?> getSelectedElements();
	public void setSelectedElements(List<?> aSelectedElements);
	public void setSelectedElement(Object anElement);
	public Object getSelectedElement();
	public void removeSelectionListeners();
	public List<?> getElements();
	public void setElements(List<?> elements);
}
