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
package com.dragome.web.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.w3c.dom.html.CanvasRenderingContext2D;
import org.w3c.dom.html.HTMLCanvasElement;

import com.dragome.commons.ChainedInstrumentationDragomeConfigurator;
import com.dragome.commons.DragomeConfiguratorImplementor;
import com.dragome.commons.compiler.ClasspathFile;
import com.dragome.commons.compiler.InMemoryClasspathFile;
import com.dragome.web.enhancers.jsdelegate.JsDelegateGenerator;

@DragomeConfiguratorImplementor
public class DomHandlerApplicationConfigurator extends ChainedInstrumentationDragomeConfigurator
{
	protected JsDelegateGenerator jsDelegateGenerator;
	protected List<Class<?>> classes= new ArrayList<>(Arrays.asList(Document.class, Element.class, Attr.class, NodeList.class, //
			Node.class, NamedNodeMap.class, Text.class, HTMLCanvasElement.class, CanvasRenderingContext2D.class));

	public DomHandlerApplicationConfigurator()
	{
	}

	public DomHandlerApplicationConfigurator(List<? extends Class<?>> additionalDelegates)
	{
		classes.addAll(additionalDelegates);
	}

	public List<ClasspathFile> getExtraClasspath(String classpath)
	{
		List<ClasspathFile> result= new ArrayList<ClasspathFile>();
		if (jsDelegateGenerator == null)
		{
			createJsDelegateGenerator(classpath);

			for (Class<?> class1 : classes)
			{
				InMemoryClasspathFile inMemoryClasspathFile= jsDelegateGenerator.generateAsClasspathFile(class1);
				addClassBytecode(inMemoryClasspathFile.getBytecode(), inMemoryClasspathFile.getClassname());
				result.add(inMemoryClasspathFile);
			}
		}

		return result;
	}
	private void createJsDelegateGenerator(String classpath)
	{
		jsDelegateGenerator= new JsDelegateGenerator(classpath.replace(";", ":"), new DomHandlerDelegateStrategy());
	}
}
