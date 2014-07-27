package com.dragome.compiler.type;

import java.util.Collection;
import java.util.LinkedHashSet;

import com.dragome.compiler.units.ClassUnit;

public class TypeCollector implements TypeVisitor
{

	public Collection<ClassUnit> collectedTypes= new LinkedHashSet<ClassUnit>();

	public void visit(ClassUnit clazz)
	{
		collectedTypes.add(clazz);
	}
}
