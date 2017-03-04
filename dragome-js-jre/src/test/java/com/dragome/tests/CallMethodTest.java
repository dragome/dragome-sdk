package com.dragome.tests;

import java.util.AbstractList;
import java.util.RandomAccess;

import org.junit.Test;
import org.junit.runner.RunWith;

import junit.framework.TestCase;

@RunWith(DragomeTestRunner.class)
public class CallMethodTest extends TestCase {

    private static final class ObjectArrayList<T> extends AbstractList<T> implements RandomAccess {

        private int size = 10;
        
        @Override
        public T get(int index) {
        	assertTrue(index < 10);
            return null;
        }

        @Override
        public int size() {
            return size;
        }
         
    }
    
    public static class CollisionWorld {
        protected final ObjectArrayList<Integer> oal = new ObjectArrayList<>();
    }
    
    public static abstract class DynamicsWorld extends CollisionWorld {
        // NOTHING
    }
    
    public static class DiscreteDynamicsWorld extends DynamicsWorld {
        public void predictUnconstraintMotion() {
            try {
                for (int i = 0; i < oal.size(); i++) {
                    final Integer colObj = oal.get(i);
                    if (colObj != null) {
                        int temp = colObj;
                        temp++;
                    }
                }
            } finally {
                oal.size();
            }
        }
    }

	@Test
	public void testSimpleForLoop() throws Exception
	{
        int j = 0;
        for (int i = 0; i < 10; i++) {
            j++;
        }
        assertTrue(j == 10);
	}
	
	@Test
	public void testDifficultForLoop() throws Exception
	{
        final DiscreteDynamicsWorld ddw = new DiscreteDynamicsWorld();
        ddw.predictUnconstraintMotion();
	}
	
}
