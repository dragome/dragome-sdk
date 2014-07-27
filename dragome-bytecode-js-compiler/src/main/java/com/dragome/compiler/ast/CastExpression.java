package com.dragome.compiler.ast;

import com.dragome.compiler.generators.AbstractVisitor;

public class CastExpression extends Expression
{

	private Expression expression;

	public void visit(AbstractVisitor visitor)
	{
		visitor.visit(this);
	}

	public void setExpression(Expression theExpression)
	{
		widen(theExpression);
		expression= theExpression;
	}

	public Expression getExpression()
	{
		return expression;
	}

}
