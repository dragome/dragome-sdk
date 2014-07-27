package com.dragome.compiler.ast;

import com.dragome.compiler.generators.AbstractVisitor;

public class ReturnStatement extends ASTNode
{

	private Expression expression;

	public ReturnStatement(int theBeginIndex, int theEndIndex)
	{
		setRange(theBeginIndex, theEndIndex);
	}

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
