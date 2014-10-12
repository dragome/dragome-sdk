/*******************************************************************************
 * Copyright (c) 2011-2014 Fernando Petrola
 * 
 *  This file is part of Dragome SDK.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package com.dragome.render;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.dragome.helpers.Utils;
import com.dragome.render.interfaces.View;
import com.dragome.templates.TemplateImpl;
import com.dragome.templates.interfaces.Template;
import com.dragome.templates.interfaces.TemplateVisitor;

public abstract class DefaultView<T> implements View<T>
{
	protected List<Template> matchingTemplates;
	protected Template originalTemplate;

	public DefaultView()
	{
	}

	public DefaultView(Template originalTemplate)
	{
		this.originalTemplate= originalTemplate;
	}

	protected List<Template> createSubTemplatesFromPaths(String... subTemplates)
	{
		List<Template> result= new ArrayList<Template>();
		for (String subTemplate : subTemplates)
		{
			Template itemTemplate= TemplateImpl.getTemplateElementInDepth(originalTemplate, subTemplate);
			result.add(itemTemplate);
		}

		return result;
	}

	public List<Template> getRequiredSubTemplates(Template template)
	{
		return matchingTemplates;
	}

	public boolean match(final T model, Template baseTemplate)
	{
		matchingTemplates= new ArrayList<Template>();

		baseTemplate.accept(new TemplateVisitor()
		{
			public void visitTemplate(Template aTemplate)
			{
				try
				{
					String name= aTemplate.getName().trim();
					if (name.startsWith("[") && name.endsWith("]"))
					{
						String[] states= name.replace("[", "").replace("]", "").split(";");

						for (String state : states)
						{
							String[] keyValue= state.split("=");
							if (!keyValue[0].equals("template"))
							{
								String camel= Utils.convertDashedToCamel("-" + keyValue[0]);
								try
								{
									if (keyValue[0].contains("event"))
										return;
									Method method= model.getClass().getMethod("is" + camel, null);
									boolean result= (Boolean) method.invoke(model, null);
									if (result != Boolean.parseBoolean(keyValue[1]))
										return;
								}
								catch (Exception e)
								{
									return;
								}
							}
						}
						matchingTemplates.add(aTemplate);
					}
				}
				catch (Exception e)
				{
					throw new RuntimeException(e);
				}
			}
		});

		Collections.sort(matchingTemplates, new Comparator<Template>()
		{
			public int compare(Template o1, Template o2)
			{
				//TODO manejar cuando template no es el ultimo estado 
				return o1.getName().compareTo(o2.getName());
			}
		});

		return true;
	}
	public void composeView(T model, List<Template> templates)
	{
		// TODO Auto-generated method stub

	}
}
