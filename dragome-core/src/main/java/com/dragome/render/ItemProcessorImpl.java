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
package com.dragome.render;

import java.util.Arrays;
import java.util.List;

import com.dragome.render.interfaces.ItemProcessor;
import com.dragome.templates.interfaces.Template;

public abstract class ItemProcessorImpl<T> implements ItemProcessor<T>
{
	private String repeatName;
	private String insertionPointName;
	private String iteratorName;
	protected Template template;

	public ItemProcessorImpl(Template template, String repeatName, String insertionPointName, String iteratorName)
	{
		this.template= template;
		this.repeatName= repeatName;
		this.insertionPointName= insertionPointName;
		this.iteratorName= iteratorName;
	}

	public ItemProcessorImpl(Template template, String repeatName, String insertionPointName)
	{
		this(template, repeatName, insertionPointName, "iterator:" + System.currentTimeMillis());
	}

	public ItemProcessorImpl(Template template, String repeatName)
	{
		this(template, repeatName, repeatName);
	}

	public String getRepeatTemplateName(T item)
	{
		return repeatName;
	}

	public String getInsertionPointName(T item)
	{
		return insertionPointName;
	}

	public String getIteratorName()
	{
		return iteratorName;
	}

	public Template getInsertTemplate(T item)
	{
		return template.getChild(getInsertionPointName(item));
	}

	public List<Template> getRepeatTemplates(T item)
	{
		return Arrays.asList(template.getChild(getRepeatTemplateName(item)));
	}

	public void fillTemplate(T item, Template aTemplate)
	{
	}

	public void fillTemplates(T item, List<Template> aTemplate)
	{
	}
}
