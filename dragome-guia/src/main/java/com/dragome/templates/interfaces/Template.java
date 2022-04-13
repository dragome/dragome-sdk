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

import com.dragome.guia.components.interfaces.VisualComponent;
import com.dragome.model.interfaces.EventProducer;

public interface Template extends EventProducer
{
	public Content<?> getContent();
	void setContent(Content<?> templateContent);
	public void updateContent(Content<?> templateContent);

	public abstract void updateName(String name);
	public abstract String getName();
	void setName(String name);

	public boolean hasChild(String aName);
	public Template getChild(String anAlias);
	public void addChild(Template template);
	public void replaceChild(Template oldChild, Template newChild);
	public void renameChild(Template child, String aName);
	public void removeChild(String name);
	public void removeAll();
	void remove(Template child);
	void insertAfter(Template newChild, Template referenceChild);
	void insertBefore(Template newChild, Template referenceChild);

	public List<Template> getChildren();
	void setChildren(List<Template> children);

	public boolean contains(Template template);

	public Template getParent();
	public void setParent(Template parent);
	public Template getTopParent();

	public void setFiringEvents(boolean firing);
	public void accept(TemplateVisitor templateVisitor);
	public void hide();
	void show();
	public Template createClone();
}
