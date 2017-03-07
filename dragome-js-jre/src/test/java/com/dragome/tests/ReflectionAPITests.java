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

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;

import junit.framework.TestCase;

@RunWith(DragomeTestRunner.class)
public class ReflectionAPITests extends TestCase
{
	@Test
	public void testInstanceOfIntArray() throws Exception
	{
		int[] object= new int[10];
		boolean a= object instanceof int[];
		assertEquals(true, a);
	}

	@Test
	public void testInstanceOfDoubleArray() throws Exception
	{
		double[] object= new double[10];
		boolean a= object instanceof double[];
		assertEquals(true, a);
	}

	@Test
	public void testInstanceOfStringArray() throws Exception
	{
		String[] object= new String[10];
		boolean a= object instanceof String[];
		assertEquals(true, a);
	}

	@Test
	public void testNullInstanceOfIntIsNotAnArray() throws Exception
	{
		int[] object= null;
		boolean a= object instanceof int[];
		assertEquals(false, a);
	}

	public static class ReflectionClass2
	{
		private boolean field1= true;
		public int field2;

		public boolean getField1()
		{
			return field1;
		}
	}

	@Retention(RetentionPolicy.RUNTIME)
	public @interface Annotation1
	{
		String value1() default "1";
		String value2() default "1";
		String[] value3() default { "1", "2" };
		Class<?> value4() default SuperClass.class;
	}

	@Annotation1
	public class SuperClass implements ReflectionInterface1, ReflectionInterface2
	{
	}

	@Annotation1(value1= "ReflectionInterface1")
	public interface ReflectionInterface1
	{
	}

	public interface ReflectionInterface2
	{
	}

	@Annotation1(value1= "ReflectionClass")
	public class ReflectionClass extends SuperClass
	{
		@Annotation1(value1= "value1:field1")
		public boolean field1;

		@Annotation1(value4= Number.class)
		public boolean field2;

		@Annotation1(value3= { "3", "4" })
		public boolean field3;

		@Annotation1(value1= "methodWithNoArguments")
		public void methodWithNoArguments()
		{
		}

		@Annotation1
		public void methodWithNoArgumentsAnnotatedWithNoArguments()
		{
		}

		public String methodWithIntegerArgument(@Annotation1(value1= "methodWithIntegerArgument_i") Integer i)
		{
			return i + "";
		}

		public void overridenMethod(int i)
		{
		}

		public void overridenMethod(@Annotation1(value1= "value1=overridenMethod_s", value2= "value2=overridenMethod_s") String s, @Annotation1(value1= "overridenMethod_l") Long l)
		{
		}

		public List<String> methodWithGenericReturn()
		{
			return null;
		}
	}

	@Test
	public void testSuperClassIsInstanceOfInterfaces() throws Exception
	{
		final SuperClass sc= new SuperClass();
		assertTrue(ReflectionInterface1.class.isInstance(sc));
		assertTrue(ReflectionInterface2.class.isInstance(sc));
	}

	@Test
	public void testSearchingForMethodWithNoArgumentsReturnsMethodWithSameName() throws Exception
	{
		Method method= ReflectionClass.class.getMethod("methodWithNoArguments", null);
		assertEquals("methodWithNoArguments", method.getName());
	}

	@Test
	public void testSearchingForMethodWithIntegerArgumentReturnsRightOne() throws Exception
	{
		Method method= ReflectionClass.class.getMethod("methodWithIntegerArgument", Integer.class);
		assertEquals("methodWithIntegerArgument", method.getName());
		assertEquals(Integer.class, method.getParameterTypes()[0]);
	}

	@Test
	public void testGetReturnTypeReturnsRightOne() throws Exception
	{
		Method method= ReflectionClass.class.getMethod("methodWithIntegerArgument", Integer.class);
		assertEquals(String.class, method.getReturnType());
	}

	@Test
	public void testGetGenericReturnTypeOfStringListReturnsStringAsFirstType() throws Exception
	{
		Method method= new ReflectionClass().getClass().getMethod("methodWithGenericReturn");
		Type genericReturnType= method.getGenericReturnType();
		Type[] actualTypeArguments= ((ParameterizedType) genericReturnType).getActualTypeArguments();
		assertEquals(String.class, actualTypeArguments[0]);
	}

