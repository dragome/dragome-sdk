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

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.dragome.callbackevictor.serverside;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.dragome.callbackevictor.serverside.javaflow.providers.asmx.AsmxResourceTransformationFactory;
import com.dragome.callbackevictor.serverside.javaflow.providers.asmx.ContinuableClassTransformer;
import com.dragome.callbackevictor.serverside.javaflow.spi.ClasspathResourceLoader;
import com.dragome.callbackevictor.serverside.javaflow.spi.ResourceTransformer;
import com.dragome.commons.compiler.BytecodeTransformer;

/**
 * {@link URLClassLoader} with bytecode instrumentation for javaflow.
 *
 * <p>
 * This class loader is useful where the application can set up multiple
 * class loaders (such as in a container environment,
 * <a href="http://classworlds.codehaus.org/">ClassWorlds</a>, or
 * <a href="http://forehead.werken.com/">Forehead</a>) and when you can
 * isolate the continuation-enabled portion of your application into a separate
 * jar file.
 */
public final class DragomeContinuationClassLoader extends URLClassLoader
{
	private final static Log log= LogFactory.getLog(DragomeContinuationClassLoader.class);

	private final ResourceTransformer transformer;

	/**
	 * Indicates whether the parent class loader should be
	 * consulted before trying to load with this class loader.
	 */
	private boolean parentFirst= true;

	/**
	 * These are the package roots that are to be loaded by the parent class
	 * loader regardless of whether the parent class loader is being searched
	 * first or not.
	 */
	private List systemPackages= new ArrayList();

	/**
	 * These are the package roots that are to be loaded by this class loader
	 * regardless of whether the parent class loader is being searched first
	 * or not.
	 */
	private List loaderPackages= new ArrayList();

	/**
	 * Whether or not this classloader will ignore the base
	 * classloader if it can't find a class.
	 *
	 * @see #setIsolated(boolean)
	 */
	private boolean ignoreBase= false;

	/* The context to be used when loading classes and resources */
	private final AccessControlContext acc;

	private static final int BUFFER_SIZE= 4096;

	protected Set<String> loadFromParent= new HashSet<String>();

	private Map<String, byte[]> bytecodes= new HashMap<String, byte[]>();

	private List<String> loadingClass= new ArrayList<String>();

	private ClassLoader last;

	private BytecodeTransformer bytecodeTransformer;

	/**
	 * Creates a classloader by using the classpath given.
	 *
	 * @param urls
	 *      The URLs from which to load classes and resources
	 * @param parent
	 *      The parent classloader to which unsatisfied loading
	 *      attempts are delegated. May be <code>null</code>,
	 *      in which case the {@link ClassLoader#getSystemClassLoader() system classloader}
	 *      is used as the parent.
	 * @param transformer
	 *      This transformer is used to perform the byte-code enhancement.
	 *      May not be null.
	 */
	public DragomeContinuationClassLoader(URL[] urls, ClassLoader parent, ResourceTransformer transformer)
	{
		super(urls, fixNullParent(parent));
		if (transformer == null)
			throw new IllegalArgumentException();
		this.transformer= transformer;
		acc= AccessController.getContext();
	} 

	public DragomeContinuationClassLoader(URL[] urls, ClassLoader parent, ClassLoader last, BytecodeTransformer bytecodeTransformer, Set<String> loadFromParentList)
	{
		this(urls, parent, new AsmxResourceTransformationFactory().createTransformer(new ClasspathResourceLoader(parent)));
		this.loadFromParent= loadFromParentList;
		this.last= last;
		this.bytecodeTransformer= bytecodeTransformer;
	}

	private static ClassLoader fixNullParent(ClassLoader classLoader)
	{
		if (classLoader != null)
		{
			return classLoader;
		}
		else
		{
			return getSystemClassLoader();
		}
	}

	/**
	 * Control whether class lookup is delegated to the parent loader first
	 * or after this loader. Use with extreme caution. Setting this to
	 * false violates the class loader hierarchy and can lead to Linkage errors
	 *
	 * @param parentFirst if true, delegate initial class search to the parent
	 *                    classloader.
	 */
	public void setParentFirst(boolean parentFirst)
	{
		this.parentFirst= parentFirst;
	}

	/**
	 * Sets whether this classloader should run in isolated mode. In
	 * isolated mode, classes not found on the given classpath will
	 * not be referred to the parent class loader but will cause a
	 * ClassNotFoundException.
	 *
	 * @param isolated Whether or not this classloader should run in
	 *                 isolated mode.
	 */
	public void setIsolated(boolean isolated)
	{
		ignoreBase= isolated;
	}

	/**
	 * Adds a package root to the list of packages which must be loaded on the
	 * parent loader.
	 *
	 * All subpackages are also included.
	 *
	 * @param packageRoot The root of all packages to be included.
	 *                    Should not be <code>null</code>.
	 */
	public synchronized void addSystemPackageRoot(String packageRoot)
	{
		systemPackages.add(appendDot(packageRoot));
	}

