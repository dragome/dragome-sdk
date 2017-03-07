package com.dragome.compiler.units;

import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;

import com.dragome.compiler.type.Signature;
import com.dragome.compiler.utils.Log;

public abstract class ProcedureUnit extends MemberUnit
{
	private String nameAndSignature;

	public String getNameAndSignature()
	{
		return nameAndSignature;
	}

	private Collection<Signature> targetSignatures= new LinkedHashSet<Signature>();

	public ProcedureUnit(Signature theSignature, ClassUnit theDeclaringClazz, String nameAndSignature)
	{
		super(theSignature, theDeclaringClazz);
		this.nameAndSignature= nameAndSignature;
	}

	public void addTarget(Signature targetSignature)
	{
		if (!targetSignature.toString().contains("#"))
		{
			throw new IllegalArgumentException("Signature must be field or method: " + targetSignature);
		}

		targetSignatures.add(targetSignature);
	}

	public void removeTargets()
	{
		Iterator iter= targetSignatures.iterator();
		while (iter.hasNext())
		{
			iter.next();
			iter.remove();
		}
	}

	public void write(int depth, Writer writer, String... annotationDefaultFound) throws IOException
	{
		String data= getData();
		if (data == null)
			return;
		Log.getLogger().debug(getIndent(depth) + getSignature());

		if (annotationDefaultFound.length > 0)
		{
			String data1= data.substring(0, data.length() - 1);
			String defaultValue= annotationDefaultFound[0];
			data1+= "return \"" + defaultValue + "\";\n}";
			writer.write(data1);
		}
		else
			writer.write(data);
	}

	public String getData()
	{
		if (!declaringClass.isResolved())
			throw new RuntimeException("Class must be resolved");
		return super.getData();
	}

	public Collection<Signature> getTargetSignatures()
	{
		return targetSignatures;
	}

}