	@Test
	public void testFoundOverridenMethodsMatchTheirArguments() throws Exception
	{
		Method overridenMethodStringLong= ReflectionClass.class.getMethod("overridenMethod", String.class, Long.class);
		Method overridenMethodInt= ReflectionClass.class.getMethod("overridenMethod", int.class);

		assertEquals(int.class, overridenMethodInt.getParameterTypes()[0]);
		assertEquals(String.class, overridenMethodStringLong.getParameterTypes()[0]);
		assertEquals(Long.class, overridenMethodStringLong.getParameterTypes()[1]);
	}

	@Test
	public void testInvokeMethodWithIntegerArgumentReturnsIntegerAsString() throws Exception
	{
		Method method= ReflectionClass.class.getMethod("methodWithIntegerArgument", Integer.class);
		String result= (String) method.invoke(new ReflectionClass(), 7);

		assertEquals("7", result);
	}

	@Test
	public void testGetMethodsReturnsAllMethods() throws Exception
	{
		Method[] methods= ReflectionClass.class.getMethods();

		Map<String, Method> methodsMap= new HashMap<String, Method>();
		for (Method method : methods)
			methodsMap.put(method.getName(), method);

		assertTrue(4 <= methods.length);
		assertNotNull(methodsMap.get("methodWithNoArguments"));
		assertNotNull(methodsMap.get("methodWithIntegerArgument"));
		assertNotNull(methodsMap.get("overridenMethod"));
	}

	@Test
	public void testIsInterfaceOverClassIsFalse() throws Exception
	{
		assertFalse(ReflectionClass.class.isInterface());
	}

	@Test
	public void testIsInterfaceOverInterfaceIstrue() throws Exception
	{
		assertTrue(ReflectionInterface1.class.isInterface());
	}

	@Test
	public void testGetSuperClassReturnsRightOne() throws Exception
	{
		assertEquals(SuperClass.class, ReflectionClass.class.getSuperclass());
	}

	@Test
	public void testGetInterfacesOfNonImplementorClassReturnsNothing() throws Exception
	{
		Class<?>[] interfaces= ReflectionClass.class.getInterfaces();
		assertEquals(0, interfaces.length);
	}

	@Test
	public void testGetInterfacesOfImplementorClassReturnsRightInterface() throws Exception
	{
		Class<?>[] interfaces= ReflectionClass.class.getSuperclass().getInterfaces();
		assertEquals(2, interfaces.length);
		Class interface1= null;
		Class interface2= null;
		// ReflectionInterface1 may not be first so we need to search
		for (int i= 0; i < interfaces.length; i++)
		{
			if (interface1 == null && ReflectionInterface1.class == interfaces[i])
				interface1= interfaces[i];
			if (interface2 == null && ReflectionInterface2.class == interfaces[i])
				interface2= interfaces[i];
		}
		assertEquals(ReflectionInterface1.class, interface1);
		assertEquals(ReflectionInterface2.class, interface2);
	}

	@Test
	public void testIsPrimiteOfIntReturnsTrue() throws Exception
	{
		assertTrue(int.class.isPrimitive());
	}

	@Test
	public void testIsPrimiteOfIntegerReturnsFalse() throws Exception
	{
		assertFalse(Integer.class.isPrimitive());
	}

	@Test
	public void testGetPackageOfStringReturnJavaLang() throws Exception
	{
		assertEquals("java.lang", String.class.getPackage().getName());
	}

	@Test
	public void testForNameOfJavaLangIntegerReturnsInteger() throws Exception
	{
		assertEquals(Integer.class, Class.forName("java.lang.Integer"));
	}

	@Test
	public void testNewInstanceOfJavaLangStringReturnsStringInstance() throws Exception
	{
		String newInstance= String.class.newInstance();
		assertEquals(String.class, newInstance.getClass());
	}

	@Test
	public void testGetSimpleNameReturnsNameWithoutPackage() throws Exception
	{
		assertEquals("String", String.class.getSimpleName());
	}

