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
package com.dragome.web.serverside.servlets;

import java.io.File;
import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.dragome.commons.compiler.classpath.Classpath;
import com.dragome.services.ServiceLocator;
import com.dragome.services.WebServiceLocator;
import com.dragome.web.serverside.compile.watchers.DirectoryWatcher;

//@WebServlet(loadOnStartup= 1, value= "/compiler-service")
public class CompilerServlet extends GetPostServlet {
	private static Logger LOGGER = Logger.getLogger(CompilerServlet.class.getName());

	protected void doService(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		resp.getWriter().write(DirectoryWatcher.lastCompilation + "");
	}

	public void init() throws ServletException {
		try {
			WebServiceLocator webServiceLocator = WebServiceLocator.getInstance();
			compile();
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}

	private void compile() throws URISyntaxException {
//		try {
//
//			RedefineClassAgent.redefineClasses();
//		} catch (ClassNotFoundException | UnmodifiableClassException e) {
//			e.printStackTrace();
//		}

		try {
			ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
			Class<?> loadClass = contextClassLoader.loadClass("com.dragome.helpers.RedefineClassAgent");

			Object obj = loadClass.newInstance();
			Method method = loadClass.getMethod("ensure");
			method.invoke(obj);

		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | NoSuchMethodException
				| SecurityException | IllegalArgumentException | InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();
			Class<?> loadClass = systemClassLoader.loadClass("com.dragome.helpers.RedefineClassAgent");

			Object obj = loadClass.newInstance();
			Method method = loadClass.getMethod("ensure");
			method.invoke(obj);

			Method getInstrumentationMethod = loadClass.getMethod("getInstrumentation");
			Instrumentation invoke = (Instrumentation) getInstrumentationMethod.invoke(null);

			ClassFileTransformerImplementation transformer = new ClassFileTransformerImplementation();
			invoke.addTransformer(transformer, true);

		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | NoSuchMethodException
				| SecurityException | IllegalArgumentException | InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		ClassLoader c = getClass().getClassLoader();
		final Classpath classPath = new Classpath();
		if (c instanceof URLClassLoader) {
			URLClassLoader urlClassLoader = (URLClassLoader) c;
			URL[] urls = urlClassLoader.getURLs();
			String classesFolder = "";
			for (URL i : urls) {
				String classPathEntry = new File(i.toURI()).toString();
				boolean isClassesFolder = i.toString().endsWith("/classes/") || i.toString().endsWith("/classes");
				boolean addToClasspath = ServiceLocator.getInstance().getConfigurator().filterClassPath(classPathEntry);

				if (addToClasspath)
					classPath.addEntry(classPathEntry);

				if (isClassesFolder)
					classesFolder = classPathEntry;

				LOGGER.log(Level.INFO, "classpath entry: " + classPathEntry);
			}

			String compiledDir = ServiceLocator.getInstance().getConfigurator().getCompiledPath();

			if (compiledDir == null) // if path is not set use the /classes path
				compiledDir = new File(new java.io.File(classesFolder).getParentFile().getParentFile().toURI())
						.toString();

			final String path = compiledDir + File.separator + "compiled-js";

			LOGGER.log(Level.INFO, "classes: " + path);

			final String classesFolder2 = classesFolder;

			new Thread() {
				public void run() {
					DirectoryWatcher.startWatching(new String[] { "-r", classesFolder2 }, classPath, path);
				}
			}.start();
		} else
			LOGGER.log(Level.SEVERE, "Cannot start compiler because there is no URLClassLoader available");
	}
}
