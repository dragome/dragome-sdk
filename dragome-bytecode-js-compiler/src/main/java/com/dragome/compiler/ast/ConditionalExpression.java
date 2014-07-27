package com.dragome.compiler.ast;

import com.dragome.compiler.generators.AbstractVisitor;

public class ConditionalExpression extends Expression
{

	private Expression conditionExpression= null;

	private Expression thenExpression= null;

	private Expression elseExpression= null;

	public void visit(AbstractVisitor visitor)
	{
		visitor.visit(this);
	}

	public Expression getConditionExpression()
	{
		return conditionExpression;
	}

	public void setConditionExpression(Expression theConditionExpression)
	{
		widen(theConditionExpression);
		conditionExpression= theConditionExpression;
	}

	public Expression getElseExpression()
	{
		return elseExpression;
	}

	public void setElseExpression(Expression theElseExpression)
	{
		widen(theElseExpression);
		elseExpression= theElseExpression;
	}

	public Expression getThenExpression()
	{
		return thenExpression;
	}

	public void setThenExpression(Expression theThenExpression)
	{
		widen(theThenExpression);
		thenExpression= theThenExpression;
	}

}
