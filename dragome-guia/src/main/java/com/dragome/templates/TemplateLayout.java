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
package com.dragome.templates;

import com.dragome.guia.GuiaServiceLocator;
import com.dragome.guia.components.interfaces.VisualComponent;
import com.dragome.guia.components.interfaces.VisualPanel;
import com.dragome.guia.events.listeners.interfaces.PanelListener;
import com.dragome.model.interfaces.HasLayout;
import com.dragome.model.interfaces.Layout;
import com.dragome.render.canvas.interfaces.Canvas;
import com.dragome.render.interfaces.ComponentRenderer;
import com.dragome.templates.interfaces.Template;

public class TemplateLayout implements Layout
{
	public static class PanelListenerImpl implements PanelListener
	{
		protected ComponentRenderer<Object, VisualComponent> renderer= GuiaServiceLocator.getInstance().getTemplateManager().getComponentRenderer();
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
