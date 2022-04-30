package com.dragome.helpers;

public class StringHelper
{

	public static String enhanceName(String name)
	{
		name= StringHelper.extractFromCamel(name);
		name= ReflectionHelper.capitalise(name);
		return name;
	}

	public static String extractFromCamel(String name)
	{
		String name1= name.replaceAll("([A-Z][a-zñ]+)", " $1") // Words beginning with UC
				.replaceAll("([A-Z][A-Z]+)", " $1") // "Words" of only UC
				.replaceAll("([^A-Za-zñ ]+)", " $1") // "Words" of non-letters
				.trim();
	
		return name1;
	}

}
