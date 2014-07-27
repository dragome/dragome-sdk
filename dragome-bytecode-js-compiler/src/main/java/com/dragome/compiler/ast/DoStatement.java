package com.dragome.compiler.ast;

import com.dragome.compiler.generators.AbstractVisitor;

public class DoStatement extends LoopStatement
{

	public DoStatement()
	{
		super();
	}

	public DoStatement(int theBeginIndex)
	{
		super(theBeginIndex);
	}

	public DoStatement(int theBeginIndex, int theEndIndex)
	{
		super(theBeginIndex, theEndIndex);
	}

	public void visit(AbstractVisitor visitor)
	{
		visitor.visit(this);
	}
}
