package com.dragome.compiler.parser;

import org.apache.bcel.generic.Type;

public class Form
{

	public static int CATEGORY1= 1;

	public static int CATEGORY2= 2;

	public static class Value
	{
		public String type;

		public String name;

		public Value(String theType, String theName)
		{
			type= theType;
			name= theName;
		}

		public int getCategory()
		{
			return type.equals("cat2") || type.equals("long") || type.equals("double") ? CATEGORY2 : CATEGORY1;
		}
	}

	private int index;

	private Form.Value[] ins;

	private Form.Value[] outs;

	private Form.Value[] operands;

	private Type type;

	public Form.Value[] getIns()
	{
		return ins;
	}

	public void setIns(Form.Value[] theIns)
	{
		ins= theIns;
	}

	public Form.Value[] getOperands()
	{
		return operands;
	}

	public void setOperands(Form.Value[] theOperands)
	{
		operands= theOperands;
	}

	public Form.Value[] getOuts()
	{
		return outs;
	}

	public void setOuts(Form.Value[] theOuts)
	{
		outs= theOuts;

		if (theOuts.length != 1)
			return;

		String s= theOuts[0].type;
		if (s.equals("object"))
			type= Type.OBJECT;
		else if (s.equals("int"))
			type= Type.INT;
		else if (s.equals("short"))
			type= Type.SHORT;
		else if (s.equals("byte"))
			type= Type.SHORT;
		else if (s.equals("long"))
			type= Type.LONG;
		else if (s.equals("float"))
			type= Type.FLOAT;
		else if (s.equals("double"))
			type= Type.DOUBLE;
		else if (!s.equals("cat1") && !s.equals("returnAddress") && !s.equals(""))
			throw new RuntimeException("Unhandled type: " + s);
	}

	public int getOpStackDelta()
	{
		return getOuts().length - getIns().length;
	}

	public Type getResultType()
	{
		if (type == null)
			throw new RuntimeException("Result type is not available for " + this);

		return type;
	}

	public int getIndex()
	{
		return index;
	}

	public void setIndex(int theIndex)
	{
		index= theIndex;
	}
}
