package com.dragome.compiler.ast;

import com.dragome.compiler.generators.AbstractVisitor;

public class ThrowStatement extends Block
{

	public void visit(AbstractVisitor visitor)
	{
		visitor.visit(this);
	}

	public Expression getExpression()
	{
		return (Expression) getFirstChild();
	}

	public void setExpression(Expression expression)
	{
		widen(expression);
		appendChild(expression);
	}
}
