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
