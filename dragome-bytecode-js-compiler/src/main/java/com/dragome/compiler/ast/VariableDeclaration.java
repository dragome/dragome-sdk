package com.dragome.compiler.ast;

import java.util.ArrayList;
import java.util.List;

import org.apache.bcel.classfile.LocalVariable;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.Type;

import com.dragome.compiler.exceptions.UnhandledCompilerProblemException;
import com.dragome.compiler.generators.AbstractVisitor;

public class VariableDeclaration extends ASTNode
{
	public static int LOCAL= 0;

	public static int NON_LOCAL= 1;

	public static int LOCAL_PARAMETER= 2;

	private String name;

	private Type type;

	private int modifiers;

	private int location;

	private boolean isInitialized;

	public List<VariableBinding> vbs= new ArrayList<VariableBinding>();

	public static String getLocalVariableName(Method method, int slot, int pc)
	{
		if (method.getLocalVariableTable() != null)
		{

			LocalVariable[] table= method.getLocalVariableTable().getLocalVariableTable();
			LocalVariable lvar= null;
			for (int i= 0; i < table.length; i++)
			{
				lvar= table[i];
				if (lvar.getIndex() == slot && lvar.getStartPC() <= pc && pc <= lvar.getStartPC() + lvar.getLength())
				{
					String name2= lvar.getName();
					if (name2.equals("in"))
						name2= "in_";
					if (isSuperpositionDetected(table, lvar))
						throw new UnhandledCompilerProblemException();

					return name2;
				}
			}

		}

		return "l" + slot;
	}

	private static boolean isSuperpositionDetected(LocalVariable[] table, LocalVariable lvar)
	{
		for (LocalVariable localVariable : table)
		{
			if (lvar != localVariable && lvar.getName().equals(localVariable.getName()) && lvar.getStartPC() >= localVariable.getStartPC() && (lvar.getStartPC() + lvar.getLength() <= localVariable.getStartPC() + localVariable.getLength()))
				return true;
		}
		return false;
	}

	public VariableDeclaration(boolean theIsInitialized)
	{
		location= VariableDeclaration.LOCAL;
		isInitialized= theIsInitialized;
	}

	public VariableDeclaration(int theLocation)
	{
		if (theLocation != VariableDeclaration.NON_LOCAL && theLocation != VariableDeclaration.LOCAL_PARAMETER)
		{
			throw new RuntimeException("Illegal location specified: " + theLocation);
		}
		location= theLocation;
		isInitialized= (theLocation == VariableDeclaration.NON_LOCAL);
	}

	public void visit(AbstractVisitor visitor)
	{
		visitor.visit(this);
	}

	public String getName()
	{
		return name;
	}

	public void setName(String theName)
	{
		name= theName;
	}

	public int getModifiers()
	{
		return modifiers;
	}

	public void setModifiers(int theModifiers)
	{
		modifiers= theModifiers;
	}

	public Type getType()
	{
		return type;
	}

	public void setType(Type theType)
	{
		type= theType;
	}

	public int getLocation()
	{
		return location;
	}

	public boolean isInitialized()
	{
		return isInitialized;
	}
}
