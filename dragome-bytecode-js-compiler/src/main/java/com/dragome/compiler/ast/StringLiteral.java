package com.dragome.compiler.ast;

import org.apache.bcel.generic.Type;

import com.dragome.compiler.generators.AbstractVisitor;

public class StringLiteral extends Expression
{

	private String value;

	public StringLiteral(String theValue)
	{
		value= theValue;
		type= Type.STRING;
	}

	public void visit(AbstractVisitor visitor)
	{
		visitor.visit(this);
	}

	public String getValue()
	{
		return value;
	}

	public void setValue(String theValue)
	{
		value= theValue;
	}

}
