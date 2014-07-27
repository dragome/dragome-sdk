package com.dragome.compiler.ast;

import com.dragome.compiler.generators.AbstractVisitor;

public class ASTNode
{

	public static final int BEFORE= 0;

	public static final int AFTER= 1;

	public static final int SAME= 2;

	public static final int CONTAINS= 3;

	public static final int ISCONTAINED= 4;

	int beginIndex= Integer.MAX_VALUE;

	int endIndex= Integer.MIN_VALUE;

	private ASTNode parent= null;

	private ASTNode previousSibling= null;

	private ASTNode nextSibling= null;

	private int stackDelta= 0;

	public ASTNode()
	{
		super();
	}

	public ASTNode(int theBeginIndex, int theEndIndex)
	{
		setRange(theBeginIndex, theEndIndex);
	}

	public int getStackDelta()
	{
		return stackDelta;
	}

	public void setStackDelta(int theStackDelta)
	{
		stackDelta= theStackDelta;
	}

	public void widen(ASTNode node)
	{
		leftWiden(node.beginIndex);
		rightWiden(node.endIndex);
	}

	public void leftWiden(int targetBeginIndex)
	{
		if (targetBeginIndex < beginIndex)
			beginIndex= targetBeginIndex;
	}

	public void rightWiden(int targetEndIndex)
	{
		if (targetEndIndex > endIndex)
			endIndex= targetEndIndex;
	}

	public void setRange(int theBeginIndex, int theEndIndex)
	{
		setBeginIndex(theBeginIndex);
		setEndIndex(theEndIndex);
	}

	public int getBeginIndex()
	{
		return beginIndex;
	}

	public void setBeginIndex(int theBeginIndex)
	{
		beginIndex= theBeginIndex;
	}

	public int getEndIndex()
	{
		return endIndex;
	}

	public void setEndIndex(int theEndIndex)
	{
		endIndex= theEndIndex;
	}

	public boolean isRightSiblingOf(ASTNode leftSibling)
	{
		for (ASTNode node= this; node != null; node= node.getPreviousSibling())
		{
			if (node == leftSibling)
			{
				return true;
			}
		}
		return false;
	}

	public ASTNode rightMostSibling()
	{
		for (ASTNode node= this;;)
		{
			if (node.getNextSibling() == null)
			{
				return node;
			}
			node= node.getNextSibling();
		}
	}

	public boolean isAncestorOf(ASTNode node)
	{
		do
		{
			node= node.getParentNode();
			if (node == this)
			{
				return true;
			}
		}
		while (node != null);

		return false;
	}

	public String toString()
	{
		StringBuilder sb= new StringBuilder();
		sb.append(getClass().getSimpleName());
		if (getBeginIndex() != Integer.MAX_VALUE)
		{
			sb.append("[");
			sb.append(getBeginIndex());
			sb.append(", ");
			sb.append(getEndIndex());
			sb.append("]");
		}
		return sb.toString();
	}

	public ASTNode getParentNode()
	{
		return parent;
	}

	public Block getParentBlock()
	{
		return (Block) parent;
	}

	public Block getLogicalParentBlock()
	{
		if (parent != null && parent.parent instanceof IfStatement)
		{
			return (Block) parent.parent;
		}
		return (Block) parent;
	}

	public void setParentNode(ASTNode theParent)
	{
		parent= theParent;
	}

	public void visit(AbstractVisitor visitor)
	{
		visitor.visit(this);
	}

	public ASTNode getNextSibling()
	{
		return nextSibling;
	}

	public void setNextSibling(ASTNode theNextSibling)
	{
		nextSibling= theNextSibling;
	}

	public ASTNode getPreviousSibling()
	{
		return previousSibling;
	}

	public void setPreviousSibling(ASTNode thePreviousSibling)
	{
		previousSibling= thePreviousSibling;
	}
}
