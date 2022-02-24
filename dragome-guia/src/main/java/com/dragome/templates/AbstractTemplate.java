package com.dragome.templates;

import com.dragome.guia.GuiaServiceLocator;
import com.dragome.guia.components.DefaultEventProducer;
import com.dragome.templates.interfaces.Content;
import com.dragome.templates.interfaces.Template;
import com.dragome.templates.interfaces.TemplateListener;
import com.dragome.templates.interfaces.TemplateVisitor;

public abstract class AbstractTemplate extends DefaultEventProducer implements Template
{

	private String name;
	protected TemplateListener templateListener= GuiaServiceLocator.getInstance().getTemplateListener();

	public AbstractTemplate()
	{
		super();
	}

	public String getName()
	{
		return name;
	}

	public void updateName(String name)
	{
		this.setName(name);

		templateListener.nameChanged(this, name);
	}

	public String toString()
	{
		StringBuilder result= new StringBuilder();
		for (Template template : getChildren())
		{
			String childString= template.toString();
			result.append(childString + "//");
		}
		return getName() + ": (" + result.toString() + ")";
	}

	public void setFiringEvents(boolean firingEvents)
	{
		this.templateListener.setEnabled(firingEvents);
	}

	public void accept(TemplateVisitor templateVisitor)
	{
		templateVisitor.visitTemplate(this);
		for (Template template : getChildren())
			template.accept(templateVisitor);
	}

	public void setName(String name)
	{
		this.name= name;
	}

	public boolean contains(Template template)
	{
		if (getChildren().contains(template))
			return true;
		else
			return getChildren().stream().anyMatch(t -> t.contains(template));
	}

	public Template getTopParent()
	{
		return getParent() == null ? this : getParent().getTopParent();
	}

}