	/**
	 * Adds a package root to the list of packages which must be loaded using
	 * this loader.
	 *
	 * All subpackages are also included.
	 *
	 * @param packageRoot The root of all packages to be included.
	 *                    Should not be <code>null</code>.
	 */
	public synchronized void addLoaderPackageRoot(String packageRoot)
	{
		loaderPackages.add(/*appendDot(*/packageRoot/*)*/);
	}

	public synchronized void addLoadFromParent(String packageRoot)
	{
		loadFromParent.add(packageRoot);
	}

	private String appendDot(String str)
	{
		if (str.endsWith("."))
			str+= '.';
		return str;
	}

	/**
	 * Loads a class through this class loader even if that class is available
	 * on the parent classpath.
	 *
	 * This ensures that any classes which are loaded by the returned class
	 * will use this classloader.
	 *
	 * @param classname The name of the class to be loaded.
	 *                  Must not be <code>null</code>.
	 *
	 * @return the required Class object
	 *
	 * @exception ClassNotFoundException if the requested class does not exist
	 *                                   on this loader's classpath.
	 */
	public Class forceLoadClass(String classname) throws ClassNotFoundException
	{
		log.debug("force loading " + classname);

		Class theClass= findLoadedClass(classname);

		if (theClass == null)
		{
			theClass= findClass(classname);
		}

		return theClass;
	}

	/**
	 * Tests whether or not the parent classloader should be checked for
	 * a resource before this one. If the resource matches both the
	 * "use parent classloader first" and the "use this classloader first"
	 * lists, the latter takes priority.
	 *
	 * @param resourceName The name of the resource to check.
	 *                     Must not be <code>null</code>.
	 *
	 * @return whether or not the parent classloader should be checked for a
	 *         resource before this one is.
	 */
	private synchronized boolean isParentFirst(String resourceName)
	{
		// default to the global setting and then see
		// if this class belongs to a package which has been
		// designated to use a specific loader first
		// (this one or the parent one)

		// XXX - shouldn't this always return false in isolated mode?

		boolean useParentFirst= parentFirst;

		for (Iterator itr= systemPackages.iterator(); itr.hasNext();)
		{
			String packageName= (String) itr.next();
			if (resourceName.startsWith(packageName))
			{
				useParentFirst= true;
				break;
			}
		}

		for (Iterator itr= loaderPackages.iterator(); itr.hasNext();)
		{
			String packageName= (String) itr.next();
			if (resourceName.startsWith(packageName))
			{
				useParentFirst= false;
				break;
			}
		}

		return useParentFirst;
	}

	/**
	 * Loads a class with this class loader.
	 *
	 * This class attempts to load the class in an order determined by whether
	 * or not the class matches the system/loader package lists, with the
	 * loader package list taking priority. If the classloader is in isolated
	 * mode, failure to load the class in this loader will result in a
	 * ClassNotFoundException.
	 *
	 * @param classname The name of the class to be loaded.
	 *                  Must not be <code>null</code>.
	 * @param resolve <code>true</code> if all classes upon which this class
	 *                depends are to be loaded.
	 *
	 * @return the required Class object
	 *
	 * @exception ClassNotFoundException if the requested class does not exist
	 * on the system classpath (when not in isolated mode) or this loader's
	 * classpath.
	 */
	protected synchronized Class loadClass(String classname, boolean resolve) throws ClassNotFoundException
	{
		try
		{
			// 'sync' is needed - otherwise 2 threads can load the same class
			// twice, resulting in LinkageError: duplicated class definition.
			// findLoadedClass avoids that, but without sync it won't work.

			Class theClass= findLoadedClass(classname);
			byte[] bs= bytecodes.get(classname);
			if (bs != null)
				theClass= defineClassFromData(bs, classname);

			if (theClass != null)
			{
				return theClass;
			}

			if (classNeedsLoadingFromParent(classname))
			{
				try
				{
					theClass= getParent().loadClass(classname);
					log.debug("Class " + classname + " loaded from parent loader " + "(parentFirst)");
				}
				catch (ClassNotFoundException cnfe)
				{
					theClass= findClass(classname);
					log.debug("Class " + classname + " loaded from ant loader " + "(parentFirst)");
				}
			}
			else
			{
				try
				{
					theClass= findClass(classname);
					log.debug("Class " + classname + " loaded from ant loader");
				}
				catch (ClassNotFoundException cnfe)
				{
					if (ignoreBase)
					{
						throw cnfe;
					}
					theClass= getParent().loadClass(classname);
					log.debug("Class " + classname + " loaded from parent loader");
				}
			}

			if (resolve)
			{
				resolveClass(theClass);
			}

			return theClass;
		}
		finally
		{
			loadingClass.remove(classname);
		}
	}
	private boolean classNeedsLoadingFromParent(String classname)
	{
		for (String packageName : loadFromParent)
		{
			if (classname.startsWith((String) packageName))
				return true;
		}

		return  classname.startsWith("jdk.") || classname.startsWith("javassist.") || classname.startsWith("junit.") || classname.startsWith("sun.") || classname.startsWith("java.") || classname.startsWith("javax.") || classname.startsWith("org.w3c") || classname.startsWith("org.xml") || loadFromParent.contains(classname) || classname.startsWith("org.apache") /* && isParentFirst(classname) */;
	}
//
//	private boolean classNeedsTransformation(String classname)
//	{
//		for (Object packageName : loaderPackages)
//		{
//			if (classname.startsWith((String) packageName))
//				return true;
//		}
//
//		return false;
//	}

