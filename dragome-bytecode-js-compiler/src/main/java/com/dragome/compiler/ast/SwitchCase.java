package com.dragome.compiler.ast;

import java.util.List;

import com.dragome.compiler.generators.AbstractVisitor;

public class SwitchCase extends Block
{

	private List<NumberLiteral> expressions;

	public SwitchCase(int theBeginIndex)
	{
		super(theBeginIndex);
	}

	public void visit(AbstractVisitor visitor)
	{
		visitor.visit(this);
	}

	public List<NumberLiteral> getExpressions()
	{
		return expressions;
	}

	public void setExpressions(List<NumberLiteral> theExpressions)
	{
		expressions= theExpressions;
	}

}
