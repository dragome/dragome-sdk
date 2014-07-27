package com.dragome.compiler.ast;

import com.dragome.compiler.generators.AbstractVisitor;

public class BooleanLiteral extends Expression
{

	public static BooleanLiteral FALSE= new BooleanLiteral(false);

	public static BooleanLiteral TRUE= new BooleanLiteral(true);

	private boolean value;

	public BooleanLiteral(boolean theValue)
	{
		value= theValue;
	}

	public void visit(AbstractVisitor visitor)
	{
		visitor.visit(this);
	}

	public boolean getValue()
	{
		return value;
	}

	public void setValue(boolean theValue)
	{
		value= theValue;
	}
}
