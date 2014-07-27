package com.dragome.compiler.ast;

public class Branch extends Expression
{

	private int targetIndex= -1;

	private ASTNode target;

	public Branch()
	{
		super();
	}

	public Branch(int theTargetIndex)
	{
		setTargetIndex(theTargetIndex);
	}

	public boolean isBackward()
	{
		return getTargetIndex() <= getBeginIndex();
	}

	public int getTargetIndex()
	{
		return targetIndex;
	}

	public void setTargetIndex(int theTargetIndex)
	{
		targetIndex= theTargetIndex;
	}

	public String toString()
	{
		String s= getClass().getName() + "[" + getBeginIndex() + ", " + getEndIndex() + ", " + targetIndex + "] -> ";
		if (target != null)
		{
			Exception e= new Exception();
			if (target == this)
			{
				s+= "self";
			}
			else if (e.getStackTrace().length > 20)
			{
				s+= "...";
			}
			else
			{
				s+= "" + target.toString();
			}
		}
		else
		{
			s+= "null";
		}
		return s;
	}

	public ASTNode getTarget()
	{
		return target;
	}

	public void setTarget(ASTNode theTarget)
	{
		target= theTarget;
		if (theTarget != null)
			targetIndex= theTarget.getBeginIndex();
	}
}
