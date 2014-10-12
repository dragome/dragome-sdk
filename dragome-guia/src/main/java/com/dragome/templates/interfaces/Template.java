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

import java.util.Map;

import com.dragome.model.interfaces.EventProducer;

public interface Template extends EventProducer
{
	public Content<?> getContent();
	public void setContent(Content<?> templateContent);

	public Map<String, Template> getChildrenMap();
	public void setChildrenMap(Map<String, Template> templateElements);

	//public Template<T> setChild(String anAlias, Template<T> templateElement);
	public Template getChild(String anAlias);

	public Boolean isInner();
	public void setInner(Boolean inner);
	public Content<?> getElementById(String id);
	public Template getParent();
	public void setParent(Template parent);
	public void addChild(Template template);
	public abstract void setName(String name);
	public abstract String getName();
	public void removeChild(String name);
	public void setFiringEvents(boolean firing);
	public void renameChild(Template child, String aName);
	public boolean hasChild(String aName);
	public void removeAll();
	public void accept(TemplateVisitor templateVisitor);

	void insertAfter(Template newChild, Template referenceChild);
	void remove(Template child);
	void insertBefore(Template newChild, Template referenceChild);
}
