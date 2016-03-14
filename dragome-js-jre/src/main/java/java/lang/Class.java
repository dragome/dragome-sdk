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
package java.lang;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.lang.reflect.TypeVariable;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.List;

import com.dragome.commons.compiler.annotations.CompilerType;
import com.dragome.commons.compiler.annotations.DragomeCompilerSettings;
import com.dragome.commons.javascript.JSObject;
import com.dragome.commons.javascript.ScriptHelper;

@DragomeCompilerSettings(CompilerType.Standard)
public final class Class<T> implements java.io.Serializable, java.lang.reflect.GenericDeclaration, java.lang.reflect.Type, java.lang.reflect.AnnotatedElement
{
	public String getSimpleName()
	{
		String simpleName= getName();
		return simpleName.substring(simpleName.lastIndexOf('.') + 1);
	}

	private class AnnotationInvocationHandler2 implements InvocationHandler
	{
		private JSObject<?> object;

		AnnotationInvocationHandler2(JSObject<?> theObject)
		{
			object= theObject;
		}

		public Object invoke(Object proxy, Method method, Object[] args)
		{
			// TODO: Annotation
			String name= method.getName();
			if (name.equals("toString"))
			{
				StringBuffer buffer= new StringBuffer();
				buffer.append("@" + object.get("$signature"));
				return buffer.toString();
			}
			return object.get(method.getName());
			//return "@" + object.get("signature") + "(" + method.getName() + "=13)";
		}
	}

	private static JSObject<Class<?>> classesByName= new JSObject<Class<?>>();
	private static JSObject<Method> foundMethods= new JSObject<Method>();

	protected Object nativeClass;
	protected Method[] methods;
	protected List<Method> declaredMethods;
	protected Method[] declaredMethodsInDepth;
	protected Class<?>[] interfacesCache;

	private Class(Object theNativeClass)
	{
		nativeClass= theNativeClass;
	}

	public String toString()
	{
		return "class " + getName();
	}

	public static Class<?> forName(String className) throws ClassNotFoundException
	{
		Class<?> clazz= classesByName.get(className);
		if (clazz == null)
		{
			if (className.startsWith("[") && className.endsWith(";") )
				className= Array.class.getName();
			
			String jsClassName= className.replaceAll("\\.", "_");
			if ("boolean".equals(jsClassName))
				jsClassName= "java_lang_Boolean";
			else if ("int".equals(jsClassName))
				jsClassName= "java_lang_Integer";
			else if ("void".equals(jsClassName))
				jsClassName= "java_lang_Void";
			else if ("long".equals(jsClassName))
				jsClassName= "java_lang_Long";
			else if ("byte".equals(jsClassName))
				jsClassName= "java_lang_Byte";
			else if ("char".equals(jsClassName))
				jsClassName= "java_lang_Character";
			else if ("float".equals(jsClassName))
				jsClassName= "java_lang_Float";
			else if ("double".equals(jsClassName))
				jsClassName= "java_lang_Double";
			else if ("short".equals(jsClassName))
				jsClassName= "java_lang_Short";

			jsClassName= jsClassName.replace("_ARRAYTYPE", ""); //TODO revisar parche temporal para arreglos!!
			ScriptHelper.put("jsClassName", jsClassName, null);

			Object nativeClass= null;

			ScriptHelper.eval("try{var result= eval(jsClassName)}catch(e){}", null);
			nativeClass= ScriptHelper.eval("result", null);

			if (nativeClass == null)
			{
				throw new ClassNotFoundException(jsClassName);
			}

			clazz= new Class(nativeClass);
			ScriptHelper.put("clazz", clazz, null);
			ScriptHelper.put("className", className, null);
			ScriptHelper.eval("clazz.realName=className", null);
			classesByName.put(className, clazz);
			//			classesByName.put(jsClassName, clazz);
		}

		return clazz;
	}

	/**
	 * Returns the Class representing the component type of an array.
	 * If this class does not represent an array class this method returns null.
	 */
	public Class<?> getComponentType()
	{
		throw new UnsupportedOperationException();
	}

