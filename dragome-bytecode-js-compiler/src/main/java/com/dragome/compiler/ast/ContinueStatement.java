package com.dragome.compiler.ast;

import com.dragome.compiler.generators.AbstractVisitor;

public class ContinueStatement extends LabeledJump
{

	public ContinueStatement(Block block)
	{
		super(block);
	}

	public void visit(AbstractVisitor visitor)
	{
		visitor.visit(this);
	}

}
