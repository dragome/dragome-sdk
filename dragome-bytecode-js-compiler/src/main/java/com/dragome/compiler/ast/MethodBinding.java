/*
 * Created on 29.01.2006
 * Copyright Wolfgang Kuehn 2005
 */
package com.dragome.compiler.ast;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.bcel.Constants;
import org.apache.bcel.classfile.ConstantCP;
import org.apache.bcel.classfile.ConstantNameAndType;
import org.apache.bcel.classfile.ConstantPool;
import org.apache.bcel.generic.ObjectType;
import org.apache.bcel.generic.Type;

import com.dragome.compiler.utils.Utils;

public class MethodBinding
{

	private static Map<String, MethodBinding> methodBindingsByKey= new LinkedHashMap<String, MethodBinding>();

	public static MethodBinding lookup(int index, ConstantPool constantPool)
	{
		ConstantCP methodRef= (ConstantCP) constantPool.getConstant(index);
		ConstantNameAndType nameAndType= (ConstantNameAndType) constantPool.getConstant(methodRef.getNameAndTypeIndex(), Constants.CONSTANT_NameAndType);

		String name= nameAndType.getName(constantPool);
		String signature= nameAndType.getSignature(constantPool);

		return lookup(methodRef.getClass(constantPool), name, signature);
	}

	public static MethodBinding lookup(String className, String name, String signature)
	{
		String key= className + "#" + name + signature;

		MethodBinding binding= methodBindingsByKey.get(key);
		if (binding != null)
			return binding;

		binding= new MethodBinding();
		binding.declaringClass= new ObjectType(className);
		binding.name= name;
		binding.parameterTypes= Type.getArgumentTypes(signature);
		binding.returnType= Type.getReturnType(signature);
		binding.signature= signature;

		methodBindingsByKey.put(key, binding);

		return binding;
	}

	private ObjectType declaringClass;

	private String name;

	private Type[] parameterTypes;

	private Type returnType;

	private String signature;

	private MethodBinding()
	{
	}

	public ObjectType getDeclaringClass()
	{
		return declaringClass;
	}

	public String getName()
	{
		return name;
	}

	public Type[] getParameterTypes()
	{
		return parameterTypes;
	}

	public Type getReturnType()
	{
		return returnType;
	}

	public boolean isConstructor()
	{
		return "<init>".equals(name);
	}

	public String getSignature()
	{
		return signature;
	}

	public String toString()
	{
		return getDeclaringClass().getClassName() + "#" + getRelativeSignature();
	}

	public String getRelativeSignature()
	{
		String signature= getName() + "(";
		String sep= "";
		for (int i= 0; i < getParameterTypes().length; i++)
		{
			Type type= getParameterTypes()[i];
			signature+= sep + Utils.getSignature(type);
			sep= ",";
		}
		signature+= ")";
		signature+= Utils.getSignature(returnType);
		return signature;
	}

}
