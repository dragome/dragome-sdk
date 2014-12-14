/*******************************************************************************
 * Copyright (c) 2011-2014 Fernando Petrola
 * 
 * This file is part of Dragome SDK.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package com.dragome.helpers;

import java.sql.ResultSet;
import java.sql.SQLException;

public class NullResultSetProcessorExecutor implements ResultSetProcessorExecutor
{
	public void process(ResultSet resultSet, ResultSetProcessor resultSetProcessor)
	{
		try
		{
			resultSetProcessor.process(resultSet);
		}
		catch (SQLException e)
		{
			throw new RuntimeException(e);
		}
	}
}
