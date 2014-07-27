package com.dragome.compiler.ast;

import com.dragome.compiler.generators.AbstractVisitor;

public class FieldWrite extends FieldAccess implements Assignable
{

	public FieldWrite()
	{
	}

	public void visit(AbstractVisitor visitor)
	{
		visitor.visit(this);
	}

}
