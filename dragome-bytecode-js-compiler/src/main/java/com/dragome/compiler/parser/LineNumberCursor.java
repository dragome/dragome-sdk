package com.dragome.compiler.parser;

import java.util.Arrays;
import java.util.Comparator;

import org.apache.bcel.classfile.Code;
import org.apache.bcel.classfile.LineNumber;
import org.apache.bcel.classfile.LineNumberTable;

import com.dragome.compiler.ast.ASTNode;

public class LineNumberCursor
{

	private class LineNumberComparator implements Comparator<LineNumber>
	{

		public int compare(LineNumber arg0, LineNumber arg1)
		{
			return arg0.getStartPC() - arg1.getStartPC();
		}

	}

	private LineNumber[] lineNumbers= null;

	private int index= 0;

	private int markedLineNumber= -1;

	private int length;

	public LineNumberCursor(Code code)
	{
		if (code == null)
			return;
		LineNumberTable table= code.getLineNumberTable();
		if (table == null)
			return;
		lineNumbers= table.getLineNumberTable();
		length= lineNumbers.length;

		Arrays.sort(lineNumbers, new LineNumberComparator());
	}

	public boolean hasLineNumbers()
	{
		return lineNumbers != null;
	}

	public int getLineNumber(ASTNode node)
	{
		if (!hasLineNumbers())
			return -1;

		while (node.getBeginIndex() == Integer.MAX_VALUE && node.getPreviousSibling() != null)
		{
			node= node.getPreviousSibling();
		}

		int pc= node.getBeginIndex();
		L: if (pc > lineNumbers[index].getStartPC())
		{
			do
			{
				if (index + 1 == length)
					break L;
				index++;
			}
			while (pc >= lineNumbers[index].getStartPC());
			index--;
		}
		else if (pc < lineNumbers[index].getStartPC())
		{
			do
			{
				if (index == 0)
					break L;
				index--;
			}
			while (pc <= lineNumbers[index].getStartPC());
			index++;
		}

		return lineNumbers[index].getLineNumber();
	}

	public int getAndMarkLineNumber(ASTNode node)
	{
		if (!hasLineNumbers())
			return -1;

		int lineNumber= getLineNumber(node);
		if (lineNumber == markedLineNumber)
			return -1;

		markedLineNumber= lineNumber;
		return lineNumber;
	}

}
