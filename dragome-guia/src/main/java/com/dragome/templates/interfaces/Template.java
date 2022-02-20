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
package com.dragome.templates.interfaces;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.dragome.model.interfaces.EventProducer;

public interface Template extends EventProducer
{
	public Content<?> getContent();
	public void updateContent(Content<?> templateContent);

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
	public abstract void updateName(String name);
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
	public List<Template> getChildren();
	public boolean isActive();
	void setChildren(List<Template> children);
	void setName(String name);
	void setContent(Content<?> templateContent);
	public boolean contains(Template template);
	public Template getTopParent();
}
