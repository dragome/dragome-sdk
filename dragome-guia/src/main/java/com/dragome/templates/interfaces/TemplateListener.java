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

import java.util.EventListener;
import java.util.List;
import java.util.Map;

public interface TemplateListener extends EventListener
{
	void contentChanged(Template template, Content<?> oldTemplateContent, Content<?> newTemplateContent);
	void insertAfter(Template newChild, Template referenceChild, Map<String, Template> children, List<Template> childrenList, Template template);
	void childRemoved(Template child);
	void childAdded(Template parent, Template child);
	void setEnabled(boolean enabled);
	void insertBefore(Template newChild, Template referenceChild, Map<String, Template> children, List<Template> childrenList, Template template);
	void childReplaced(Template parent, Template previousChild, Template newChild);
	void nameChanged(Template template, String name);
}
