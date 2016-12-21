package com.dragome.utils;

public class NamingUtils {

	public static String javaToDragomeNotation(Class<?> aClazz) {
		return aClazz.getName().replace(".", "_");
	}
}
