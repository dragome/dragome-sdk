package com.dragome.render.serverside.swing;

import java.awt.Component;
import java.awt.Container;
import java.util.List;
import java.util.Map;

import javax.swing.SwingUtilities;

import com.dragome.render.html.renderers.Mergeable;
import com.dragome.templates.interfaces.Content;
import com.dragome.templates.interfaces.Template;
import com.dragome.templates.interfaces.TemplateListener;

public class SwingTemplateListener implements TemplateListener
{
	private boolean enabled= true;

	private boolean isInvokingEvents()
	{
		return enabled;
	}

	public void setEnabled(boolean enabled)
	{
		this.enabled= enabled;
	}

	public void contentChanged(Template template, Content<?> oldTemplateContent, Content<?> newTemplateContent)
	{
		if (isInvokingEvents())
		{
			if (oldTemplateContent != null)
			{
				Component value= (Component) oldTemplateContent.getValue();
				Mergeable<Component> value2= (Mergeable<Component>) newTemplateContent.getValue();

				value2.mergeWith(template, value);
			}
		}
	}

	public void insertAfter(Template newChild, Template referenceChild, Map<String, Template> children, List<Template> childrenList, Template template)
	{
	}

	public void childRemoved(final Template child)
	{
		if (isInvokingEvents())
		{
			SwingUtilities.invokeLater(new Runnable()
			{
				public void run()
				{
					Container component= (Container) child.getContent().getValue();
					Container parent= component.getParent();
					parent.remove(component);
					parent.getParent().validate();
				}
			});
		}
	}

	public void childAdded(Template parent, Template child)
	{
		if (isInvokingEvents())
		{
			Container component= (Container) parent.getContent().getValue();
			Component childComponent= (Component) child.getContent().getValue();
			component.add(childComponent);
		}
	}

	public void insertBefore(Template newChild, Template referenceChild, Map<String, Template> children, List<Template> childrenList, Template template)
	{
		if (isInvokingEvents())
		{
			Component referenceElement= (Component) referenceChild.getContent().getValue();
//			Object componentConstraints= ((MigLayout) referenceElement.getParent().getLayout()).getComponentConstraints(referenceElement);
			Object componentConstraints= null;

			int index= childrenList.indexOf(referenceChild) - 1;
			if (index < 0)
				index= 0;
			childrenList.add(index, newChild);

			children.put(newChild.getName(), newChild);

			Component newElement= (Component) newChild.getContent().getValue();
			referenceElement.getParent().add(newElement, componentConstraints);
			newChild.setParent(template);
		}
	}

	public void childReplaced(Template parent, Template previousChild, Template newChild)
	{
		if (previousChild != newChild)
		{
			System.out.println("child replaced");
		}
	}

	@Override
	public void nameChanged(Template template, String name)
	{
		// TODO Auto-generated method stub
		
	}
}
