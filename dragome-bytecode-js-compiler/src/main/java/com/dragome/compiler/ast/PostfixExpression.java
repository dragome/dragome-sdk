package com.dragome.compiler.ast;

import com.dragome.compiler.generators.AbstractVisitor;

public class PostfixExpression extends PStarExpression
{

	public PostfixExpression()
	{
		super();
	}

	public void visit(AbstractVisitor visitor)
	{
		visitor.visit(this);
	}
}
