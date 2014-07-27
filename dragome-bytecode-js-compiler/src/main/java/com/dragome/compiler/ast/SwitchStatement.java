package com.dragome.compiler.ast;

import com.dragome.compiler.generators.AbstractVisitor;

public class SwitchStatement extends Block
{

	private Expression expression;

	public SwitchStatement()
	{
		super();
	}

	public void visit(AbstractVisitor visitor)
	{
		visitor.visit(this);
	}

	public SwitchCase getDefault()
	{
		return (SwitchCase) getLastChild();
	}

	public Expression getExpression()
	{
		return expression;
	}

	public void setExpression(Expression theExpression)
	{
		widen(theExpression);
		expression= theExpression;
	}
}
