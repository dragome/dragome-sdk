/*******************************************************************************
 * Copyright (c) 2011-2014 Fernando Petrola
 * 
 *  This file is part of Dragome SDK.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package com.dragome.tests;

import org.junit.Test;
import org.junit.runner.RunWith;

import junit.framework.TestCase;

@RunWith(DragomeTestRunner.class)
public class DefaultMethodsTests extends TestCase
{
	public interface A
	{
		default String foo(int a)
		{
			return "Calling A.foo():" + a;
		}

		default String bar(int a)
		{
			return "Calling A.bar():" + a;
		}
	}

	public interface B
	{
		default String foo(int a)
		{
			return "Calling B.foo():" + a;
		}
	}

	public static class AB implements A, B
	{
		public String foo(int a)
		{
			return B.super.foo(a);
		}
	}

	@Test
	public void testSuperDefaultMethodIsCalledCorrectly()
	{
		String foo= new AB().foo(1);
		String bar= new AB().bar(2);

		assertEquals("Calling B.foo():1", foo);
		assertEquals("Calling A.bar():2", bar);
	}
}
