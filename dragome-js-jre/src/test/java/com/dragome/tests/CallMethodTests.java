package com.dragome.tests;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;

import junit.framework.TestCase;

@RunWith(DragomeTestRunner.class)
public class CallMethodTests extends TestCase
{
	static List<String> oal= new ArrayList<>();

	@Test
	public void testTryLoopIf()
	{
		try
		{
			while (!oal.isEmpty())
			{
				String colObj= oal.get(0);

				if (colObj != null)
				{
					colObj.getBytes();
				}
			}
		}
		finally
		{
			oal.size();
		}
	}
}
