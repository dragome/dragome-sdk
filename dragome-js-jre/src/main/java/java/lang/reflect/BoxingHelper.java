package java.lang.reflect;

import com.dragome.commons.javascript.ScriptHelper;

public class BoxingHelper
{

	public static Object convertObjectToPrimitive(Object result)
	{
		if (result == null || !(result instanceof Object))
			return result;

		ScriptHelper.put("result", result, null);
		if (ScriptHelper.evalBoolean("result.$getClass$java_lang_Class != null", null))  //TODO: revisar
		{
			Class<?> type= result.getClass();
			if (type.equals(Integer.class))
			{
				int intValue= ((Integer) result).intValue();
				ScriptHelper.put("intValue", intValue, null);
				return ScriptHelper.eval("intValue", null);
			}
			else if (type.equals(Boolean.class))
			{
				boolean booleanValue= ((Boolean) result).booleanValue();
				ScriptHelper.put("booleanValue", booleanValue, null);
				return ScriptHelper.eval("booleanValue", null);
			}
			else if (type.equals(Long.class))
			{
				long longValue= ((Long) result).longValue();
				ScriptHelper.put("longValue", longValue, null);
				return ScriptHelper.eval("longValue", null);
			}
			else if (type.equals(Short.class))
			{
				short shortValue= ((Short) result).shortValue();
				ScriptHelper.put("shortValue", shortValue, null);
				return ScriptHelper.eval("shortValue", null);
			}
			else if (type.equals(Byte.class))
			{
				byte byteValue= ((Byte) result).byteValue();
				ScriptHelper.put("byteValue", byteValue, null);
				return ScriptHelper.eval("byteValue", null);
			}
			else if (type.equals(Float.class))
			{
				float floatValue= ((Float) result).floatValue();
				ScriptHelper.put("floatValue", floatValue, null);
				return ScriptHelper.eval("floatValue", null);
			}
			else if (type.equals(Double.class))
			{
				double doubleValue= ((Double) result).doubleValue();
				ScriptHelper.put("doubleValue", doubleValue, null);
				return ScriptHelper.eval("doubleValue", null);
			}
			else if (type.equals(Character.class))
			{
				char charValue= ((Character) result).charValue();
				ScriptHelper.put("charValue", charValue, null);
				return ScriptHelper.eval("charValue", null);
			}
			else
				return result;
		}
		else
			return result;
	}

}
