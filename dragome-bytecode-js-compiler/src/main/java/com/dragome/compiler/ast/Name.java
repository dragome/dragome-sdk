package com.dragome.compiler.ast;

import com.dragome.compiler.generators.AbstractVisitor;

/**
 * Copyright by Wolfgang Kuehn 2005
 * Created on Feb 20, 2005
 */
public class Name extends Expression
{

	private String identifier;

	public Name(String newIdentifier)
	{

		identifier= newIdentifier;
	}

	public void visit(AbstractVisitor visitor)
	{
		visitor.visit(this);
	}

	public boolean equals(Object obj)
	{
		if (!(obj instanceof Name))
			return false;
		return identifier.equals(((Name) obj).identifier);
	}

	public String getIdentifier()
	{
		return identifier;
	}

	public void setIdentifier(String theIdentifier)
	{
		identifier= theIdentifier;
	}
}
