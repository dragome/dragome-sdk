/**
 * Copyright by Wolfgang Kuehn 2005
 */
package com.dragome.compiler.ast;

public class ConditionalBranch extends Branch
{

	private Expression expression;

	public ConditionalBranch(int targetIndex)
	{
		super(targetIndex);
	}

	public ConditionalBranch(int theBeginIndex, int theEndIndex, int targetIndex)
	{
		super(targetIndex);
		setExpression(new Expression(theBeginIndex, theEndIndex));
	}

	public Expression getExpression()
	{
		return expression;
	}

	public void setExpression(Expression theExpression)
	{
		expression= theExpression;
		widen(theExpression);
		appendChild(theExpression);
	}

}
