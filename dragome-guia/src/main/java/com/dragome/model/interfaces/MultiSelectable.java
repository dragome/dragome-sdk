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