	public T newInstance() throws InstantiationException, IllegalAccessException
	{
		ScriptHelper.eval("var o = new this.$$$nativeClass", this);
		ScriptHelper.eval("o.$$init_$void()", this);
		return (T) ScriptHelper.eval("o", this);
	}

	/**
	 * Determines if the specified Object is assignment-compatible with the object represented by this Class.
	 */
	public boolean isInstance(Object obj)
	{
		Class cls= obj.getClass();
		do
		{
			if (cls.getName().equals(getName()))
			{
				return true;
			}
			if (cls.getName().equals("java.lang.Object"))
			{
				return false;
			}
			cls= cls.getSuperclass();
		}
		while (true);
	}

	/**
	 * Determines if the class or interface represented by this Class object is either the same as, or is a
	 * superclass or superinterface of, the class or interface represented by the specified Class parameter.
	 */
	public boolean isAssignableFrom(Class<?> otherClass)
	{
		Class<?>[] interfaces= getInterfaces();

		if (otherClass == null)
			throw new NullPointerException();

		if (otherClass.isInterface() && Object.class.equals(this))
			return true;

		ScriptHelper.put("otherClass", otherClass, this);
		return ScriptHelper.evalBoolean("dragomeJs.isInstanceof(otherClass.$$$nativeClass, this.$$$nativeClass)", this);
	}

	public boolean isInterface()
	{
		return ScriptHelper.evalBoolean("this.$$$nativeClass.$$type == \"Interface\"", this);
	}

	public boolean isArray()
	{
		return false;
	}

	/**
	 * Returns the class loader for the class.
	 */
	public ClassLoader getClassLoader()
	{
		return null;
	}

	public String getName()
	{
		String result;

		if (isInterface())
			result= (String) ScriptHelper.eval("this.$$$nativeClass.name", this);
		else
			result= (String) ScriptHelper.eval("this.$$$nativeClass.classname", this);

		return result != null ? result.replace("_", ".") : "java.lang.Object"; //TODO arreglar, no se pueden usar nombre de clases con _!!
	}

	public Class<? super T> getSuperclass()
	{
		if (ScriptHelper.evalBoolean("this.$$$nativeClass.classname == 'java_lang_Object' ", this))
			return null;
		else
		{
			Boolean eval= ScriptHelper.evalBoolean("this.$$$nativeClass.superclass != undefined", this);
			return eval ? new Class(ScriptHelper.eval("this.$$$nativeClass.superclass", this)) : null;
		}
	}

	public Method[] getDeclaredMethods()
	{
		if (declaredMethods == null)
		{
			declaredMethods= new ArrayList<Method>();
			String[] signatures= new String[0];
			ScriptHelper.put("signatures", signatures, this);
			ScriptHelper.eval("for (var e in this.$$$nativeClass.$$members) { if (typeof this.$$$nativeClass.$$members[e]  === 'function' && e.startsWith('$')) signatures.push(e); }", this);
			ScriptHelper.eval("for (var e in this.$$$nativeClass.prototype) { if (typeof this.$$$nativeClass.prototype[e]  === 'function' && e.startsWith('$')) signatures.push(e); }", this);
			addMethods(signatures, Modifier.PUBLIC);
			signatures= new String[0];
			ScriptHelper.eval("for (var e in this.$$$nativeClass) { if (typeof this.$$$nativeClass[e]  === 'function' && e.startsWith('$')) signatures.push(e); }", this);
			addMethods(signatures, Modifier.PUBLIC | Modifier.STATIC);
		}
		return declaredMethods.toArray(new Method[0]);
	}

	private void addMethods(String[] signatures, int modifier)
	{
		for (int i= 0, j= 0; i < signatures.length; i++)
		{
			String signature= signatures[i];
			if (signature.startsWith("$$init_") || signature.startsWith("$$clinit_") /*|| signature.indexOf("_") == -1*/)
				continue;
			declaredMethods.add(new Method(this, signatures[i], modifier));
			j++;
		}
	}

