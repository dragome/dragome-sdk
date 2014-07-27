package com.dragome.compiler.ast;

import com.dragome.compiler.generators.AbstractVisitor;

public class CatchClause extends Block
{

	private VariableDeclaration exception;

	public CatchClause(int theBeginIndex)
	{
		super(theBeginIndex);
	}

	public void visit(AbstractVisitor visitor)
	{
		visitor.visit(this);
	}

	public VariableDeclaration getException()
	{
		return exception;
	}

	public void setException(VariableDeclaration theException)
	{
		exception= theException;
	}
}
