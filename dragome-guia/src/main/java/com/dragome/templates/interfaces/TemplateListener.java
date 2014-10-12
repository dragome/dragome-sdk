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
package com.dragome.templates.interfaces;

import java.util.EventListener;
import java.util.List;
import java.util.Map;

import com.dragome.templates.TemplateImpl;

public interface TemplateListener extends EventListener
{
	void contentChanged(Content<?> oldTemplateContent, Content<?> newTemplateContent);
	void insertAfter(Template newChild, Template referenceChild, Map<String, Template> children, List<Template> childrenList, Template template);
	void childRemoved(Template child);
	void childAdded(Template parent, Template child);
	void setEnabled(boolean enabled);
	void insertBefore(Template newChild, Template referenceChild, Map<String, Template> children, List<Template> childrenList, Template template);
	void childReplaced(TemplateImpl parent, Template previousChild, Template newChild);
}
