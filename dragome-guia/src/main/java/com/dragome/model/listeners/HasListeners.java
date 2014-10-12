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
package com.dragome.model.listeners;

import java.util.List;

public interface HasListeners<T>
{
	public void addListener(T aListener);
	public void removeListener(T aListener);
	public boolean hasListeners(T aClass);
	public List<T> getListeners(T aListenerClass);
	public void fireEvent(T aListenerClass);
}