	public Method[] getDeclaredMethodsInDepth()
	{
		if (declaredMethodsInDepth == null)
		{
			declaredMethodsInDepth= getDeclaredMethods();

			Class<? super T> superclass= getSuperclass();
			int j= declaredMethodsInDepth.length;
			if (superclass != null)
				for (Method method : superclass.getDeclaredMethodsInDepth())
				{
					declaredMethodsInDepth[j++]= method;
				}
		}

		return declaredMethodsInDepth;
	}

	public Method[] getMethods()
	{
		if (methods == null)
		{
			methods= new Method[0];
			int j= 0;

			for (Class<?> interfaze : getInterfaces())
				for (Method method : interfaze.getMethods())
					methods[j++]= method;

			Class<? super T> superclass= getSuperclass();
			if (superclass != null)
				for (Method method : superclass.getMethods())
					methods[j++]= method;

			for (Method method : getDeclaredMethods())
				methods[j++]= method;
		}

		return methods;
	}

	public Class<?>[] getInterfaces()
	{
		if (interfacesCache == null)
		{
			List<Class> interfacesCollection= new ArrayList<Class>();
			ScriptHelper.put("interfacesCollection", interfacesCollection, this);
			if (isInterface())
				ScriptHelper.eval("var interfacesList= this.$$$nativeClass.$$extends", this);
			else
				ScriptHelper.eval("var interfacesList= this.$$$nativeClass.$$implements", this);

			ScriptHelper.eval("for (var e in interfacesList) {", this);

			if (ScriptHelper.evalBoolean("interfacesList[e].$$type != 'Class'", this))
			{
				String className= (java.lang.String) ScriptHelper.eval("interfacesList[e].$$$nativeClass ? interfacesList[e].$$$nativeClass.name : interfacesList[e].name", this);
				try
				{
					interfacesCollection.add(Class.forName(className));
				}
				catch (java.lang.ClassNotFoundException e)
				{
					e.printStackTrace();
				}
			}
			ScriptHelper.eval("}", this);
			interfacesCache= new Class<?>[interfacesCollection.size()];
			int i= 0;
			for (Class class1 : interfacesCollection)
			{
				interfacesCache[i++]= class1;
			}
		}
		return interfacesCache;
	}

	public Annotation[] getDeclaredAnnotations()
	{
		Annotation[] annotations= new Annotation[0];
		Object[] maps= (Object[]) ScriptHelper.eval("this.$$$nativeClass.annotations", this);
		if (maps == null)
			return annotations;

		int i= 0;
		for (Object map : maps)
		{
			InvocationHandler handler= new AnnotationInvocationHandler2(new JSObject(map));
			Annotation annotation= (Annotation) Proxy.newProxyInstance(null, new Class[] { null }, handler);
			annotations[i++]= annotation;
		}
		return annotations;
	}

	public boolean desiredAssertionStatus()
	{
		return false;
	}

	public Method getMethod(String name, Class<?>... parameterTypes) throws NoSuchMethodException, SecurityException
	{
		String argumentTypesToString= argumentTypesToString(parameterTypes);
		String key= name + argumentTypesToString;

		Method foundMethod= foundMethods.get(key);
		if (foundMethod == null)
		{
			Method[] declaredMethods= getMethods();

			for (Method method : declaredMethods)
			{
				if (method.getName().equals(name) && //
						arrayContentsEq(parameterTypes, method.getParameterTypes()) && // 
						(foundMethod == null || foundMethod.getReturnType().isAssignableFrom(method.getReturnType())))
					foundMethods.put(key, foundMethod= method);
			}
		}

		if (foundMethod == null)
			throw new NoSuchMethodException(getName() + "." + key);

		return foundMethod;
	}

	private static String argumentTypesToString(Class<?>[] argTypes)
	{
		StringBuilder buf= new StringBuilder();
		buf.append("(");
		if (argTypes != null)
		{
			for (int i= 0; i < argTypes.length; i++)
			{
				if (i > 0)
				{
					buf.append(", ");
				}
				Class<?> c= argTypes[i];
				buf.append((c == null) ? "null" : c.getName());
			}
		}
		buf.append(")");
		return buf.toString();
	}

