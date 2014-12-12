package com.dragome.forms.bindings.builders;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dragome.render.interfaces.TemplateHandler;
import com.dragome.services.GuiaServiceLocator;
import com.dragome.templates.interfaces.SimpleItemProcessor;
import com.dragome.templates.interfaces.Template;

public class DrangularTemplateRepeater<T>
{
	private List<T> items;
	private List<T> lastItems;
	private Template mainTemplate;
	private String subTemplateName;
	private SimpleItemProcessor<T> simpleItemProcessor;
	private boolean updating;
	private Map<T, Template> clonedChildren= new HashMap<T, Template>();
	private Template child;
	private Template lastInserted;
	private Template parent;

	public DrangularTemplateRepeater()
	{
	}

	public DrangularTemplateRepeater(List<T> items, Template mainTemplate, String subTemplateName, final SimpleItemProcessor<T> simpleItemProcessor, boolean updating)
	{
		this.items= items;
		this.mainTemplate= mainTemplate;
		this.subTemplateName= subTemplateName;
		this.simpleItemProcessor= simpleItemProcessor;
		this.updating= updating;
	}

	public void repeatItems()
	{
		if (!items.equals(lastItems))
		{
			TemplateHandler templateHandler= GuiaServiceLocator.getInstance().getTemplateHandler();
			if (child == null)
			{
				child= mainTemplate.getChild(subTemplateName);
				parent= child.getParent();
			}

			if (lastInserted != null)
				parent.insertAfter(child, lastInserted);

			for (Template template : clonedChildren.values())
				if (template.getParent() != null)
					template.getParent().remove(template);

			if (items.isEmpty())
				lastInserted= null;
			
			for (T item : items)
			{
				Template clonedChild= templateHandler.clone(child);
				clonedChild.setName(clonedChild.getName() + System.currentTimeMillis());
				clonedChildren.put(item, clonedChild);
				simpleItemProcessor.fillTemplate(item, clonedChild);

				parent.insertBefore(clonedChild, child);
				lastInserted= clonedChild;
				templateHandler.makeVisible(clonedChild);
			}

			parent.remove(child);

			lastItems= new ArrayList<T>(items);
		}
	}

	public void clearAndRepeatItems()
	{
		repeatItems();
	}
}