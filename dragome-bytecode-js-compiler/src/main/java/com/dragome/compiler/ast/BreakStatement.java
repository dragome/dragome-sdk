package com.dragome.compiler.ast;

import com.dragome.compiler.generators.AbstractVisitor;

public class BreakStatement extends LabeledJump
{

	public BreakStatement(String theLabel)
	{
		super(theLabel);
	}

	public BreakStatement(Block block)
	{
		super(block);
	}

	public void visit(AbstractVisitor visitor)
	{
		visitor.visit(this);
	}

}
