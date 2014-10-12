package com.dragome.forms.bindings.builders;

import com.dragome.model.interfaces.VisualComponent;
import com.dragome.services.GuiaServiceLocator;

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

	public void setCaseBuilder(CaseBuilder caseBuilder)
	{
		this.caseBuilder= caseBuilder;
	}

	private boolean defaultCase;
	private TemplateBindingBuilder templateBindingBuilder;
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

	public Case(CaseBuilder caseBuilder, boolean defaultCase, TemplateBindingBuilder templateBindingBuilder)
	{
		this.caseBuilder= caseBuilder;
		this.defaultCase= defaultCase;
		this.templateBindingBuilder= templateBindingBuilder;
	}

	public Case(Supplier<?> supplier, CaseBuilder caseBuilder, TemplateBindingBuilder templateBindingBuilder)
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
