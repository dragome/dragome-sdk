package com.dragome.forms.bindings.builders;

public enum Order
{
	ASC, DESC;

	public Order swap()
	{
		return this == ASC ? DESC : ASC;
	}
}