	public Constructor<T> getDeclaredConstructor(Class<?>... parameterTypes) throws NoSuchMethodException, SecurityException
	{
		return new Constructor(this, new Class<?>[0], new Class<?>[0], 0);
	}

	public <A extends Annotation> A getAnnotation(Class<A> annotationClass)
	{
		return getAnnotationInternal(this, annotationClass, null, null);
	}

	public static <A extends Annotation> A getAnnotationInternal(Class<?> aClass, Class<A> annotationClass, String methodName, Integer parameterIndex)
	{
		if (annotationClass.getName().startsWith("org.junit"))
			return null;

		A annotation= (A) Proxy.newProxyInstance(null, new Class[] { annotationClass }, new AnnotationInvocationHandler(aClass, annotationClass, methodName, parameterIndex));
		return annotation;
	}

	public Annotation[] getAnnotations()
	{
		return null;
	}

	public boolean isAnnotationPresent(Class<? extends Annotation> annotationType)
	{
		return false;
	}

	public TypeVariable<?>[] getTypeParameters()
	{
		return null;
	}

	public Constructor<T> getConstructor(Class<?>... parameterTypes) throws NoSuchMethodException, SecurityException
	{
		if (parameterTypes == null || parameterTypes.length > 0)
			throw new NoSuchMethodException();
		else
			return new Constructor<T>(this, parameterTypes, null, Modifier.PUBLIC);
	}

	public Field[] getFields() throws SecurityException
	{
		return new Field[0];
	}

	public Field[] getDeclaredFields() throws SecurityException
	{
		return new Field[0];
	}

	public Field getDeclaredField(String name) throws NoSuchFieldException, SecurityException
	{
		return null;
	}

	public Constructor<?>[] getDeclaredConstructors() throws SecurityException
	{
		return null;
	}

	public String getResourceAsStream(String resourcePath)
	{
		//TODO implementar
		return null;
	}

	public boolean isPrimitive()
	{
		String realName= (java.lang.String) ScriptHelper.eval("this.realName", null);
		return !realName.contains("_") && !realName.contains(".");
	}

	@Override
	public int hashCode()
	{
		final int prime= 31;
		int result= 1;
		result= prime * result + ((getName() == null) ? 0 : getName().hashCode());
		return result;
	}

	@Override
	public boolean equals(java.lang.Object obj)
	{
		ScriptHelper.put("obj", obj, this);
		boolean result= ScriptHelper.evalBoolean("obj.$$$nativeClass == this.$$$nativeClass", this);
		return result;
	}

	public Package getPackage()
	{
		String name= getName();
		String packageName= name.substring(0, name.indexOf(getSimpleName()) - 1);
		return new Package(packageName);
	}

	public <U> Class<? extends U> asSubclass(Class<U> clazz)
	{
		if (clazz.isAssignableFrom(this))
			return (Class<? extends U>) this;
		else
			throw new ClassCastException(this.toString());
	}

	public int getModifiers()
	{
		return 0x1;
	}

	public String getCanonicalName()
	{
		return getName();
	}

	public Method getDeclaredMethod(String name, Class<?>... parameterTypes) throws NoSuchMethodException, SecurityException
	{
		return getMethod(name, parameterTypes);
	}

	public static <T> java.lang.Class<T> getType(java.lang.String typeName)
	{
		try
		{
			return (java.lang.Class<T>) Class.forName(typeName);
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	public ProtectionDomain getProtectionDomain()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public static Class<?> forName(String name, boolean initialize, ClassLoader loader) throws ClassNotFoundException
	{
		return forName(name);
	}

	private static boolean arrayContentsEq(Object[] a1, Object[] a2)
	{
		if (a1 == null)
		{
			return a2 == null || a2.length == 0;
		}

		if (a2 == null)
		{
			return a1.length == 0;
		}

		if (a1.length != a2.length)
		{
			return false;
		}

		for (int i= 0; i < a1.length; i++)
		{
			if (!a1[i].equals(a2[i]))
			{
				return false;
			}
		}

		return true;
	}
}
