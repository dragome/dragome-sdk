package com.dragome.compiler.ast;

abstract public class LabeledJump extends Jump
{

	String label;

	public LabeledJump(String newLabel)
	{
		super();
		label= newLabel;
	}

	public LabeledJump(Block block)
	{
		super();
		label= block.setLabeled();
	}

	public String getLabel()
	{
		return label;
	}
}
