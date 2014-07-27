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

import java.util.EventListener;

public interface EventProducer
{
	public <T extends EventListener> boolean hasListener(Class<T> aType);
	public <T extends EventListener> void removeListener(Class<T> aType, T aListener);
	public <T extends EventListener> void addListener(Class<T> aType, T aListener);
	public <T extends EventListener> T getListener(Class<T> aType);
}
