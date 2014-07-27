package com.dragome.compiler.ast;

import org.w3c.dom.DOMException;

import com.dragome.compiler.generators.AbstractVisitor;
import com.dragome.compiler.utils.Log;

public class Block extends ASTNode
{

	public static int TAG= 0;

	private String label;

	private ASTNode firstChild= null;

	private ASTNode lastChild= null;

	private int childCount= 0;

	public Block()
	{
		super();
	}

	public Block(int theBeginIndex)
	{
		setBeginIndex(theBeginIndex);
	}

	public Block(int theBeginIndex, int theEndIndex)
	{
		this(theBeginIndex);
		setRange(theBeginIndex, theEndIndex);
	}

	public int getTargetPc()
	{
		if (lastChild instanceof Jump)
		{
			return ((Jump) lastChild).getTargetIndex();
		}
		return Integer.MAX_VALUE;
	}

	public int getTargetIndex()
	{
		return beginIndex;
	}

	public void setBeginIndex(int theBeginIndex)
	{
		super.setBeginIndex(theBeginIndex);
	}

	public void visit(AbstractVisitor visitor)
	{
		visitor.visit(this);
	}

	public void appendChildren(ASTNode begin, ASTNode end)
	{
		if (begin == null || end == null)
		{
			throw new RuntimeException("Illegal null parameters");
		}
		if (begin.getParentBlock() != null)
			(begin.getParentBlock()).removeChildren(begin, end);

		if (firstChild == null)
		{
			setFirstChildInternal(begin);
		}
		else
		{
			ASTNode prev= getLastChild();
			prev.setNextSibling(begin);
			begin.setPreviousSibling(prev);
		}
		setLastChildInternal(end);

		ASTNode node= begin;
		while (node != null)
		{
			node.setParentNode(this);
			childCount++;
			if (node == end)
				break;
			node= node.getNextSibling();
		}
	}

	public void appendChildren(Block sourceBlock)
	{
		if (sourceBlock.getChildCount() > 0)
		{
			appendChildren(sourceBlock.getFirstChild(), sourceBlock.getLastChild());
		}
	}

	public ASTNode setChildAt(int index, ASTNode newChild)
	{
		if (index == childCount)
		{
			appendChild(newChild);
			return null;
		}
		else if (index < 0 || index > childCount)
		{
			throw new RuntimeException("Index " + index + " out of range [0, " + childCount + "]");
		}

		return replaceChild(newChild, getChildAt(index));
	}

	public ASTNode getChildAt(int index)
	{
		if (childCount == 0)
		{
			throw new RuntimeException("Requested child at index " + index + ", but block has no children");
		}
		if (index < 0 || index >= childCount)
		{
			throw new RuntimeException("Index " + index + " out of range [0, " + (childCount - 1) + "]");
		}

		if (index == childCount - 1)
		{
			return getLastChild();
		}

		ASTNode node= getFirstChild();
		int i= 0;
		while (i < index)
		{
			i++;
			node= node.getNextSibling();
		}

		return node;
	}

	public ASTNode appendChild(ASTNode newChild)
	{
		Log.getLogger().debug("Appending " + newChild + " to " + this);

		unlink(newChild);

		if (firstChild == null)
		{
			newChild.setPreviousSibling(null);
			setFirstChildInternal(newChild);
		}
		else
		{
			ASTNode prev= getLastChild();
			prev.setNextSibling(newChild);
			newChild.setPreviousSibling(prev);
		}
		setLastChildInternal(newChild);
		newChild.setParentNode(this);
		childCount++;

		return newChild;
	}

	public ASTNode replaceChild(ASTNode newChild, ASTNode oldChild)
	{
		Log.getLogger().debug("Replacing " + oldChild + " by " + newChild + " in " + this);
		if (oldChild.getParentNode() != this)
		{
			throw new DOMException(DOMException.NOT_FOUND_ERR, "Node " + oldChild + " is not a child of " + this);
		}
		unlink(newChild);
		if (oldChild.getPreviousSibling() != null)
		{
			oldChild.getPreviousSibling().setNextSibling(newChild);
		}
		if (oldChild.getNextSibling() != null)
		{
			oldChild.getNextSibling().setPreviousSibling(newChild);
		}
		newChild.setPreviousSibling(oldChild.getPreviousSibling());
		newChild.setNextSibling(oldChild.getNextSibling());
		newChild.setParentNode(this);
		if (getFirstChild() == oldChild)
		{
			setFirstChildInternal(newChild);
		}
		if (getLastChild() == oldChild)
		{
			setLastChildInternal(newChild);
		}
		oldChild.setPreviousSibling(null);
		oldChild.setNextSibling(null);
		oldChild.setParentNode(null);
		return oldChild;
	}

