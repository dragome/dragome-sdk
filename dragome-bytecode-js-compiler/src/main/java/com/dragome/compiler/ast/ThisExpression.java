package com.dragome.compiler.ast;

import org.apache.bcel.generic.Type;

import com.dragome.compiler.generators.AbstractVisitor;

public class ThisExpression extends VariableBinding
{

	private static VariableDeclaration vd;

	static
	{
		vd= new VariableDeclaration(VariableDeclaration.NON_LOCAL);
		vd.setName("this");
		vd.setType(Type.OBJECT);
	}

	public ThisExpression()
	{
		super(vd);
	}

	public void visit(AbstractVisitor visitor)
	{
		visitor.visit(this);
	}
}
