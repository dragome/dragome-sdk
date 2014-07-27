package com.dragome.compiler.type;

import com.dragome.compiler.units.ClassUnit;

public interface TypeVisitor
{
	public void visit(ClassUnit clazz);
}
