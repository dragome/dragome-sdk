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

import java.util.List;

public interface VisualPanel extends VisualComponent, HasLayout
{
	public VisualPanel addChild(VisualComponent aVisualComponent);
	public List<? extends VisualComponent> getChildren();
	public void setChildren(List<? extends VisualComponent> children);
	public void removeChild(VisualComponent aVisualComponent);
	public VisualComponent getChildByName(String aName);
	public void replaceChild(VisualComponent child);
	void addOrReplaceChild(VisualComponent child);
	public void replaceChild(VisualComponent newChild, VisualComponent oldChild);
	void initLayout(Layout layout);
}
