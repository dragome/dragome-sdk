
package java.io;

import java.io.ObjectStreamClass.WeakClassKey;
import java.lang.ref.ReferenceQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ObjectOutputStream extends OutputStream implements ObjectOutput, ObjectStreamConstants
{

	private static class Caches
	{
		/** cache of subclass security audit results */
		static final ConcurrentMap<WeakClassKey, Boolean> subclassAudits= new ConcurrentHashMap<>();

		/** queue for WeakReferences to audited subclasses */
		static final ReferenceQueue<Class<?>> subclassAuditsQueue= new ReferenceQueue<>();
	}

	/**
	 * Provide programmatic access to the persistent fields to be written
	 * to ObjectOutput.
	 *
	 * @since 1.2
	 */
	public static abstract class PutField
	{

		/**
		 * Put the value of the named boolean field into the persistent field.
		 *
		 * @param  name the name of the serializable field
		 * @param  val the value to assign to the field
		 * @throws IllegalArgumentException if <code>name</code> does not
		 * match the name of a serializable field for the class whose fields
		 * are being written, or if the type of the named field is not
		 * <code>boolean</code>
		 */
		public abstract void put(String name, boolean val);

		/**
		 * Put the value of the named byte field into the persistent field.
		 *
		 * @param  name the name of the serializable field
		 * @param  val the value to assign to the field
		 * @throws IllegalArgumentException if <code>name</code> does not
		 * match the name of a serializable field for the class whose fields
		 * are being written, or if the type of the named field is not
		 * <code>byte</code>
		 */
		public abstract void put(String name, byte val);

		/**
		 * Put the value of the named char field into the persistent field.
		 *
		 * @param  name the name of the serializable field
		 * @param  val the value to assign to the field
		 * @throws IllegalArgumentException if <code>name</code> does not
		 * match the name of a serializable field for the class whose fields
		 * are being written, or if the type of the named field is not
		 * <code>char</code>
		 */
		public abstract void put(String name, char val);

		/**
		 * Put the value of the named short field into the persistent field.
		 *
		 * @param  name the name of the serializable field
		 * @param  val the value to assign to the field
		 * @throws IllegalArgumentException if <code>name</code> does not
		 * match the name of a serializable field for the class whose fields
		 * are being written, or if the type of the named field is not
		 * <code>short</code>
		 */
		public abstract void put(String name, short val);

		/**
		 * Put the value of the named int field into the persistent field.
		 *
		 * @param  name the name of the serializable field
		 * @param  val the value to assign to the field
		 * @throws IllegalArgumentException if <code>name</code> does not
		 * match the name of a serializable field for the class whose fields
		 * are being written, or if the type of the named field is not
		 * <code>int</code>
		 */
		public abstract void put(String name, int val);

		/**
		 * Put the value of the named long field into the persistent field.
		 *
		 * @param  name the name of the serializable field
		 * @param  val the value to assign to the field
		 * @throws IllegalArgumentException if <code>name</code> does not
		 * match the name of a serializable field for the class whose fields
		 * are being written, or if the type of the named field is not
		 * <code>long</code>
		 */
		public abstract void put(String name, long val);

		/**
		 * Put the value of the named float field into the persistent field.
		 *
		 * @param  name the name of the serializable field
		 * @param  val the value to assign to the field
		 * @throws IllegalArgumentException if <code>name</code> does not
		 * match the name of a serializable field for the class whose fields
		 * are being written, or if the type of the named field is not
		 * <code>float</code>
		 */
		public abstract void put(String name, float val);

		/**
		 * Put the value of the named double field into the persistent field.
		 *
		 * @param  name the name of the serializable field
		 * @param  val the value to assign to the field
		 * @throws IllegalArgumentException if <code>name</code> does not
		 * match the name of a serializable field for the class whose fields
		 * are being written, or if the type of the named field is not
		 * <code>double</code>
		 */
		public abstract void put(String name, double val);

		/**
		 * Put the value of the named Object field into the persistent field.
		 *
		 * @param  name the name of the serializable field
		 * @param  val the value to assign to the field
		 *         (which may be <code>null</code>)
		 * @throws IllegalArgumentException if <code>name</code> does not
		 * match the name of a serializable field for the class whose fields
		 * are being written, or if the type of the named field is not a
		 * reference type
		 */
		public abstract void put(String name, Object val);

		/**
		 * Write the data and fields to the specified ObjectOutput stream,
		 * which must be the same stream that produced this
		 * <code>PutField</code> object.
		 *
		 * @param  out the stream to write the data and fields to
		 * @throws IOException if I/O errors occur while writing to the
		 *         underlying stream
		 * @throws IllegalArgumentException if the specified stream is not
		 *         the same stream that produced this <code>PutField</code>
		 *         object
		 * @deprecated This method does not write the values contained by this
		 *         <code>PutField</code> object in a proper format, and may
		 *         result in corruption of the serialization stream.  The
		 *         correct way to write <code>PutField</code> data is by
		 *         calling the {@link java.io.ObjectOutputStream#writeFields()}
		 *         method.
		 */
		@Deprecated
		public abstract void write(ObjectOutput out) throws IOException;
	}

	@Override
	public void writeBoolean(boolean v) throws IOException
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void writeByte(int v) throws IOException
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void writeBytes(String s) throws IOException
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void writeChar(int v) throws IOException
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void writeChars(String s) throws IOException
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void writeDouble(double v) throws IOException
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void writeFloat(float v) throws IOException
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void writeInt(int v) throws IOException
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void writeLong(long v) throws IOException
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void writeShort(int v) throws IOException
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void writeUTF(String s) throws IOException
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void writeObject(Object obj) throws IOException
	{
		// TODO Auto-generated method stub
		
	}

}
