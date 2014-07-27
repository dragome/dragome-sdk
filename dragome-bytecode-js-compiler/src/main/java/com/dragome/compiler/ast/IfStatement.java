package com.dragome.compiler.ast;

import com.dragome.compiler.generators.AbstractVisitor;

public class IfStatement extends Block
{

	public IfStatement()
	{
		super();
	}

	public Expression getExpression()
	{
		return (Expression) getChildAt(0);
	}

	public void setExpression(Expression expression)
	{
		widen(expression);
		setChildAt(0, expression);
	}

	public Block getIfBlock()
	{
		return (Block) getChildAt(1);
	}

	public void setIfBlock(Block block)
	{
		widen(block);
		setChildAt(1, block);
	}

	public Block getElseBlock()
	{
		if (getChildCount() < 3)
			return null;
		return (Block) getChildAt(2);
	}

	public void setElseBlock(Block block)
	{
		widen(block);
		setChildAt(2, block);
	}

	public void visit(AbstractVisitor visitor)
	{
		visitor.visit(this);
	}

}
