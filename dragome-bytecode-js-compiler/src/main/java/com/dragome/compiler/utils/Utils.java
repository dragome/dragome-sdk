package com.dragome.compiler.utils;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.util.Date;
import java.util.Properties;

import org.apache.bcel.generic.ArrayType;
import org.apache.bcel.generic.BasicType;
import org.apache.bcel.generic.ObjectType;
import org.apache.bcel.generic.Type;

import com.dragome.compiler.DragomeJsCompiler;
import com.dragome.compiler.ast.ASTNode;
import com.dragome.compiler.ast.MethodDeclaration;

public final class Utils
{
	private Utils()
	{
	}

	public static String generateExceptionMessage(MethodDeclaration methodDecl, ASTNode node)
	{
		String msg= null;
		if (node != null)
		{
			int line= methodDecl.getLineNumberCursor().getLineNumber(node);
			if (line != -1)
			{
				msg= "Error near line " + line;
			}
		}
		if (msg == null)
		{
			msg= "Error";
		}

		msg+= " in " + methodDecl.getMethodBinding();

		return msg;
	}

	public static RuntimeException generateException(Throwable e, MethodDeclaration methodDecl, ASTNode node)
	{
		String msg= generateExceptionMessage(methodDecl, node);
		DragomeJsCompiler.errorCount++;
		Log.getLogger().error(msg);
		return new RuntimeException(msg, e);
	}

	public static String stackTraceToString(Throwable e)
	{
		StringWriter sw= new StringWriter();
		PrintWriter writer= new PrintWriter(sw);
		e.printStackTrace(writer);
		writer.close();
		return sw.getBuffer().toString();
	}

	public static String currentTimeStamp()
	{
		return DateFormat.getDateTimeInstance().format(new Date());
	}

	public static String getSignature(Type type)
	{
		String signature;

		if (type instanceof ArrayType)
		{
			ArrayType aType= (ArrayType) type;
			signature= getSignature(aType.getBasicType());
			for (int i= 0; i < aType.getDimensions(); i++)
			{
				signature+= "[]";
			}
		}
		else if (type instanceof ObjectType)
		{
			signature= ((ObjectType) type).getClassName();
		}
		else
		{
			if (!(type instanceof BasicType))
				throw new RuntimeException();
			signature= type.toString();
		}

		return signature;
	}

	public static String escape(String str)
	{
		int len= str.length();
		StringBuffer buf= new StringBuffer(len + 5);
		char[] ch= str.toCharArray();

		for (int i= 0; i < len; i++)
		{
			switch (ch[i])
			{
				case '\\':
					buf.append("\\\\");
					break;
				case '\n':
					buf.append("\\n");
					break;
				case '\r':
					buf.append("\\r");
					break;
				case '\t':
					buf.append("\\t");
					break;
				case '\b':
					buf.append("\\b");
					break;
				case '"':
					buf.append("\\\"");
					break;
				default:
					buf.append(ch[i]);
			}
		}

		return '"' + buf.toString() + '"';
	}

	public static File resolve(File baseDir, String path)
	{
		File resolvedFile= new File(path);
		if (!resolvedFile.isAbsolute())
		{
			resolvedFile= new File(baseDir, path);
		}
		return resolvedFile;
	}

}
