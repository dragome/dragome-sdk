package com.dragome.compiler.ast;

import org.apache.bcel.generic.ObjectType;

import com.dragome.compiler.generators.AbstractVisitor;

public class ClassInstanceCreation extends MethodInvocation
{

	public ClassInstanceCreation(ObjectType theType)
	{
		type= theType;
	}

	public ClassInstanceCreation(MethodDeclaration methodDecl, MethodBinding methodBinding)
	{
		super(methodDecl, methodBinding);
	}

	public void visit(AbstractVisitor visitor)
	{
		visitor.visit(this);
	}

}
