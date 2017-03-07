package com.dragome.compiler.units;

import java.io.IOException;
import java.io.Serializable;
import java.io.Writer;
import java.util.LinkedHashMap;
import java.util.Map;

import com.dragome.compiler.type.Signature;
import com.dragome.compiler.utils.Log;

public abstract class Unit implements Serializable
{
	private Signature signature;

	private String data;

	private transient boolean isTainted= false;

	private static transient Map<Integer, String> indentPerDepth= new LinkedHashMap<Integer, String>();

	public Unit()
	{
	}

	public abstract void write(int depth, Writer writer, String... annotationDefaultFound) throws IOException;

	String getIndent(int depth)
	{
		String indent= indentPerDepth.get(depth);
		if (indent == null)
		{
			indent= "";
			for (int i= 0; i < depth; i++)
				indent+= '\t';

			indentPerDepth.put(depth, indent);
		}
		return indent;
	}

	public String toString()
	{
		return signature.toString();
	}

	public Signature getSignature()
	{
		return signature;
	}

	void setSignature(Signature theSignature)
	{
		signature= theSignature;
	}

	public String getData()
	{
		return data;
	}

	public void setData(String theData)
	{
		data= theData;
	}

	public boolean isTainted()
	{
		return isTainted;
	}

	public void setTainted()
	{
		if (!isTainted)
			Log.getLogger().debug("Taint " + this);

		isTainted= true;
	}

}
