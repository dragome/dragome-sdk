/*
 * Copyright 2009 Andrew Pietsch 
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you 
 * may not use this file except in compliance with the License. You may 
 * obtain a copy of the License at 
 *      
 *      http://www.apache.org/licenses/LICENSE-2.0 
 *
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or 
 * implied. See the License for the specific language governing permissions 
 * and limitations under the License. 
 */

package com.dragome.forms.bindings.client.form.validation;

import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;

import com.dragome.model.interfaces.IndexedValidationResult;
import com.dragome.model.interfaces.IndexedValidationResultCollector;
import com.dragome.model.interfaces.Severity;
import com.dragome.model.interfaces.ValidationMessage;
import com.dragome.model.interfaces.ValidationResult;
import com.dragome.services.interfaces.ValidationResultCollector;

/**
 *
 */
public class IndexedValidationResultImpl implements IndexedValidationResult, IndexedValidationResultCollector
{
	private static final int UNINDEXED_MESSAGE_INDEX= -1;
	private ValidationResultImpl allMessages= new ValidationResultImpl();
	private TreeMap<Integer, ValidationResult> results= new TreeMap<Integer, ValidationResult>();

	public IndexedValidationResultImpl()
	{
	}

	public void add(ValidationMessage message)
	{
		allMessages.add(message);
		prepareIndexedResult(UNINDEXED_MESSAGE_INDEX).add(message);
	}

	public void add(int index, ValidationMessage message)
	{
		allMessages.add(message);
		prepareIndexedResult(index).add(message);
	}

	public ValidationResultCollector getIndexedCollector(final int index)
	{
		return new PreIndexedValidationResultCollector(index);
	}

	public boolean isEmpty()
	{
		return allMessages.isEmpty();
	}

	public List<ValidationMessage> getMessages()
	{
		return allMessages.getMessages();
	}

	public List<ValidationMessage> getMessages(Severity severity)
	{
		return allMessages.getMessages(severity);
	}

	public boolean contains(Severity severity)
	{
		return allMessages.contains(severity);
	}

	public SortedSet<Severity> getSeverities()
	{
		return allMessages.getSeverities();
	}

	public Set<Integer> getResultIndicies()
	{
		Set<Integer> results= this.results.keySet();
		results.remove(UNINDEXED_MESSAGE_INDEX);
		return results;
	}

	public ValidationResult getUnindexedResult()
	{
		return getIndexedResult(UNINDEXED_MESSAGE_INDEX);
	}

	public ValidationResult getIndexedResult(int index)
	{
		ValidationResultImpl result= (ValidationResultImpl) results.get(index);
		return result != null ? result : EmptyValidationResult.INSTANCE;
	}

	public int size()
	{
		return results.size();
	}

	protected ValidationResultImpl prepareIndexedResult(int index)
	{
		ValidationResultImpl result= (ValidationResultImpl) results.get(index);
		if (result == null)
		{
			result= new ValidationResultImpl();
			results.put(index, result);
		}

		return result;
	}

	private class PreIndexedValidationResultCollector implements ValidationResultCollector
	{
		private final int index;

		public PreIndexedValidationResultCollector(int index)
		{
			this.index= index;
		}

		public void add(ValidationMessage message)
		{
			IndexedValidationResultImpl.this.add(index, message);
		}

		public boolean contains(Severity severity)
		{
			return getIndexedResult(index).contains(severity);
		}
	}
}