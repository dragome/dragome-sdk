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
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.net.URL;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.dragome.commons.compiler.annotations.AnnotationsHelper;
import com.dragome.commons.compiler.annotations.AnnotationsHelper.AnnotationContainer.AnnotationEntry;
import com.dragome.commons.compiler.annotations.AnnotationsHelper.AnnotationEntryWithEntityType;
import com.dragome.commons.compiler.annotations.CompilerType;
import com.dragome.commons.compiler.annotations.DragomeCompilerSettings;
import com.dragome.commons.compiler.annotations.MethodAlias;
import com.dragome.commons.javascript.JSObject;
import com.dragome.commons.javascript.ScriptHelper;
import com.dragome.helpers.AnnotationsAdder;

@DragomeCompilerSettings(CompilerType.Standard)
public final class Class<T> implements java.io.Serializable, java.lang.reflect.GenericDeclaration, java.lang.reflect.Type, java.lang.reflect.AnnotatedElement
{
	private static final int ANNOTATION= 0x00002000;
	private static final int ENUM= 0x00004000;
	private static final int SYNTHETIC= 0x00001000;

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
	private JSObject<Method> foundMethods= new JSObject<Method>();
	private JSObject<Field> foundFields= new JSObject<Field>();

	protected Object nativeClass;
	protected Method[] methods;
	protected List<Method> declaredMethods;
	protected List<Field> declaredFields;
	protected Method[] declaredMethodsInDepth;
	protected Class<?>[] interfacesCache;
	protected boolean isArray;
	protected String type;

	private Class(Object theNativeClass)
	{
		nativeClass= theNativeClass;
		ScriptHelper.put("theNativeClass", theNativeClass, null);
		ScriptHelper.put("self", this, null);
		ScriptHelper.eval("theNativeClass.javaClass=self", null);
	}

	public String toString()
	{
		return "class " + getName();
	}

