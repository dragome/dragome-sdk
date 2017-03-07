package com.dragome.compiler;

import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.InvocationHandler;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.bcel.classfile.AnnotationDefault;

import com.dragome.compiler.type.Signature;
import com.dragome.compiler.units.ClassUnit;
import com.dragome.compiler.units.MemberUnit;

public class NullClassUnit extends ClassUnit
{
	private String className;

	public NullClassUnit(String className)
	{
		this.setClassName(className);
	}

	public Collection<ClassUnit> getInterfaces()
	{
		return new ArrayList<>();
	}

	public Collection<ClassUnit> getSupertypes()
	{
		return new ArrayList<>();
	}

	public ClassUnit getSuperUnit()
	{
		return null;
	}

	public void write(int depth, Writer writer2, String... annotationDefaultFound) throws IOException
	{
	}

	protected boolean isImplementing(Class<InvocationHandler> class1)
	{
		return false;
	}

	public String toString()
	{
		return "";
	}

	public Signature getSignature()
	{
		return Project.getSingleton().getClassUnit(Object.class.getName()).getSignature();
	}

	public String getName()
	{
		return getClass().getSimpleName();
	}

	public Collection<MemberUnit> getDeclaredMembers()
	{
		return new ArrayList<>();
	}

	public void setTainted()
	{
	}

	public boolean isTainted()
	{
		return true;
	}

	public boolean isResolved()
	{
		return true;
	}

	public void addSubUnit(ClassUnit subUnit)
	{
	}

	public void addDependency(String dependency)
	{
	}

	public void addMemberUnit(MemberUnit unit)
	{
	}

	public void addInterface(ClassUnit interfaceUnit)
	{
	}

	public void addNotReversibleMethod(String methodNameSignature)
	{
	}

	public String getClassName()
	{
		return className;
	}

	public void setClassName(String className)
	{
		this.className= className;
	}
}