	public ASTNode removeChild(ASTNode oldChild)
	{
		Log.getLogger().debug("Removing " + oldChild + " from " + this);
		removeChildren(oldChild, oldChild);

		oldChild.setPreviousSibling(null);
		oldChild.setNextSibling(null);
		return oldChild;
	}

	private void removeChildren(ASTNode begin, ASTNode end)
	{
		if (begin == null || end == null)
			throw new RuntimeException("Illegal null parameters");
		ASTNode node= begin;
		while (node != null)
		{
			if (node.getParentNode() != this)
			{
				throw new DOMException(DOMException.NOT_FOUND_ERR, "Node " + node + " is not a child of " + this);
			}
			node.setParentNode(null);
			childCount--;
			if (node == end)
				break;
			node= node.getNextSibling();
		}

		if (node != end)
		{
			throw new RuntimeException("Node " + end + " is not a right-sibling of " + begin);
		}

		if (begin.getPreviousSibling() != null)
		{
			begin.getPreviousSibling().setNextSibling(end.getNextSibling());
		}
		if (end.getNextSibling() != null)
		{
			end.getNextSibling().setPreviousSibling(begin.getPreviousSibling());
		}
		if (getFirstChild() == begin)
		{
			setFirstChildInternal(end.getNextSibling());
		}
		if (getLastChild() == end)
		{
			setLastChildInternal(begin.getPreviousSibling());
		}
	}

	public void removeChildren()
	{
		if (getFirstChild() != null)
		{
			removeChildren(getFirstChild(), getLastChild());
		}
	}

	public int getChildCount()
	{
		return childCount;
	}

	public ASTNode insertBefore(ASTNode newChild, ASTNode refChild)
	{
		if (refChild == null)
		{
			return appendChild(newChild);
		}

		Log.getLogger().debug("Inserting " + newChild + " before " + refChild + " in " + this);

		if (refChild.getParentNode() != this)
		{
			throw new DOMException(DOMException.NOT_FOUND_ERR, "Reference " + refChild + " is not a child of " + this);
		}

		unlink(newChild);

		if (refChild.getPreviousSibling() != null)
		{
			refChild.getPreviousSibling().setNextSibling(newChild);
		}
		newChild.setPreviousSibling(refChild.getPreviousSibling());
		newChild.setNextSibling(refChild);
		newChild.setParentNode(this);
		childCount++;

		refChild.setPreviousSibling(newChild);

		if (getFirstChild() == refChild)
		{
			setFirstChildInternal(newChild);
		}

		return newChild;
	}

	public void addStatements(java.util.List statements)
	{
		for (int i= 0; i < statements.size(); i++)
		{
			appendChild((ASTNode) statements.get(i));
		}
	}

	public ASTNode getFirstChild()
	{
		return firstChild;
	}

	public ASTNode getLastChild()
	{
		return lastChild;
	}

	private void setFirstChildInternal(ASTNode newFirstChild)
	{
		firstChild= newFirstChild;
		if (firstChild != null)
		{
			firstChild.setPreviousSibling(null);
			beginIndex= Math.min(beginIndex, firstChild.getBeginIndex());
		}
	}

	private void setLastChildInternal(ASTNode newLastChild)
	{
		lastChild= newLastChild;
		if (lastChild != null)
		{
			lastChild.setNextSibling(null);
			endIndex= Math.max(endIndex, lastChild.getEndIndex());
		}
	}

	private void unlink(ASTNode node)
	{
		if (node.getParentBlock() != null)
		{
			node.getParentBlock().removeChild(node);
		}
	}

	public String getLabel()
	{
		if (label == null)
			throw new RuntimeException("Statement is not labeled");
		return label;
	}

	public void setLabel(String theLabel)
	{
		label= theLabel;
	}

	public boolean isLabeled()
	{
		return label != null;
	}

	public String setLabeled()
	{
		if (label == null)
			label= "L" + (++TAG);
		return label;
	}
}
