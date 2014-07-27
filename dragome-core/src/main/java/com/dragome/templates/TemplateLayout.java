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
package com.dragome.templates;

import com.dragome.model.interfaces.HasLayout;
import com.dragome.model.interfaces.Layout;
import com.dragome.model.interfaces.VisualComponent;
import com.dragome.model.interfaces.VisualPanel;
import com.dragome.model.listeners.PanelListener;
import com.dragome.render.canvas.interfaces.Canvas;
import com.dragome.render.interfaces.ComponentRenderer;
import com.dragome.services.ServiceLocator;
import com.dragome.templates.interfaces.Template;

public class TemplateLayout implements Layout
{
	public static class PanelListenerImpl implements PanelListener
	{
		protected ComponentRenderer<Object, VisualComponent> renderer= ServiceLocator.getInstance().getTemplateManager().getComponentRenderer();
		protected VisualPanel visualPanel;

		private PanelListenerImpl(VisualPanel visualPanel)
		{
			this.visualPanel= visualPanel;
		}
		public void componentAdded(VisualComponent aVisualComponent)
		{
			Template template= getTemplate(aVisualComponent);
			if (template != null)
			{
				if (aVisualComponent.getName() != null)
				{
					template.setName(aVisualComponent.getName());
					getTemplate(this.visualPanel).addChild(template);
				}
			}

			Canvas<Object> render= renderer.render(aVisualComponent);
			if (render != null)
			{
				if (aVisualComponent.getName() != null)
				{
					Template child= getTemplate(this.visualPanel).getChild(aVisualComponent.getName());
					child.setContent(new ContentImpl<Object>(render.getContent()));
				}
			}
		}
		public void componentRemoved(VisualComponent aVisualComponent)
		{
			Template template= getTemplate(aVisualComponent);
			if (template != null)
				template.getParent().remove(template);
		}

		public void childReplaced(VisualComponent oldChild, VisualComponent newChild)
		{
			Template oldTemplate= getTemplate(oldChild);
			if (oldTemplate != null)
			{
				Template newTemplated= getTemplate(newChild);
				if (newTemplated != null)
				{
					newTemplated.setName(oldTemplate.getName());
					oldTemplate.getParent().addChild(newTemplated);
				}
			}
		}

		private Template getTemplate(VisualComponent oldChild)
		{
			if (oldChild instanceof HasLayout && ((HasLayout) oldChild).getLayout() instanceof TemplateLayout)
				return ((TemplateLayout) ((HasLayout) oldChild).getLayout()).getTemplate();
			else
				return null;
		}
	}

	protected Template template;
	protected VisualPanel associatedPanel;

	public TemplateLayout()
	{
	}

	public TemplateLayout(Template template)
	{
		setTemplate(template);
	}

	public Template getTemplate()
	{
		return template;
	}

	public void setTemplate(Template template)
	{
		this.template= template;
	}

	public void setAssociatedPanel(VisualPanel visualPanel)
	{
		this.associatedPanel= visualPanel;
		visualPanel.addListener(PanelListener.class, new PanelListenerImpl(visualPanel));
	}
}
