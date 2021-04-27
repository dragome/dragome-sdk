/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.dragome.callbackevictor.enhancers;


/**
 * Adds additional behaviors necessary for stack capture/restore
 * on top of {@link Stack}.
 */
public final class StackRecorder extends Stack {

    private static final long serialVersionUID = 2L;

    private static final ThreadLocal<StackRecorder> threadMap = new ThreadLocal<StackRecorder>();

    /**
     * True, if the continuation restores the previous stack trace to the last
     * invocation of suspend().
     *
     * <p>
     * This field is accessed from the byte code injected into application code,
     * and therefore defining a wrapper get method makes it awkward to
     * step through the user code. That's why this field is public.
     */
    public transient boolean isRestoring = false;

    /**
     * True, is the continuation freeze the strack trace, and stops the
     * continuation.
     *
     * @see #isRestoring
     */
    public transient boolean isCapturing = false;

    /** Context object passed by the client code to continuation during resume */
    private transient Object context;
    /** Result object passed by the continuation to the client code during suspend */
    public transient Object value;

    /**
     * Creates a new empty {@link StackRecorder} that runs the given target.
     */
    public StackRecorder( final Runnable pTarget ) {
        super(pTarget);
    }

    /**
     * Creates a clone of the given {@link StackRecorder}.
     */
    public StackRecorder(final Stack pParent) {
        super(pParent);
    }

    public static Object suspend(final Object value) {
        final StackRecorder stackRecorder = get();
        if(stackRecorder == null) {
            throw new IllegalStateException("No continuation is running");
        }

        stackRecorder.isCapturing = !stackRecorder.isRestoring;
        stackRecorder.isRestoring = false;
        stackRecorder.value = value;

        // flow breaks here, actual return will be executed in resumed continuation
        // return in continuation to be suspended is executed as well but ignored

        return stackRecorder.context;
    }

    public StackRecorder execute(final Object context) {
        final StackRecorder old = registerThread();
        try {
            isRestoring = !isEmpty(); // start restoring if we have a filled stack
            this.context = context;
            
            if (isRestoring) {
            }
            
            getRunnable().run();

            if (isCapturing) {
                if(isEmpty()) {
                    // if we were really capturing the stack, at least we should have
                    // one object in the reference stack. Otherwise, it usually means
                    // that the application wasn't instrumented correctly.
                    throw new IllegalStateException("stack corruption. Is "+getRunnable().getClass()+" instrumented for javaflow?");
                }
                // top of the reference stack is the object that we'll call into
                // when resuming this continuation. we have a separate Runnable
                // for this, so throw it away
                popReference();
                return this;
            } else {
                return null;    // nothing more to continue
            }
        } catch(final ContinuationDeath cd) {
            // this isn't an error, so no need to log
            throw cd;
        } catch(final Error e) {
            throw e;
        } catch(final RuntimeException e) {
            throw e;
        } finally {
            this.context = null;
            deregisterThread(old);
        }
    }

    public Object getContext() {
        return context;
    }

    /**
     * Bind this stack recorder to running thread.
     */
    private StackRecorder registerThread() {
        final StackRecorder old = get();
        threadMap.set(this);
        return old;
    }

    /**
     * Unbind the current stack recorder to running thread.
     */
    private void deregisterThread(final StackRecorder old) {
        threadMap.set(old);
    }

    /**
     * Return the continuation, which is associated to the current thread.
     */
    public static StackRecorder get() {
        return threadMap.get();
    }
}
