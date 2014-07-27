/*
 * Copyright 2010 Andrew Pietsch
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you
 * may not use this file except in compliance with the License. You may
 * obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing permissions
 * and limitations under the License.
 */

package com.dragome.forms.bindings.reflect;

import java.util.HashMap;

import com.dragome.forms.bindings.client.bean.AbstractBeanModelProvider;
import com.dragome.forms.bindings.client.bean.Path;
import com.dragome.forms.bindings.client.bean.PropertyDescriptor;

/**
 * This class provides an implementation of {@link com.pietschy.gwt.pectin.client.bean.BeanModelProvider}
 * that can be used in JVM based tests.  This class <b>can not</b> be used withing client code as it uses reflection.
 */
public class ReflectionBeanModelProvider<B> extends AbstractBeanModelProvider<B>
{
	private BeanDescriptor rootDescriptor;
	private HashMap<String, BeanDescriptor> beanDescriptors= new HashMap<String, BeanDescriptor>();

	public ReflectionBeanModelProvider(Class<B> clazz)
	{
		rootDescriptor= new BeanDescriptor(clazz);
	}

	@Override
	public PropertyDescriptor createPropertyDescriptor(String fullPath)
	{
		ComputedPath path= new ComputedPath(fullPath);

		return getBeanDescriptorFor(path).getPropertyDescriptor(path);
	}

	private BeanDescriptor getBeanDescriptorFor(Path path)
	{
		if (path.isTopLevel())
		{
			return rootDescriptor;
		}
		else
		{
			// we get a descriptor for the parent path, i.e. for the path
			// a.b.c we get the descriptor for a.b which will return a descriptor
			// for the type of b.  This ensures that all paths of the form a.b.?
			// will share the same BeanDescriptor.
			String beanPath= path.getParentPath();

			// try and get the cached version.
			BeanDescriptor descriptor= beanDescriptors.get(beanPath);

			if (descriptor == null)
			{
				// this will call "re-entrant-ly" till we hit the rootDescriptor from
				// which point the path gets built from the bottom up.
				PropertyDescriptor beanDescriptor= createPropertyDescriptor(beanPath);

				Class parentBeanType= beanDescriptor.getValueType();

				ensureLegalNestedType(parentBeanType);

				descriptor= new BeanDescriptor(parentBeanType);

				beanDescriptors.put(beanPath, descriptor);
			}

			return descriptor;
		}
	}

	private void ensureLegalNestedType(Class parentBeanType)
	{
		// todo: implement
	}
}
