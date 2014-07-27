package com.dragome.compiler.ast;

import org.apache.bcel.generic.Type;

import com.dragome.compiler.generators.AbstractVisitor;

/**
 * Copyright by Wolfgang Kuehn 2005
 * Created on Feb 27, 2005
 */
public class InstanceofExpression extends Expression
{
	private Expression leftOperand;

	private Type rightOperand;

	public void visit(AbstractVisitor visitor)
	{
		visitor.visit(this);
	}

	public Expression getLeftOperand()
	{
		return leftOperand;
	}

	public void setLeftOperand(Expression theLeftOperand)
	{
		leftOperand= theLeftOperand;
	}

	public Type getRightOperand()
	{
		return rightOperand;
	}

	public void setRightOperand(Type theRightOperand)
	{
		rightOperand= theRightOperand;
	}
}
