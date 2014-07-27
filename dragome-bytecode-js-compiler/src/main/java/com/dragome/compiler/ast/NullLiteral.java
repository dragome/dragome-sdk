package com.dragome.compiler.ast;

import com.dragome.compiler.generators.AbstractVisitor;

public class NullLiteral extends Expression
{

	public void visit(AbstractVisitor visitor)
	{
		visitor.visit(this);
	}
}
