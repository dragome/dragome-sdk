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

import com.dragome.model.interfaces.Layout;
import com.dragome.templates.interfaces.Template;

public class RepeatTemplateLayout extends TemplateLayout implements Layout
{
	protected boolean horizontal;
	protected String contentTemplateAlias;
	protected String templateToRepeatAlias;

	public RepeatTemplateLayout()
	{
	}

	public RepeatTemplateLayout(Template template, String templateToRepeatAlias, String contentTemplateAlias, boolean horizontal)
	{
		this(template, templateToRepeatAlias, contentTemplateAlias);
		this.horizontal= horizontal;
	}

	public RepeatTemplateLayout(boolean horizontal)
	{
		this.horizontal= horizontal;
	}

	public RepeatTemplateLayout(Template template, String templateToRepeatAlias, String contentTemplateAlias)
	{
		this.template= template;
		this.templateToRepeatAlias= templateToRepeatAlias;
		this.contentTemplateAlias= contentTemplateAlias;
	}

	public boolean isHorizontal()
	{
		return horizontal;
	}

	public void setHorizontal(boolean horizontal)
	{
		this.horizontal= horizontal;
	}

	public String getContentTemplateAlias()
	{
		return contentTemplateAlias;
	}

	public void setContentTemplateAlias(String contentPlace)
	{
		this.contentTemplateAlias= contentPlace;
	}

	public String getTemplateToRepeatAlias()
	{
		return templateToRepeatAlias;
	}

	public void setTemplateToRepeatAlias(String templateToRepeatAlias)
	{
		this.templateToRepeatAlias= templateToRepeatAlias;
	}
}
