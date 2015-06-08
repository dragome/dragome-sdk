package com.dragome.compiler.writer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;

import net.jpountz.lz4.LZ4Compressor;
import net.jpountz.lz4.LZ4Factory;

public class DeflateEncoder
{
	private static void writeIntLE(int i, OutputStream outputStream) throws IOException
	{
		outputStream.write((byte) i);
		outputStream.write((byte) (i >>> 8));
		outputStream.write((byte) (i >>> 16));
		outputStream.write((byte) (i >>> 24));
	}

	public static void encode(File file, File outputFile)
	{
		try
		{
			byte[] data= Files.readAllBytes(file.toPath());

			LZ4Factory factory= LZ4Factory.safeInstance();

			LZ4Compressor compressor= factory.highCompressor(9);
			FileOutputStream fos= new FileOutputStream(outputFile);

			byte[] result= compressor.compress(data);

			writeIntLE(data.length, fos);
			fos.write(result);
			fos.close();
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}
}