	@Test
	public void testNumberIsNotAssignableFromString() throws Exception
	{
		assertFalse(Number.class.isAssignableFrom(String.class));
	}

	@Test
	public void testNumberIsAssignableFromInteger() throws Exception
	{
		assertTrue(Number.class.isAssignableFrom(Integer.class));
	}

	@Test
	public void testGettingAnnotationFromInterface() throws Exception
	{
		Annotation1 annotation1= ReflectionInterface1.class.getAnnotation(Annotation1.class);
		assertEquals("ReflectionInterface1", annotation1.value1());
	}

	@Test
	public void testGettingNotParametizedAnnotationFromClass() throws Exception
	{
		Annotation1 annotation1= SuperClass.class.getAnnotation(Annotation1.class);
		assertNotNull(annotation1);
	}

	@Test
	public void testGettingAnnotationFromMethod() throws Exception
	{
		Class<ReflectionClass> class1= ReflectionClass.class;
		Method method= class1.getMethod("methodWithNoArguments", null);
		Annotation1 annotation1= method.getAnnotation(Annotation1.class);
		assertEquals("methodWithNoArguments", annotation1.value1());
	}

	@Test
	public void testGettingNotParametizedAnnotationFromMethod() throws Exception
	{
		Class<ReflectionClass> class1= ReflectionClass.class;
		Method method= class1.getMethod("methodWithNoArgumentsAnnotatedWithNoArguments", null);
		Annotation1 annotation1= method.getAnnotation(Annotation1.class);
		assertNotNull(annotation1);
	}

	@Test
	public void testNotGettingAnnotationFromMethod() throws Exception
	{
		Class<ReflectionClass> class1= ReflectionClass.class;
		Method method= class1.getMethod("methodWithGenericReturn", null);
		Annotation1 annotation1= method.getAnnotation(Annotation1.class);
		assertNull(annotation1);
	}

	@Test
	public void testGettingAnnotationFromParameter() throws Exception
	{
		Class<ReflectionClass> class1= ReflectionClass.class;
		Method method= class1.getMethod("methodWithIntegerArgument", Integer.class);
		Annotation1 annotation1= method.getParameters()[0].getAnnotation(Annotation1.class);
		assertEquals("methodWithIntegerArgument_i", annotation1.value1());
	}

	@Test
	public void testGettingAnnotationFromTwoParameters() throws Exception
	{
		Class<ReflectionClass> class1= ReflectionClass.class;
		Method method= class1.getMethod("overridenMethod", String.class, Long.class);
		Annotation1 annotation1= method.getParameters()[0].getAnnotation(Annotation1.class);
		Annotation1 annotation2= method.getParameters()[1].getAnnotation(Annotation1.class);
		assertEquals("value1=overridenMethod_s", annotation1.value1());
		assertEquals("value2=overridenMethod_s", annotation1.value2());
		assertEquals("overridenMethod_l", annotation2.value1());
	}

	@Test
	public void testGettingAnnotationFromField() throws Exception
	{
		Class<ReflectionClass> class1= ReflectionClass.class;
		Field field= class1.getField("field1");
		Annotation1 annotation1= field.getAnnotation(Annotation1.class);
		assertEquals("value1:field1", annotation1.value1());
	}

	@Test
	public void testGettingAnnotationOfTypeClassFromField() throws Exception
	{
		Class<ReflectionClass> class1= ReflectionClass.class;
		Field field= class1.getField("field1");
		Annotation1 annotation1= field.getAnnotation(Annotation1.class);
		assertEquals(SuperClass.class, annotation1.value4());

		Field field2= class1.getField("field2");
		Annotation1 annotation2= field2.getAnnotation(Annotation1.class);
		assertEquals(Number.class, annotation2.value4());
	}

	@Test
	public void testGettingAnnotationDefaultValueFromField() throws Exception
	{
		Class<ReflectionClass> class1= ReflectionClass.class;
		Field field= class1.getField("field1");
		Annotation1 annotation1= field.getAnnotation(Annotation1.class);
		assertEquals("1", annotation1.value2());
	}

