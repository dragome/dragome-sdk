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
package com.dragome.templates.interfaces;

import java.util.List;

public interface TemplateLoadingStrategy
{
	void loadMainTemplate(String templateName);
	void hideContainer();
	void setupContainer();
	void showContainer();
	Template loadTemplate(String templateName, String aContainerId);
	Template loadTemplate(String templateName);
	Template getMainTemplate();
	Template loadTemplateCloned(String templateName, String aContainerId);
	List<Template> findAllTemplates();
}
