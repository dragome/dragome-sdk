/*
 * Copyright (c) 2011-2014 Fernando Petrola
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.dragome.web.helpers.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.dragome.commons.javascript.ScriptHelper;
import com.dragome.helpers.jdbc.ResultSetProcessor;
import com.dragome.helpers.jdbc.ResultSetProcessorExecutor;

public class BrowserResultSetProcessorExecutor implements ResultSetProcessorExecutor
{
	public BrowserResultSetProcessorExecutor()
	{
	}

	public void process(ResultSet resultSet, ResultSetProcessor resultSetProcessor)
	{
		try
		{
			ScriptHelper.eval("window.rsProcessor= function (resultset){", this);
			ScriptHelper.eval("window.results=resultset;", this);
			resultSetProcessor.process(resultSet);
			ScriptHelper.eval("};", this);
		}
		catch (SQLException e)
		{
			throw new RuntimeException(e);
		}
	}

}
