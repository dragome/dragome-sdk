package com.dragome.compiler.ast;

import com.dragome.compiler.generators.AbstractVisitor;

public class PrefixExpression extends PStarExpression
{

	static public Operator NOT= new Operator("!");

	static public Operator MINUS= new Operator("-");

	static public Operator PLUS= new Operator("+");

	static public Operator COMPLEMENT= new Operator("~");

	public PrefixExpression()
	{
		super();
	}

	public void visit(AbstractVisitor visitor)
	{
		visitor.visit(this);
	}
}
