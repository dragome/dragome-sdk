package com.dragome.compiler.ast;

import com.dragome.compiler.generators.AbstractVisitor;
import com.dragome.compiler.parser.Pass1;

public class WhileStatement extends LoopStatement
{

	public WhileStatement()
	{
		super();
		Pass1.loopFound= false;
	}

	public WhileStatement(int theBeginIndex)
	{
		super(theBeginIndex);
	}

	public WhileStatement(int theBeginIndex, int theEndIndex)
	{
		super(theBeginIndex, theEndIndex);
	}

	public void visit(AbstractVisitor visitor)
	{
		visitor.visit(this);
	}
}
