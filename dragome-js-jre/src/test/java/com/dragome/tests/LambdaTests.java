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
// Copyright © 2013-2014 Esko Luontola <www.orfjackal.net>
// This software is released under the Apache License 2.0.
// The license text is at http://www.apache.org/licenses/LICENSE-2.0

package com.dragome.tests;

import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.BinaryOperator;
import java.util.function.Supplier;

import org.junit.Test;
import org.junit.runner.RunWith;

import junit.framework.TestCase;

@RunWith(DragomeTestRunner.class)
public class LambdaTests extends TestCase
{
	@Test
	public void test_empty_lambda() {
		Runnable lambda = () -> {
		};

		lambda.run();
	}

	@Test
	public void test_lambda_returning_a_value() throws Exception
	{
		Callable<String> lambda = () -> "some value";

		assertEquals("some value", lambda.call());
	}

	private interface Function1<IN, OUT> {
		OUT apply(IN value);
	}

	@Test
	public void test_lambda_taking_parameters()
	{
		Function1<String, Integer> lambda = (String s) -> s.getBytes().length;

		assertEquals((Integer) 3, lambda.apply("foo"));
	}

	private int instanceVar = 0;

	@Test
	public void test_lambda_using_instance_variables()
	{
		Runnable lambda = () -> {
			instanceVar = 42;
		};
		lambda.run();

		assertEquals(instanceVar, 42);
	}

	@Test
	public void test_lambda_using_local_variables()
	{
		int[] localVar = new int[1];
		Runnable lambda = () -> {
			localVar[0] = 42;
		};
		lambda.run();

		assertEquals(localVar[0], 42);
	}

	@Test
	public void test_lambda_using_local_variables_of_primitive_types() throws Exception
	{
		boolean bool = true;
		byte b = 2;
		short s = 3;
		int i = 4;
		long l = 5;
		float f = 6;
		double d = 7;
		char c = 8;
		Callable<Integer> lambda = () -> (int) ((bool ? 1 : 0) + b + s + i + l + f + d + c);

		Integer call = lambda.call();
		assertEquals(call, (Integer) 36);
	}

	@Test
	public void test_method_references_to_virtual_methods() throws Exception
	{
		String foo = "foo";
		Callable<String> ref = foo::toUpperCase;

		assertEquals(ref.call(), "FOO");
	}

	@Test
	public void test_method_references_to_interface_methods() throws Exception
	{
		List<String> foos = Arrays.asList("foo");
		Callable<Integer> ref = foos::size;

		Integer result = ref.call();
		assertEquals(result, (Integer) 1);
	}

	@Test
	public void test_method_references_to_static_methods() throws Exception
	{
		long expected = System.currentTimeMillis();
		Callable<Long> ref = System::currentTimeMillis;

		assertTrue(ref.call() >= expected);
	}

	@Test
	public void test_method_references_to_constructors() throws Exception
	{
		Callable<List<String>> ref = ArrayList<String>::new;

		assertTrue(ref.call() instanceof ArrayList);
	}

	class SuperClass
	{
		String inheritedMethod()
		{
			return "superclass version";
		}
	}

	public class SubClass extends SuperClass
	{
		private String t1() throws Exception
		{
			Callable<String> ref = super::inheritedMethod;
			return ref.call();
		}
	}

	@Test
	public void test_method_references_to_overridden_inherited_methods_with_super() throws Exception
	{
		SubClass subClass = new SubClass();

		assertEquals(subClass.t1(), "superclass version");
	}

	String inheritedMethod()
	{
		return "overridden version";
	}

	@Test
	public void test_method_references_to_private_methods() throws Exception
	{
		Callable<String> ref1 = LambdaTests::privateClassMethod;
		assertEquals(ref1.call(), "foo");

		Callable<String> ref2 = this::privateInstanceMethod;
		assertEquals(ref2.call(), "foo");

		// Normal method calls should still work after our magic
		// of making them them accessible from the lambda classes.
		assertEquals(privateClassMethod(), "foo");
		assertEquals(privateInstanceMethod(), "foo");
	}

	public class SomeEntity
	{
		protected String name;

		public SomeEntity(String name)
		{
			this.name = name;
		}

		public int compareTo(SomeEntity entity1)
		{
			return name.compareTo(entity1.name);
		}
	}

	@Test
	public void test_method_reference_to_instance_comparator() throws Exception
	{
		SomeEntity[] a = new SomeEntity[] { new SomeEntity("c"), new SomeEntity("a"), new SomeEntity("b") };

		Arrays.sort(a, SomeEntity::compareTo);

		assertEquals(a[0].name, "a");
		assertEquals(a[1].name, "b");
		assertEquals(a[2].name, "c");
	}

	private String privateInstanceMethod()
	{
		return "foo";
	}

	private static String privateClassMethod()
	{
		return "foo";
	}

	@Test
	public void testLambda_using_instance_variables()
	{
		Runnable lambda = () -> {
			instanceVar = 42;
		};
		lambda.run();

		assertEquals(instanceVar, 42);
	}

	@Test
	public void testLambda_using_local_variables()
	{
		int[] localVar = new int[1];
		Runnable lambda = () -> {
			localVar[0] = 43;
		};
		lambda.run();

		assertEquals(localVar[0], 43);
	}

	@Test
	public void testCreatingBinaryOperatorWithLambda()
	{
		BinaryOperator<Integer> sum = (v1, v2) -> v1 + v2;
		Integer result = sum.apply(1, 2);

		assertEquals(new Integer(3), result);
	}

	@Test
	public void testLambaFromLambda()
	{
		FileFilter[] filters = new FileFilter[] { f -> f.exists(), f -> f.canRead(), f -> f.getName().startsWith("q") };

		Supplier<Runnable> c = () -> () -> {
			instanceVar = 80;
		};
		Runnable runnable = c.get();
		runnable.run();

		assertEquals(80, instanceVar);
	}

	@Test
	public void testNoParametersMethodReferenceAsLambda()
	{
		String[] list = new String[] { "One", "Two", "Three", "Four", "Five", "Six" };

		Comparator<String> upperComparator = Comparator.comparing(String::toUpperCase);

		Arrays.sort(list, upperComparator);

		assertEquals(list[0], "Five");
		assertEquals(list[1], "Four");
		assertEquals(list[2], "One");
		assertEquals(list[3], "Six");
		assertEquals(list[4], "Three");
		assertEquals(list[5], "Two");
	}

	@Test
	public void testUseTwoParametersMethodReferenceAsComparator()
	{
		String[] list = new String[] { "One", "Two", "Three", "Four", "Five", "Six" };
		Arrays.sort(list, (a, b) -> a.substring(1).compareTo(b.substring(1)));

		assertEquals(list[0], "Three");
		assertEquals(list[1], "Five");
		assertEquals(list[2], "Six");
		assertEquals(list[3], "One");
		assertEquals(list[4], "Four");
		assertEquals(list[5], "Two");
	}

	public static int compareFrom2(String a, String b)
	{
		return a.substring(1).compareTo(b.substring(1));
	}

}
