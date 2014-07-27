package javascript;

import java.io.PrintStream;

import com.dragome.commons.javascript.ConsoleOutputStream;
import com.dragome.commons.javascript.ScriptHelper;

public class Utils
{
	public static void init()
	{
		System.err= new PrintStream(new ConsoleOutputStream());
		System.out= new PrintStream(new ConsoleOutputStream());
	}

	/**
	 * This method unifies the instructions lcmp, fcmp<op> and dcmp<op> with
	 * op = (g|l).
	 * It compares two numerical values. The value gORl must be 1 for (g)
	 * or -1 for (l), otherwise 0.
	 * If either value1 or value2 is NaN, then lg is returned.
	 */
	public static int cmp(double value1, double value2, int gORl)
	{
		if (gORl != 0 && (Double.isNaN(value1) || Double.isNaN(value2)))
			return gORl;
		ScriptHelper.put("value1", value1, null);
		ScriptHelper.put("value2", value2, null);
		return ScriptHelper.evalInt("dragomeJs.cmp(value1, value2)", null);
	}

}
