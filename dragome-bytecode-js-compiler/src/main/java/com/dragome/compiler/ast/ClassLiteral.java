package com.dragome.compiler.ast;

import com.dragome.compiler.generators.AbstractVisitor;
import com.dragome.compiler.type.Signature;

public class ClassLiteral extends Expression
{

	private Signature signature;

	public ClassLiteral(Signature theSignature)
	{
		signature= theSignature;
	}

	public void visit(AbstractVisitor visitor)
	{
		visitor.visit(this);
	}

	public Signature getSignature()
	{
		return signature;
	}

}