	/**
	 * Define a class given its bytes
	 *
	 * @param classData the bytecode data for the class
	 * @param classname the name of the class
	 *
	 * @return the Class instance created from the given data
	 */
	public Class defineClassFromData(final byte[] classData, final String classname)
	{
		return (Class) AccessController.doPrivileged(new PrivilegedAction()
		{
			public Object run()
			{
				// define a package if necessary.
				int i= classname.lastIndexOf('.');
				if (i > 0)
				{
					String packageName= classname.substring(0, i);
					Package pkg= getPackage(packageName);
					if (pkg == null)
					{
						definePackage(packageName, null, null, null, null, null, null, null);
					}
				}

				byte[] newData;
				if (!bytecodeTransformer.requiresTransformation(classname) || loadingClass.contains(classname))
					newData= classData;
				else
				{
					loadingClass.add(classname);
					Thread.currentThread().setContextClassLoader(last);
					newData= transformer.transform(classData);
					Thread.currentThread().setContextClassLoader(DragomeContinuationClassLoader.this);
				}

				ProtectionDomain domain= this.getClass().getProtectionDomain();
				return defineClass(classname, newData, 0, newData.length, domain);
			}
		}, acc);
	}

	/**
	 * Reads a class definition from a stream.
	 *
	 * @param stream The stream from which the class is to be read.
	 *               Must not be <code>null</code>.
	 * @param classname The name of the class in the stream.
	 *                  Must not be <code>null</code>.
	 *
	 * @return the Class object read from the stream.
	 *
	 * @exception IOException if there is a problem reading the class from the
	 * stream.
	 * @exception SecurityException if there is a security problem while
	 * reading the class from the stream.
	 */
	private Class getClassFromStream(InputStream stream, String classname) throws IOException, SecurityException
	{
		ByteArrayOutputStream baos= new ByteArrayOutputStream();
		int bytesRead;
		byte[] buffer= new byte[BUFFER_SIZE];

		while ((bytesRead= stream.read(buffer, 0, BUFFER_SIZE)) != -1)
		{
			baos.write(buffer, 0, bytesRead);
		}

		byte[] classData= baos.toByteArray();
		return defineClassFromData(classData, classname);
	}

	/**
	 * Searches for and load a class on the classpath of this class loader.
	 *
	 * @param name The name of the class to be loaded. Must not be
	 *             <code>null</code>.
	 *
	 * @return the required Class object
	 *
	 * @exception ClassNotFoundException if the requested class does not exist
	 *                                   on this loader's classpath.
	 */
	public Class findClass(final String name) throws ClassNotFoundException
	{
		log.debug("Finding class " + name);

		// locate the class file
		String classFileName= name.replace('.', '/') + ".class";

		InputStream stream= getResourceAsStream(classFileName);
		if (stream == null)
			throw new ClassNotFoundException(name);

		try
		{
			return getClassFromStream(stream, name);
		}
		catch (IOException e)
		{
			throw new ClassNotFoundException(name, e);
		}
		finally
		{
			try
			{
				stream.close();
			}
			catch (IOException e)
			{
				// ignore
			}
		}
	}

	/**
	 * Finds the resource with the given name. A resource is
	 * some data (images, audio, text, etc) that can be accessed by class
	 * code in a way that is independent of the location of the code.
	 *
	 * @param name The name of the resource for which a stream is required.
	 *             Must not be <code>null</code>.
	 * @return a URL for reading the resource, or <code>null</code> if the
	 *         resource could not be found or the caller doesn't have
	 *         adequate privileges to get the resource.
	 */
	public synchronized URL getResource(String name)
	{
		// we need to search the components of the path to see if
		// we can find the class we want.
		if (isParentFirst(name))
		{
			return super.getResource(name);
		}

		// try this class loader first, then parent
		URL url= findResource(name);
		if (url == null)
		{
			url= getParent().getResource(name);
		}
		return url;
	}

	public void addLoaderPackagesRoot(String[] packagesroot)
	{
		for (String packageroot : packagesroot)
			addLoaderPackageRoot(packageroot);
	}

	public void addClassBytecode(byte[] bytecode, String aName)
	{
		bytecodes.put(aName, bytecode);
	}
}
