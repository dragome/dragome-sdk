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
package com.dragome.model.listeners;

import java.util.EventListener;

import com.dragome.model.interfaces.VisualComponent;

public interface DoubleClickListener extends EventListener
{
	void doubleClickPerformed(VisualComponent aVisualComponent);
}
