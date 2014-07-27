package com.dragome.compiler.ast;

import com.dragome.compiler.generators.AbstractVisitor;
import com.dragome.compiler.graph.TryHeaderNode;

public class TryStatement extends Block
{

	public TryHeaderNode header;

	public TryStatement()
	{
		super();
	}

	public void addCatchStatement(CatchClause catchStatement)
	{
		if (getChildCount() < 2)
			throw new RuntimeException("Illegal DOM state");
		((Block) getChildAt(1)).appendChild(catchStatement);
	}

	public Block getCatchStatements()
	{
		return (Block) getChildAt(1);
	}

	public Block getFinallyBlock()
	{
		if (getChildCount() < 3)
			return null;
		return (Block) getChildAt(2);
	}

	public void setFinallyBlock(Block finallyBlock)
	{
		setChildAt(2, finallyBlock);
	}

	public Block getTryBlock()
	{
		return (Block) getChildAt(0);
	}

	public void setTryBlock(Block tryBlock)
	{
		setChildAt(0, tryBlock);
		setChildAt(1, new Block());
	}

	public void visit(AbstractVisitor visitor)
	{
		visitor.visit(this);
	}

	public String toString()
	{
		return super.toString();
	}

}
