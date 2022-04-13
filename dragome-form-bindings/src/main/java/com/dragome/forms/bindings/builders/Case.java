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
package com.dragome.forms.bindings.builders;

import java.util.function.Supplier;

import com.dragome.guia.GuiaServiceLocator;
import com.dragome.guia.components.interfaces.VisualComponent;

public class Case
{
	private Supplier<?> supplier;

	public Case()
	{
	}

	public Supplier<?> getSupplier()
	{
		return supplier;
	}

	public void setSupplier(Supplier<?> supplier)
	{
		this.supplier= supplier;
	}

	public VisualComponent getComponent()
	{
		return component;
	}

	public void setComponent(VisualComponent component)
	{
		this.component= component;
	}

	private VisualComponent component;
	private CaseBuilder caseBuilder;

	public CaseBuilder getCaseBuilder()
	{
		return caseBuilder;
	}

	public void setCaseBuilder(SingleComponentCaseBuilder caseBuilder)
	{
		this.caseBuilder= caseBuilder;
	}

	private boolean defaultCase;
	private BaseBuilder templateBindingBuilder;
	private boolean built;

	public boolean isDefaultCase()
	{
		return defaultCase;
	}

	public void setDefaultCase(boolean defaultCase)
	{
		this.defaultCase= defaultCase;
	}

	public Case(Supplier<?> supplier, VisualComponent component)
	{
		this.supplier= supplier;
		this.component= component;
	}

	public Case(SingleComponentCaseBuilder caseBuilder, boolean defaultCase, TemplateBindingBuilder templateBindingBuilder)
	{
		this.caseBuilder= caseBuilder;
		this.defaultCase= defaultCase;
		this.templateBindingBuilder= templateBindingBuilder;
	}

	public Case(Supplier<?> supplier, CaseBuilder caseBuilder, BaseBuilder templateBindingBuilder)
	{
		this.supplier= supplier;
		this.caseBuilder= caseBuilder;
		this.templateBindingBuilder= templateBindingBuilder;
	}

	public void build()
	{
		if (!built)
		{
			component= caseBuilder.build(templateBindingBuilder);
			built= true;
		}
	}

	public void hide()
	{
		GuiaServiceLocator.getInstance().getTemplateHandler().makeInvisible(templateBindingBuilder.getTemplate());
	}

	public void show()
	{
		GuiaServiceLocator.getInstance().getTemplateHandler().makeVisible(templateBindingBuilder.getTemplate());
	}
}
