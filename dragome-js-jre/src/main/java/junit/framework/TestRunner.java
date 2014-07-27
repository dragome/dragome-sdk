package junit.framework;

import java.lang.reflect.Method;

public class TestRunner
{
	public static void runTests(Class<?> clazz)
	{
		try
		{
			Method[] methods= clazz.getMethods();

			Object test= clazz.newInstance();

			for (Method method : methods)
			{
				if (method.getName().startsWith("test"))
				{
					try
					{
						method.invoke(test, null);
						System.out.println("method tested: " + method.getName() + " OK!");
					}
					catch (Error e)
					{
						System.out.println("method tested: " + method.getName() + " FAILED!");
						e.printStackTrace();
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
				}
			}
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}
}
