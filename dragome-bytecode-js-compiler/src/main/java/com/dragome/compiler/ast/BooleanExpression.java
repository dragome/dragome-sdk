package com.dragome.compiler.ast;

public class BooleanExpression implements Cloneable
{
	private Expression expression;

	public BooleanExpression(Expression newExpression)
	{
		expression= newExpression;
	}

	public Expression getExpression()
	{
		return expression;
	}

}
