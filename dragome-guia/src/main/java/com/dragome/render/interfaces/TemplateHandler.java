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
package com.dragome.render.interfaces;

import java.util.List;

import com.dragome.templates.interfaces.Template;

public interface TemplateHandler
{
	public Template clone(Template mainPanel);
	public void makeVisible(Template template);
	public void markWith(Template child, String name);
	public void releaseTemplate(Template template);
	public List<Template> cloneTemplates(List<Template> templates);
	void makeInvisible(Template template);
	boolean isConnected(Template aTemplate);
}