	public static Class<?> forName(String className) throws ClassNotFoundException
	{
		className= className.replace("_", ".");

		Class<?> clazz= classesByName.get(className);
		if (clazz == null)
		{
			if (className.startsWith("["))
			{ // temp fix  for [].getClass();

				String jsClassName= "java_lang_reflect_Array";

				ScriptHelper.put("jsClassName", jsClassName, null);
				Object nativeClass= null;
				ScriptHelper.eval("try{var result= eval(jsClassName)}catch(e){}", null);
				nativeClass= ScriptHelper.eval("result", null);

				if (nativeClass == null)
					throw new ClassNotFoundException(jsClassName);

				String type= className.replaceAll("\\[", "");
				type= type.replaceAll(";", "");
				if (type.startsWith("L"))
					type= type.replaceFirst("L", "");

				if (type.startsWith("Z"))
					type= "boolean";
				else if (type.startsWith("B"))
					type= "byte";
				else if (type.startsWith("C"))
					type= "char";
				else if (type.startsWith("D"))
					type= "double";
				else if (type.startsWith("F"))
					type= "float";
				else if (type.startsWith("I"))
					type= "int";
				else if (type.startsWith("J"))
					type= "long";
				else if (type.startsWith("S"))
					type= "short";

				clazz= new Class(nativeClass);
				clazz.isArray= true;
				clazz.type= type;
				ScriptHelper.put("clazz", clazz, null);
				ScriptHelper.put("className", className, null);
				ScriptHelper.eval("clazz.realName=className", null);
				classesByName.put(className, clazz);
				return clazz;
			}

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
		if (isArray() == false)
			return null;
		else
			try
			{
				return Class.forName(type);
			}
			catch (java.lang.ClassNotFoundException e)
			{
				e.printStackTrace();
			}
		return null;
	}

	public T newInstance() throws InstantiationException, IllegalAccessException
	{
		if (this.equals(String.class))
			return (T) "";
		else
		{
			ScriptHelper.eval("var o= null", this);
			ScriptHelper.eval("try{var o = new this.$$$nativeClass___java_lang_Object; o.$$init_$void()}catch(e){} ", this);
			T result= (T) ScriptHelper.eval("o", this);
			if (result == null)
				throw new NullPointerException();

			return result;
		}
	}

	/**
	 * Determines if the specified Object is assignment-compatible with the object represented by this Class.
	 * https://docs.oracle.com/javase/8/docs/api/java/lang/Class.html#isInstance-java.lang.Object-
	 */
	public boolean isInstance(final Object obj)
	{
		if (obj != null)
			return isAssignableFrom(obj.getClass());
		else
			return false;
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
		return ScriptHelper.evalBoolean("dragomeJs.isInstanceof(otherClass.$$$nativeClass___java_lang_Object, this.$$$nativeClass___java_lang_Object)", this);
	}

	public boolean isInterface()
	{
		return ScriptHelper.evalBoolean("this.$$$nativeClass___java_lang_Object.$$type == \"Interface\"", this);
	}

	public boolean isArray()
	{
		return isArray;
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

		if (!isArray && !isPrimitive())
		{
			if (isInterface())
				result= (String) ScriptHelper.eval("this.$$$nativeClass___java_lang_Object.name", this);
			else
				result= (String) ScriptHelper.eval("this.$$$nativeClass___java_lang_Object.classname", this);
		}
		else
			result= (java.lang.String) ScriptHelper.eval("this.realName", null);

		return result != null ? result.replace("_", ".") : "java.lang.Object"; //TODO arreglar, no se pueden usar nombre de clases con _!!
	}

	public Class<? super T> getSuperclass()
	{
		try
		{
			Logger.getLogger(AnnotationsAdder.class.getName()).log(Level.FINEST, "Cannot add type annotation");

			if (ScriptHelper.evalBoolean("this.$$$nativeClass___java_lang_Object.classname == 'java_lang_Object' ", this))
				return null;
			else
			{
				Boolean eval= ScriptHelper.evalBoolean("this.$$$nativeClass___java_lang_Object.superclass != undefined", this);
				if (eval)
				{
					boolean javaClassExists= ScriptHelper.evalBoolean("this.$$$nativeClass___java_lang_Object.superclass.javaClass != undefined", this);

					if (!javaClassExists)
					{
						String className= (String) ScriptHelper.eval("this.$$$nativeClass___java_lang_Object.superclass.classname", this);
						return (Class<? super T>) forName(className.replace("_", "."));
					}
					else
						return (Class<? super T>) ScriptHelper.eval("this.$$$nativeClass___java_lang_Object.superclass.javaClass", this);
				}
				else
				{
					return null;
				}
			}
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	public Method[] getDeclaredMethods()
	{
		return getDeclaredMessages(true);
	}

	private Method[] getDeclaredMessages(boolean filterConstructors)
	{
		if (declaredMethods == null)
		{
			declaredMethods= new ArrayList<Method>();
			findMethods(declaredMethods, filterConstructors);
		}
		return declaredMethods.toArray(new Method[0]);
	}

	private void findMethods(List<Method> declaredMethods2, boolean filterConstructors)
	{
		String[] signatures= new String[0];
		ScriptHelper.put("signatures", signatures, this);
		ScriptHelper.eval("for (var e in this.$$$nativeClass___java_lang_Object.$$members) { if (typeof this.$$$nativeClass___java_lang_Object.$$members[e]  === 'function' && e.startsWith('$')) signatures.push(e); }", this);
		ScriptHelper.eval("for (var e in this.$$$nativeClass___java_lang_Object.prototype) { if (typeof this.$$$nativeClass___java_lang_Object.prototype[e]  === 'function' && e.startsWith('$')) signatures.push(e); }", this);
		signatures= (String[]) ScriptHelper.eval("signatures", this);
		addMethods(declaredMethods2, signatures, Modifier.PUBLIC, filterConstructors);
		signatures= new String[0];
		ScriptHelper.put("signatures", signatures, this);
		ScriptHelper.eval("for (var e in this.$$$nativeClass___java_lang_Object) { if (typeof this.$$$nativeClass___java_lang_Object[e]  === 'function' && e.startsWith('$')) signatures.push(e); }", this);
		signatures= (String[]) ScriptHelper.eval("signatures", this);
		addMethods(declaredMethods2, signatures, Modifier.PUBLIC | Modifier.STATIC, filterConstructors);
	}

	public Constructor<?>[] getDeclaredConstructors() throws SecurityException
	{
		ArrayList<Method> constructors= new ArrayList<>();
		findMethods(constructors, false);
		List<Constructor> collect= constructors.stream().map(m -> new Constructor(m)).collect(Collectors.toList());
		return collect.toArray(new Constructor[0]);
	}

	@MethodAlias(local_alias= "getMethodBySignature")
	public Method getMethodBySignature(String signature)
	{
		Method[] foundMethods= getMethods();
		for (Method method : foundMethods)
		{
			if (method.getName().equals(signature))
				return method;
		}

		return new Method(this, signature, Modifier.PUBLIC);
	}

	private void addMethods(List<Method> declaredMethods2, String[] signatures, int modifier, boolean filterConstructors)
	{
		for (int i= 0; i < signatures.length; i++)
		{
			String signature= signatures[i];

			if ((!isConstructor(signature) && filterConstructors) || (isConstructor(signature) && !filterConstructors))
			{
				declaredMethods2.add(new Method(this, signatures[i], modifier));
			}
		}
	}

	public boolean checkIsDefault(String signature)
	{
		if (!isInterface())
			return false;

		ScriptHelper.put("result", "\n{\n}", this);
		ScriptHelper.put("signature", signature, this);
		ScriptHelper.eval("try{result= this.$$$nativeClass___java_lang_Object.$$members[signature].toString()}catch(e){}", this);
		ScriptHelper.eval("try{result= this.$$$nativeClass___java_lang_Object.prototype[signature].toString()}catch(e){}", this);
		Object result2= ScriptHelper.eval("result", this);
		boolean isDefault= !result2.toString().endsWith("\n{\n}");
		return isDefault;
	}

	private boolean isConstructor(String signature)
	{
		return signature.startsWith("$$init_") || signature.startsWith("$$clinit_") /*|| signature.indexOf("_") == -1*/;
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
			List<Method> internalGetMethods= internalGetMethods(true);
			methods= internalGetMethods.toArray(new Method[0]);
		}
		return methods;
	}

	public List<Method> internalGetMethods(boolean filterDuplicated)
	{
		List<Method> result= new ArrayList<>();

		for (Method method : getDeclaredMethods())
			addMethod(result, method, filterDuplicated);

		for (Class<?> interfaze : getInterfaces())
			for (Method method : interfaze.getMethods())
				addMethod(result, method, filterDuplicated);

		Class<? super T> superclass= getSuperclass();
		if (superclass != null)
			for (Method method : superclass.getMethods())
				addMethod(result, method, filterDuplicated);

		return result;
	}

	private void addMethod(List<Method> result, Method method, boolean filterDuplicated)
	{
		boolean found= false;

		if (filterDuplicated)
		{
			for (Method method2 : result)
			{
				List<Class<?>> asList= Arrays.asList(method.getParameterTypes());
				List<Class<?>> asList2= Arrays.asList(method2.getParameterTypes());
				if (method.getName().equals(method2.getName()) && asList.equals(asList2))
					found= true;
			}
		}

		if (!found)
			result.add(method);
	}

	public Class<?>[] getInterfaces()
	{
		if (interfacesCache == null)
		{
			List<Class> interfacesCollection= new ArrayList<Class>();
			ScriptHelper.put("interfacesCollection", interfacesCollection, this);
			if (isInterface())
				ScriptHelper.eval("var interfacesList= this.$$$nativeClass___java_lang_Object.$$extends", this);
			else
				ScriptHelper.eval("var interfacesList= this.$$$nativeClass___java_lang_Object.$$implements", this);

			ScriptHelper.eval("for (var e=0; interfacesList && e < interfacesList.length; e++) {", this);

			if (ScriptHelper.evalBoolean("interfacesList[e].$$type != 'Class'", this))
			{
				String className= (java.lang.String) ScriptHelper.eval("interfacesList[e].$$$nativeClass___java_lang_Object ? interfacesList[e].$$$nativeClass___java_lang_Object.name : interfacesList[e].name", this);
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
		Object[] maps= (Object[]) ScriptHelper.eval("this.$$$nativeClass___java_lang_Object.annotations", this);
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
		return getAnnotationInternal(this, annotationClass, null, null, null);
	}

	public static <A extends Annotation> A getAnnotationInternal(Class<?> aClass, Class<A> annotationClass, String methodName, Integer parameterIndex, String fieldName)
	{
		List<AnnotationEntry> annotationEntries= new ArrayList<>(AnnotationsHelper.getAnnotationsByType(annotationClass).getEntries());
		boolean annotationFound= false;

		for (AnnotationEntry annotationEntry : annotationEntries)
			if (annotationEntry.getType().equals(aClass))
			{
				String annotationKey= AnnotationInvocationHandler.getAnnotationKey(fieldName, parameterIndex, methodName, "");
				if (annotationEntry.getAnnotationKey().startsWith(annotationKey) || (annotationKey == null && !annotationEntry.getAnnotationKey().contains("/")))
					annotationFound= true;
			}

		if (!annotationFound)
			return null;

		A annotation= (A) Proxy.newProxyInstance(null, new Class[] { annotationClass }, new AnnotationInvocationHandler(aClass, annotationClass, methodName, parameterIndex, fieldName));
		return annotation;
	}

	public static List<Annotation> getAnnotationsInternal(Class<?> aClass, String methodName, Integer parameterIndex, String fieldName)
	{
		final List<AnnotationEntryWithEntityType> annotationEntrysWithEntityType= AnnotationsHelper.getAnnotationsByClass(aClass);
		final List<Annotation> ret= new ArrayList<>();

		final String annotationKey= AnnotationInvocationHandler.getAnnotationKey(fieldName, parameterIndex, methodName, "");
		for (AnnotationEntryWithEntityType annotationEntryWithEntityType : annotationEntrysWithEntityType)
		{
			final AnnotationEntry annotationEntry= annotationEntryWithEntityType.getAnnotationEntry();
			if (annotationEntry.getAnnotationKey().startsWith(annotationKey) || (annotationKey == null && !annotationEntry.getAnnotationKey().contains("/")))
			{
				final Class<? extends Annotation> annotationClass= annotationEntryWithEntityType.getAnnotationType();
				final Annotation annotation= (Annotation) Proxy.newProxyInstance(null, new Class[] { annotationClass }, new AnnotationInvocationHandler(aClass, annotationClass, methodName, parameterIndex, fieldName));
				ret.add(annotation);
			}
		}

		return ret;
	}

	public Annotation[] getAnnotations()
	{
		return new Annotation[0];
	}

	public boolean isAnnotationPresent(Class<? extends Annotation> annotationClass)
	{
		return getAnnotation(annotationClass) != null;
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
		if (declaredFields == null)
		{
			declaredFields= new ArrayList<Field>();
			Object[] signatures= new Object[0];
			ScriptHelper.put("signatures", signatures, this);
			ScriptHelper.eval("for (var e in this.$$$nativeClass___java_lang_Object.$$members) { if (e.startsWith('$$$') && e != '$$$$signatures'){ var b={}; b.e = e; signatures.push(b); }}", this);
			ScriptHelper.eval("for (var e in this.$$$nativeClass___java_lang_Object.prototype) { if (e.startsWith('$$$') && e != '$$$$signatures'){ var b={}; b.e = e; signatures.push(b); }}", this);
			addFields(signatures, Modifier.PUBLIC);

			signatures= new Object[0];
			ScriptHelper.put("signatures", signatures, this);
			ScriptHelper.eval("for (var e in this.$$$nativeClass___java_lang_Object) { if (e.startsWith('$$$') && e != '$$$$signatures'){ var b={}; b.e = e; signatures.push(b); }}", this);
			addFields(signatures, Modifier.PUBLIC | Modifier.STATIC);

			//			signatures= new String[0];
			//			ScriptHelper.eval("for (var e in this.$$$nativeClass___java_lang_Object) { if (e.startsWith('$$$')){ var b={}; b.e = e; signatures.push(b); }}", this);
			//			addFields(signatures, Modifier.PUBLIC | Modifier.STATIC);
		}
		return declaredFields.toArray(new Field[0]);
	}

	private void addFields(Object[] signatures, int modifier)
	{
		for (int i= 0; i < signatures.length; i++)
		{
			ScriptHelper.put("sig", signatures[i], this);
			String signature= ScriptHelper.evalCasting("sig.e", String.class, this);

			declaredFields.add(new Field(this, signature, modifier));
		}
	}

	public Field getDeclaredField(String name) throws NoSuchFieldException, SecurityException
	{
		return getField(name);
	}

	public Field getField(String name) throws NoSuchFieldException, SecurityException
	{
		Field foundField= foundFields.get(name);
		if (foundField == null)
		{
			Field[] declaredFields= getDeclaredFields();
			for (Field method : declaredFields)
				if (method.getName().equals(name))
					foundFields.put(name, foundField= method);
		}

		if (foundField == null)
			throw new NoSuchFieldException(getName() + "." + name);

		return foundField;
	}

	public String getResourceAsStream(String resourcePath)
	{
		//TODO implementar
		return null;
	}

	public URL getResource(String name)
	{
		return null;
	}

	public boolean isPrimitive()
	{
		String realName= (java.lang.String) ScriptHelper.eval("this.realName", null); //TODO fix it: classes in default package!
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
		if (obj == null)
			return false;
		ScriptHelper.put("obj", obj, this);
		boolean result= ScriptHelper.evalBoolean("obj.$$$nativeClass___java_lang_Object == this.$$$nativeClass___java_lang_Object", this);
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

	public Type getGenericSuperclass()
	{
		return getSuperclass();
	}

	public boolean isEnum()
	{
		return (this.getModifiers() & ENUM) != 0 && this.getSuperclass() == java.lang.Enum.class;
	}
	public Class<?> getEnclosingClass() throws SecurityException
	{
		return null;
	}

	public String getSignatureFor(String signature)
	{
		String genericSignature= null;
		if (ScriptHelper.evalBoolean("this.$$$nativeClass___java_lang_Object.$$$$signatures ", this))
		{
			ScriptHelper.put("signature", signature, this);
			genericSignature= (String) ScriptHelper.eval("this.$$$nativeClass___java_lang_Object.$$$$signatures[signature]", this);
		}

		if (genericSignature == null)
		{
			if (getSuperclass() != null)
				return getSuperclass().getSignatureFor(signature);
			else
				return null;
		}

		return genericSignature;
	}
}
