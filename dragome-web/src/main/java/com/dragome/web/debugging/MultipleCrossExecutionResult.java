package com.dragome.web.debugging;

import java.util.List;

import com.dragome.web.debugging.interfaces.CrossExecutionResult;

public class MultipleCrossExecutionResult
{
	private List<CrossExecutionResult> results;
	
	public MultipleCrossExecutionResult()
	{
	}

	public MultipleCrossExecutionResult(List<CrossExecutionResult> list)
	{
		this.setResults(list);
	}

	public List<CrossExecutionResult> getResults()
	{
		return results;
	}

	public void setResults(List<CrossExecutionResult> results)
	{
		this.results = results;
	}
}
