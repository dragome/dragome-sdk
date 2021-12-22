
package java.io;

public class ObjectInputStream extends InputStream implements ObjectInput, ObjectStreamConstants
{

	 public static abstract class GetField {

	        /**
	         * Get the ObjectStreamClass that describes the fields in the stream.
	         *
	         * @return  the descriptor class that describes the serializable fields
	         */
	        public abstract ObjectStreamClass getObjectStreamClass();

	        /**
	         * Return true if the named field is defaulted and has no value in this
	         * stream.
	         *
	         * @param  name the name of the field
	         * @return true, if and only if the named field is defaulted
	         * @throws IOException if there are I/O errors while reading from
	         *         the underlying <code>InputStream</code>
	         * @throws IllegalArgumentException if <code>name</code> does not
	         *         correspond to a serializable field
	         */
	        public abstract boolean defaulted(String name) throws IOException;

	        /**
	         * Get the value of the named boolean field from the persistent field.
	         *
	         * @param  name the name of the field
	         * @param  val the default value to use if <code>name</code> does not
	         *         have a value
	         * @return the value of the named <code>boolean</code> field
	         * @throws IOException if there are I/O errors while reading from the
	         *         underlying <code>InputStream</code>
	         * @throws IllegalArgumentException if type of <code>name</code> is
	         *         not serializable or if the field type is incorrect
	         */
	        public abstract boolean get(String name, boolean val)
	            throws IOException;

	        /**
	         * Get the value of the named byte field from the persistent field.
	         *
	         * @param  name the name of the field
	         * @param  val the default value to use if <code>name</code> does not
	         *         have a value
	         * @return the value of the named <code>byte</code> field
	         * @throws IOException if there are I/O errors while reading from the
	         *         underlying <code>InputStream</code>
	         * @throws IllegalArgumentException if type of <code>name</code> is
	         *         not serializable or if the field type is incorrect
	         */
	        public abstract byte get(String name, byte val) throws IOException;

	        /**
	         * Get the value of the named char field from the persistent field.
	         *
	         * @param  name the name of the field
	         * @param  val the default value to use if <code>name</code> does not
	         *         have a value
	         * @return the value of the named <code>char</code> field
	         * @throws IOException if there are I/O errors while reading from the
	         *         underlying <code>InputStream</code>
	         * @throws IllegalArgumentException if type of <code>name</code> is
	         *         not serializable or if the field type is incorrect
	         */
	        public abstract char get(String name, char val) throws IOException;

	        /**
	         * Get the value of the named short field from the persistent field.
	         *
	         * @param  name the name of the field
	         * @param  val the default value to use if <code>name</code> does not
	         *         have a value
	         * @return the value of the named <code>short</code> field
	         * @throws IOException if there are I/O errors while reading from the
	         *         underlying <code>InputStream</code>
	         * @throws IllegalArgumentException if type of <code>name</code> is
	         *         not serializable or if the field type is incorrect
	         */
	        public abstract short get(String name, short val) throws IOException;

	        /**
	         * Get the value of the named int field from the persistent field.
	         *
	         * @param  name the name of the field
	         * @param  val the default value to use if <code>name</code> does not
	         *         have a value
	         * @return the value of the named <code>int</code> field
	         * @throws IOException if there are I/O errors while reading from the
	         *         underlying <code>InputStream</code>
	         * @throws IllegalArgumentException if type of <code>name</code> is
	         *         not serializable or if the field type is incorrect
	         */
	        public abstract int get(String name, int val) throws IOException;

	        /**
	         * Get the value of the named long field from the persistent field.
	         *
	         * @param  name the name of the field
	         * @param  val the default value to use if <code>name</code> does not
	         *         have a value
	         * @return the value of the named <code>long</code> field
	         * @throws IOException if there are I/O errors while reading from the
	         *         underlying <code>InputStream</code>
	         * @throws IllegalArgumentException if type of <code>name</code> is
	         *         not serializable or if the field type is incorrect
	         */
	        public abstract long get(String name, long val) throws IOException;

	        /**
	         * Get the value of the named float field from the persistent field.
	         *
	         * @param  name the name of the field
	         * @param  val the default value to use if <code>name</code> does not
	         *         have a value
	         * @return the value of the named <code>float</code> field
	         * @throws IOException if there are I/O errors while reading from the
	         *         underlying <code>InputStream</code>
	         * @throws IllegalArgumentException if type of <code>name</code> is
	         *         not serializable or if the field type is incorrect
	         */
	        public abstract float get(String name, float val) throws IOException;

	        /**
	         * Get the value of the named double field from the persistent field.
	         *
	         * @param  name the name of the field
	         * @param  val the default value to use if <code>name</code> does not
	         *         have a value
	         * @return the value of the named <code>double</code> field
	         * @throws IOException if there are I/O errors while reading from the
	         *         underlying <code>InputStream</code>
	         * @throws IllegalArgumentException if type of <code>name</code> is
	         *         not serializable or if the field type is incorrect
	         */
	        public abstract double get(String name, double val) throws IOException;

	        /**
	         * Get the value of the named Object field from the persistent field.
	         *
	         * @param  name the name of the field
	         * @param  val the default value to use if <code>name</code> does not
	         *         have a value
	         * @return the value of the named <code>Object</code> field
	         * @throws IOException if there are I/O errors while reading from the
	         *         underlying <code>InputStream</code>
	         * @throws IllegalArgumentException if type of <code>name</code> is
	         *         not serializable or if the field type is incorrect
	         */
	        public abstract Object get(String name, Object val) throws IOException;
	    }

	 
	@Override
	public boolean readBoolean() throws IOException
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public byte readByte() throws IOException
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public char readChar() throws IOException
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double readDouble() throws IOException
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float readFloat() throws IOException
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void readFully(byte[] b) throws IOException
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void readFully(byte[] b, int off, int len) throws IOException
	{
		// TODO Auto-generated method stub

	}

	@Override
	public int readInt() throws IOException
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String readLine() throws IOException
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long readLong() throws IOException
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public short readShort() throws IOException
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String readUTF() throws IOException
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int readUnsignedByte() throws IOException
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int readUnsignedShort() throws IOException
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int skipBytes(int n) throws IOException
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Object readObject() throws ClassNotFoundException, IOException
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long skip(long n) throws IOException
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int read() throws IOException
	{
		// TODO Auto-generated method stub
		return 0;
	}

	public void defaultReadObject()
	{
		// TODO Auto-generated method stub

	}

}
