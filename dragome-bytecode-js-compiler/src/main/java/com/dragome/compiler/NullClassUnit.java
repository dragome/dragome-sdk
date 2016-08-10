package com.dragome.compiler;

import com.dragome.compiler.units.ClassUnit;
import com.dragome.compiler.units.MemberUnit;

public class NullClassUnit extends ClassUnit
{
	public NullClassUnit()
	{
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
}
