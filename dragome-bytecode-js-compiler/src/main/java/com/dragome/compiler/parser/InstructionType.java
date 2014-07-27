package com.dragome.compiler.parser;

public class InstructionType
{

	private short code;

	private String name;

	private Form[] forms;

	public InstructionType(short theCode, String theName, int formCount)
	{
		code= theCode;
		name= theName;
		forms= new Form[formCount];
	}

	public int getFormCount()
	{
		return forms.length;
	}

	public void setForm(Form form, int index)
	{
		forms[index]= form;
		form.setIndex(index);
	}

	public Form getForm(int index)
	{
		return forms[index];
	}

	public String getName()
	{
		return name;
	}

	public void setName(String theName)
	{
		name= theName;
	}

	public short getCode()
	{
		return code;
	}

	public void setCode(short theCode)
	{
		code= theCode;
	}

}
