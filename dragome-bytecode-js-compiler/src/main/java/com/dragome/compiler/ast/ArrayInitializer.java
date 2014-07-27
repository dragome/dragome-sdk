package com.dragome.compiler.ast;

import java.util.List;

import com.dragome.compiler.generators.AbstractVisitor;

public class ArrayInitializer extends Expression
{

	private List<Expression> expressions= new java.util.ArrayList<Expression>();

	public void visit(AbstractVisitor visitor)
	{
		visitor.visit(this);
	}

	public List<Expression> getExpressions()
	{
		return expressions;
	}
}