	@Test
	public void testGettingAnnotationArrayFromField() throws Exception
	{
		Class<ReflectionClass> class1= ReflectionClass.class;
		Field field3= class1.getField("field3");
		Annotation1 annotation1= field3.getAnnotation(Annotation1.class);
		String[] value3= annotation1.value3();
		assertEquals("4", value3[1]);
	}

	@Test
	public void testGettingNotParametizedAnnotationFromField() throws Exception
	{
		Class<ReflectionClass> class1= ReflectionClass.class;
		Field field= class1.getField("field2");
		Annotation1 annotation1= field.getAnnotation(Annotation1.class);
		assertNotNull(annotation1);
	}

	@Test
	public void testSetFieldWithTrue() throws Exception
	{
		Field field= ReflectionClass.class.getField("field1");
		ReflectionClass obj= new ReflectionClass();
		field.set(obj, true);
		assertEquals(true, obj.field1);
	}

	@Test
	public void testSetFieldPrivateWithFalse() throws Exception
	{
		Field field= ReflectionClass2.class.getDeclaredField("field1");
		field.setAccessible(true);
		ReflectionClass2 obj= new ReflectionClass2();
		field.set(obj, false);
		obj.field1= false;
		assertEquals(false, obj.field1);
	}

	@Test
	public void testGetFieldWithTrue() throws Exception
	{
		Field field= ReflectionClass.class.getField("field1");
		ReflectionClass obj= new ReflectionClass();
		obj.field1= true;
		Object boolValue= field.get(obj);
		assertEquals(true, boolValue);
	}

	@Test
	public void testGetFieldPrivateWithTrue() throws Exception
	{
		Field field= ReflectionClass2.class.getDeclaredField("field1");
		field.setAccessible(true);
		ReflectionClass2 obj= new ReflectionClass2();
		Object boolValue= field.get(obj);
		assertEquals(true, boolValue);
	}

	@Test
	public void testGetFieldInteger() throws Exception
	{
		Field field= ReflectionClass2.class.getField("field2");
		ReflectionClass2 obj= new ReflectionClass2();
		obj.field2= 10;
		Object intValue= field.get(obj);
		assertEquals(10, intValue);
	}

	@Test
	public void testGettingTypeOfField() throws Exception
	{
		Class<ReflectionClass> class1= ReflectionClass.class;
		Field field= class1.getField("field1");
		Class<?> fieldType= field.getType();
		assertEquals(boolean.class, fieldType);
	}

	public static abstract class Component
	{
	}

	public static abstract class BaseComponentMapper<A extends Component>
	{
	}

	public static class ComponentMapper<A extends Component> extends BaseComponentMapper<A>
	{
	}

	public static class CpBdxObject extends Component
	{
	}

	public static class ParameterizedComponent<A> extends Component
	{
		A obj;
	}

	public static class TestService
	{
		ComponentMapper<CpBdxObject> mMapGameobject;
		ComponentMapper<ParameterizedComponent<Integer>> mMapCharacterProps;
	}

	@Test
	public void testParameterizedTypeActualTypeInstanceofClass() throws Exception
	{
		final Class<?> c= TestService.class;
		final Field[] fields= c.getDeclaredFields();
		for (final Field f : fields)
		{
			if (f.getName() == "mMapGameobject")
			{
				final Type genericType= f.getGenericType();
				assertTrue(genericType instanceof ParameterizedType);
				final Type[] actualTypes= ((ParameterizedType) genericType).getActualTypeArguments();
				assertEquals(actualTypes.length, 1);
				final Type actualType= actualTypes[0];
				assertTrue(actualType instanceof Class);
				assertEquals((Class<?>) actualType, CpBdxObject.class);
				return;
			}
		}
		fail();
	}

	@Test
	public void testParameterizedTypeActualTypeInstanceofParameterizedType() throws Exception
	{
		final Class<?> c= TestService.class;
		final Field[] fields= c.getDeclaredFields();
		for (final Field f : fields)
		{
			if (f.getName() == "mMapCharacterProps")
			{
				final Type genericType= f.getGenericType();
				assertTrue(genericType instanceof ParameterizedType);
				final Type[] actualTypes= ((ParameterizedType) genericType).getActualTypeArguments();
				assertEquals(actualTypes.length, 1);
				final Type actualType= actualTypes[0];
				assertTrue(actualType instanceof ParameterizedType);
				final Class<?> ret= (Class<?>) ((ParameterizedType) actualType).getRawType();
				assertEquals(ret, ParameterizedComponent.class);
				return;
			}
		}
		fail();
	}

