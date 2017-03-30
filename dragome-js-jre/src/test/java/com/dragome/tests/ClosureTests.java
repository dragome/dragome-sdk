package com.dragome.tests;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;

import junit.framework.TestCase;

@RunWith(DragomeTestRunner.class)
public class ClosureTests extends TestCase
{

	//////////////////////////////////////////////////////////////////////////////////
	
	public static interface Supplier<T> {
		public T get();
	}

	//////////////////////////////////////////////////////////////////////////////////
	
	public static class ObjectPool<T> {
		private final Supplier<T> supplier;

		private ObjectPool(final Supplier<T> supplier) {
			this.supplier = supplier;
		}

		public T get() {
			return supplier.get();
		}

		public static <T> ObjectPool<T> create(final Supplier<T> supplier) {
			return new ObjectPool<>(supplier);
		}

	}

	//////////////////////////////////////////////////////////////////////////////////

	public static abstract class NodeOverlapCallback {
		public abstract void processNode(int newVal);
	}

	//////////////////////////////////////////////////////////////////////////////////

	public static class MyNodeOverlapCallback extends NodeOverlapCallback {
		
		public int val = 10;
		
	    @Override
	    public void processNode(int newVal) {
	    	val = newVal;
	    }
	}

	//////////////////////////////////////////////////////////////////////////////////

	@Test
	public void testWithoutUseLamdaClosure()
	{
		final ObjectPool<MyNodeOverlapCallback> myNodeCallbacks = ObjectPool.create(
		    new Supplier<MyNodeOverlapCallback>() {
		        @Override
		        public MyNodeOverlapCallback get() {
		            return new MyNodeOverlapCallback();
		        }
		    }
		);
		    
		final MyNodeOverlapCallback myNodeCallback = myNodeCallbacks.get();
		assertNotNull(myNodeCallback);
		assertEquals(myNodeCallback.val, 10);
		myNodeCallback.processNode(15);
		assertEquals(myNodeCallback.val, 15);
	}

	//////////////////////////////////////////////////////////////////////////////////

	@Test
	public void testWithUseLamdaClosure()
	{
		final ObjectPool<MyNodeOverlapCallback> myNodeCallbacks = ObjectPool.create( 
		    () -> new MyNodeOverlapCallback()
		);

		final MyNodeOverlapCallback myNodeCallback = myNodeCallbacks.get();
		assertNotNull(myNodeCallback);
		assertEquals(myNodeCallback.val, 10);
		myNodeCallback.processNode(25);
		assertEquals(myNodeCallback.val, 25);
	}
	
	//////////////////////////////////////////////////////////////////////////////////

}
