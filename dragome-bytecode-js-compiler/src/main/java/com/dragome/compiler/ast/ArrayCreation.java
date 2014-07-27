package com.dragome.compiler.ast;

import java.util.List;

import org.apache.bcel.generic.Type;

import com.dragome.compiler.Project;
import com.dragome.compiler.generators.AbstractVisitor;

public class ArrayCreation extends Expression
{

	private List<ASTNode> dimensions;

	private ArrayInitializer initializer;

	public ArrayCreation(MethodDeclaration methodDecl, Type theType, List<ASTNode> theDimensions)
	{
		type= theType;
		dimensions= theDimensions;
		for (ASTNode dimension : dimensions)
		{
			this.widen(dimension);
		}
		Project.getSingleton().addReference(methodDecl, this);
	}

	public void visit(AbstractVisitor visitor)
	{
		visitor.visit(this);
	}

	public ArrayInitializer getInitializer()
	{
		return initializer;
	}

	public void setInitializer(ArrayInitializer theInitializer)
	{
		initializer= theInitializer;
	}

	public List<ASTNode> getDimensions()
	{
		return dimensions;
	}

}
