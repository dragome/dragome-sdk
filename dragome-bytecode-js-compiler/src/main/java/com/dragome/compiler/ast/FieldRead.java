package com.dragome.compiler.ast;

import com.dragome.compiler.generators.AbstractVisitor;

public class FieldRead extends FieldAccess
{

	public FieldRead()
	{
	}

	public void visit(AbstractVisitor visitor)
	{
		visitor.visit(this);
	}

}
