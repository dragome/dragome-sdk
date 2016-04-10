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
import java.util.List;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.w3c.dom.XMLHttpRequest;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;
import org.w3c.dom.events.ProgressEvent;
import org.w3c.dom.html.CanvasRenderingContext2D;
import org.w3c.dom.html.HTMLCanvasElement;
import org.w3c.dom.typedarray.ArrayBuffer;
import org.w3c.dom.typedarray.ArrayBufferView;
import org.w3c.dom.typedarray.Float32Array;
import org.w3c.dom.typedarray.Float64Array;
import org.w3c.dom.typedarray.Int16Array;
import org.w3c.dom.typedarray.Int32Array;
import org.w3c.dom.typedarray.Int8Array;
import org.w3c.dom.typedarray.Uint16Array;
import org.w3c.dom.typedarray.Uint32Array;
import org.w3c.dom.typedarray.Uint8Array;
import org.w3c.dom.webgl.WebGLActiveInfo;
import org.w3c.dom.webgl.WebGLBuffer;
import org.w3c.dom.webgl.WebGLContextAttributes;
import org.w3c.dom.webgl.WebGLFramebuffer;
import org.w3c.dom.webgl.WebGLObject;
import org.w3c.dom.webgl.WebGLProgram;
import org.w3c.dom.webgl.WebGLRenderbuffer;
import org.w3c.dom.webgl.WebGLRenderingContext;
import org.w3c.dom.webgl.WebGLShader;
import org.w3c.dom.webgl.WebGLTexture;
import org.w3c.dom.webgl.WebGLUniformLocation;
import org.w3c.dom.websocket.WebSocket;

import com.dragome.commons.ChainedInstrumentationDragomeConfigurator;
import com.dragome.commons.DragomeConfiguratorImplementor;
import com.dragome.commons.compiler.classpath.Classpath;
import com.dragome.commons.compiler.classpath.ClasspathEntry;
import com.dragome.commons.compiler.classpath.ClasspathFile;
import com.dragome.commons.compiler.classpath.InMemoryClasspathFile;
import com.dragome.commons.compiler.classpath.VirtualFolderClasspathEntry;
import com.dragome.web.debugging.MessageEvent;
import com.dragome.web.enhancers.jsdelegate.JsDelegateGenerator;
import com.dragome.web.html.dom.w3c.ArrayBufferFactory;
import com.dragome.web.html.dom.w3c.HTMLCanvasElementExtension;
import com.dragome.web.html.dom.w3c.HTMLImageElementExtension;
import com.dragome.web.html.dom.w3c.TypedArraysFactory;
import com.dragome.web.html.dom.w3c.WebGLRenderingContextExtension;
import com.dragome.web.services.RequestExecutorImpl.XMLHttpRequestExtension;

@DragomeConfiguratorImplementor
public class DomHandlerApplicationConfigurator extends ChainedInstrumentationDragomeConfigurator
{
	protected JsDelegateGenerator jsDelegateGenerator;
	protected List<Class<?>> classes= new ArrayList<>(Arrays.asList(Document.class, Element.class, Attr.class, NodeList.class, //
			Node.class, NamedNodeMap.class, Text.class, HTMLCanvasElement.class, CanvasRenderingContext2D.class, EventTarget.class, //
			EventListener.class, Event.class, ArrayBuffer.class, HTMLImageElementExtension.class, HTMLCanvasElementExtension.class, Event.class, //
			WebGLActiveInfo.class, WebGLBuffer.class, WebGLContextAttributes.class, WebGLFramebuffer.class, //
			WebGLObject.class, WebGLProgram.class, WebGLRenderbuffer.class, WebGLRenderingContext.class, //
			WebGLShader.class, WebGLTexture.class, WebGLUniformLocation.class, WebGLRenderingContextExtension.class, //
			ArrayBuffer.class, ArrayBufferView.class, Float32Array.class, Float64Array.class, Int16Array.class, //
			Int32Array.class, Int8Array.class, Uint16Array.class, Uint32Array.class, Uint8Array.class, //
			ArrayBufferFactory.class, TypedArraysFactory.class, XMLHttpRequest.class, Object.class, ProgressEvent.class, //
			EventTarget.class, Event.class, XMLHttpRequest.class, WebSocket.class, MessageEvent.class, XMLHttpRequestExtension.class));

	public DomHandlerApplicationConfigurator()
	{
	}

	public DomHandlerApplicationConfigurator(List<? extends Class<?>> additionalDelegates)
	{
		classes.addAll(additionalDelegates);
	}

	public List<ClasspathEntry> getExtraClasspath(Classpath classpath)
	{
		List<ClasspathEntry> result= new ArrayList<ClasspathEntry>();
		List<ClasspathFile> classpathFiles= new ArrayList<ClasspathFile>();

		result.add(new VirtualFolderClasspathEntry(classpathFiles));

		if (jsDelegateGenerator == null)
			createJsDelegateGenerator(classpath);

		for (Class<?> class1 : classes)
		{
			InMemoryClasspathFile inMemoryClasspathFile= jsDelegateGenerator.generateAsClasspathFile(class1);
			addClassBytecode(inMemoryClasspathFile.getBytecode(), inMemoryClasspathFile.getClassname());
			classpathFiles.add(inMemoryClasspathFile);
		}

		return result;
	}
	private void createJsDelegateGenerator(Classpath classpath)
	{
		jsDelegateGenerator= new JsDelegateGenerator(classpath.toString().replace(";", ":"), new DomHandlerDelegateStrategy());
	}
}