	// TODO need write test for case where ParameterizedType instance Of GenericArrayType 
	// https://github.com/junkdog/artemis-odb/blob/master/artemis/src/main/java/com/artemis/utils/reflect/Field.java#L117

	////////////////////////////////////////////
	// Support default keyword in declaring an annotation type
	// https://github.com/dragome/dragome-sdk/issues/159
	////////////////////////////////////////////

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ ElementType.FIELD, ElementType.TYPE })
	@Documented
	public static @interface Wire
	{
		boolean injectInherited() default false;
		boolean failOnNull() default true;
		String name() default "test";
	}

	public static class Const
	{
		public static final String NAME_VAL= "same.name";
	}

	public static class SameStaticMember
	{
		public int mVal;
	}

	public static class SameMember
	{
		public int mVal;
	}

	public static class SameSystem
	{
		@Wire(name= Const.NAME_VAL)
		static public SameStaticMember mSameStaticMember;

		@Wire(failOnNull= false)
		public SameMember mSameMember;
	}

	@Test
	public void testGetDeclaredFieldsForStaticField() throws Exception
	{
		final Class<?> c= SameSystem.class;

		{
			final Field field= c.getDeclaredField("mSameStaticMember");
			assertNotNull(field);
			final Wire wire= field.getAnnotation(Wire.class);
			assertNotNull(wire);

			assertEquals(wire.name(), Const.NAME_VAL);
			assertTrue(wire.failOnNull());
			assertFalse(wire.injectInherited());
		}

		{
			final Field field= c.getDeclaredField("mSameMember");
			assertNotNull(field);
			final Wire wire= field.getAnnotation(Wire.class);
			assertNotNull(wire);

			assertEquals(wire.name(), "test");
			assertFalse(wire.failOnNull());
			assertFalse(wire.injectInherited());
		}

	}

	@Test
	public void testGetMethodNoParamCall() throws Exception
	{
		long expected= System.currentTimeMillis();
		Method method= System.class.getMethod("currentTimeMillis");
		Object invoke= method.invoke(null);
		Long toExpect= (Long) invoke;
		assertTrue(toExpect >= expected);
	}

	@Test
	public void testSupportStaticFieldAnnotationType() throws Exception
	{
		final Class<?> c= SameSystem.class;
		final Field[] fields= c.getDeclaredFields();
		for (final Field f : fields)
		{
			if (f.getName() == "mSameStaticMember")
			{
				return;
			}
		}
		fail();
	}

	@Test
	public void testSupportSetField() throws Exception
	{
		final Class<?> c= SameSystem.class;
		final SameSystem sameSystem= new SameSystem();
		{
			final Field field= c.getDeclaredField("mSameStaticMember");
			assertNotNull(field);
			assertTrue(Modifier.isStatic(field.getModifiers()));

			final SameStaticMember sameStaticMember= new SameStaticMember();
			sameStaticMember.mVal= 123;
			field.set(sameSystem, sameStaticMember);
		}

		{
			final Field field= c.getDeclaredField("mSameMember");
			assertNotNull(field);
			assertFalse(Modifier.isStatic(field.getModifiers()));

			final SameMember sameMember= new SameMember();
			sameMember.mVal= 234;
			field.set(sameSystem, sameMember);
		}

		assertNotNull(sameSystem.mSameStaticMember);
		assertNotNull(sameSystem.mSameMember);

		assertEquals(sameSystem.mSameStaticMember.mVal, 123);
		assertEquals(sameSystem.mSameMember.mVal, 234);

		{
			final SameSystem sameSystemTemp= new SameSystem();
			sameSystemTemp.mSameStaticMember= new SameStaticMember();
			sameSystemTemp.mSameStaticMember.mVal= 543;

			assertEquals(sameSystem.mSameStaticMember.mVal, 543);
		}
	}

}
