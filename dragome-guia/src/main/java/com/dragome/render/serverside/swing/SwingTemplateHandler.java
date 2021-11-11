package com.dragome.render.serverside.swing;

import java.awt.Component;
import java.awt.Dimension;
import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.output.ByteArrayOutputStream;

import com.dragome.render.interfaces.TemplateHandler;
import com.dragome.templates.TemplateImpl;
import com.dragome.templates.interfaces.Content;
import com.dragome.templates.interfaces.Template;

public class SwingTemplateHandler implements TemplateHandler
{
	public void makeVisible(Template template)
	{
		((Component) template.getContent().getValue()).setVisible(true);
	}

	public void makeInvisible(Template template)
	{
		Component component= (Component) template.getContent().getValue();
		component.setPreferredSize(new Dimension(1, 1));
		component.setVisible(false);
	}

	public void markWith(Template child, String name)
	{
	}

	public void releaseTemplate(Template template)
	{
	}

	public Template clone(Template mainPanel)
	{
		return cloneTemplate(mainPanel);
	}
	private Template cloneTemplate(Template template)
	{
		Template clonedTemplate= cloneChildren(template);

		return clonedTemplate;
	}

	private Template cloneChildren(Template template)
	{
		Template clonedTemplate= new TemplateImpl(template.getName());
		Content<?> content= template.getContent();
		Component component= (Component) content.getValue();

		Component clonedObject= cloneObjectBySerialization(component);

		clonedTemplate= SwingTemplateLoadingStrategy.createTemplate(clonedObject, clonedTemplate);

		//		clonedTemplate.setFiringEvents(false);
		//		Component clonedObject= cloneObjectBySerialization(component);
		//
		//		clonedTemplate.setContent(new SwingContent(clonedObject));
		//
		//		clonedTemplate.setFiringEvents(true);
		//
		//		for (Template child : template.getChildrenMap().values())
		//		{
		//			Template clonedChild= cloneChildren(child);
		//			clonedTemplate.addChild(clonedChild);
		//		}
		//
		//		clonedTemplate.setFiringEvents(true);

		return clonedTemplate;
	}

	private <T> T cloneObjectBySerialization(T object)
	{
		Object result;
		try
		{
			ByteArrayOutputStream f= new ByteArrayOutputStream();
			ObjectOutputStream ostream= new ObjectOutputStream(f);
			ostream.writeObject(object);
			ostream.close();

			ObjectInputStream in= new ObjectInputStream(new ByteArrayInputStream(f.toByteArray()));
			result= in.readObject();
			in.close();
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}

		return (T) result;
	}

	public List<Template> cloneTemplates(List<Template> templates)
	{
		List<Template> clonedTemplates= new ArrayList<Template>();
		for (Template childTemplate : templates)
			clonedTemplates.add(clone(childTemplate));

		return clonedTemplates;
	}

	@Override
	public boolean isConnected(Template aTemplate)
	{
		// TODO Auto-generated method stub
		return false;
	}
}
