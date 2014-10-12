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
package com.dragome.model.interfaces;

import java.util.Collection;

import com.dragome.model.interfaces.list.HasListModel;


public interface VisualListBox<T> extends VisualComponent, HasConstrainedValue<T>, IsEditor<TakesValueEditor<T>>, HasRenderer<T>, HasListModel<T>
{
	boolean isMultipleItems();
	void setMultipleItems(boolean multipleItems);
	void setSelectedValues(Iterable<T> selectedValues);
	Collection<T> getSelectedValues();
}
