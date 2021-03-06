// Generated by esidl 0.4.0.

package org.w3c.dom.typedarray;

import org.w3c.dom.ByteArray;

public interface Int8Array extends ArrayBufferView
{
    // Int8Array
    public static final int BYTES_PER_ELEMENT = 1;
    public int getLength();
    public byte get(int index);
    public void set(int index, byte value);
    public void set(Int8Array array);
    public void set(Int8Array array, int offset);
    public void set(ByteArray array);
    public void set(ByteArray array, int offset);
    public Int8Array subarray(int start, int end);
}
