package com.dragome.compiler.ast;

import org.apache.bcel.generic.Type;

import com.dragome.compiler.parser.Form;

/**
 * Copyright by Wolfgang Kuehn 2005
 * Created on Feb 17, 2005
 */
public class Expression extends Block implements Cloneable
{

	Type type= Type.UNKNOWN;

	public Expression()
	{
		super();
	}

	public Expression(int theBeginIndex, int theEndIndex)
	{
		super(theBeginIndex, theEndIndex);
	}

	public Object clone()
	{
		if (getChildCount() > 0)
			throw new RuntimeException("Cannot clone expression with children");
		ASTNode node;
		try
		{
			node= (ASTNode) super.clone();
		}
		catch (CloneNotSupportedException e)
		{
			return null;
		}
		node.setParentNode(null);
		node.setPreviousSibling(null);
		node.setNextSibling(null);
		return node;
	}

	public Type getTypeBinding()
	{
		return type;
	}

	public void setTypeBinding(Type theType)
	{
		type= theType;
	}

	public int getCategory()
	{
		if (getTypeBinding().getType() == Type.LONG.getType())
			return Form.CATEGORY2;
		if (getTypeBinding().getType() == Type.DOUBLE.getType())
			return Form.CATEGORY2;
		return Form.CATEGORY1;

	}

}
