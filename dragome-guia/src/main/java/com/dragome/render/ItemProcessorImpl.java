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
