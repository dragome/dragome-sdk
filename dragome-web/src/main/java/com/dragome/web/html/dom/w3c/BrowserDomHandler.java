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
package com.dragome.web.html.dom.w3c;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.dragome.commons.javascript.ScriptHelper;
import com.dragome.web.enhancers.jsdelegate.JsCast;
import com.dragome.web.html.dom.DomHandler;

public class BrowserDomHandler implements DomHandler
{
	public BrowserDomHandler()
	{
	}

	public Document getDocument()
	{
		Object documentNode= ScriptHelper.eval("document", this);
		return JsCast.castTo(documentNode, Document.class);
	}

	public Element getElementBySelector(String selector)
	{
		ScriptHelper.put("selector", selector, this);
		Object object= ScriptHelper.eval("document.querySelectorAll(selector)[0]", this);
		Element result= null;
		if (object != null)
			result= JsCast.castTo(object, Element.class);

		return result;
	}
}
