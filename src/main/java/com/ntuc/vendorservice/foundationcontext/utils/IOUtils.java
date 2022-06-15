package com.ntuc.vendorservice.foundationcontext.utils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

/**
 * Stream manipulation
 * @author I305675
 *
 */
public class IOUtils {
	public static final int BUFFER_SIZE = 1024;

	/**
	 * This method extracts a byte array out of an {@link InputStream InputStream}.
	 *
	 *  @param inputStream - the stream we want to extract a byte[] from.
	 *  @return the inputstream's content as a byte array.
	 * */
	public static byte[] toByteArray(InputStream inputStream)
	        throws IOException {

	    ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    byte[] buffer = new byte[BUFFER_SIZE];
	    int length = 0;
	    while ((length = inputStream.read(buffer)) != -1) {
	        baos.write(buffer, 0, length);
	    }
	    return baos.toByteArray();
	}

	/**
	 * This method copies the content of an {@link InputStream InputStream}
	 * into an {@link OutputStream OutputStream}.
	 *
	 * @param in - the InputStream that the method reads from.
	 * @param out - the OutputStream that the method writes into.
	 * */
	public static void writeToOutputStream(InputStream in, OutputStream out)
			throws IOException {

		byte[] buf = new byte[BUFFER_SIZE];
        int len;
        while ((len = in.read(buf)) > 0){
          out.write(buf, 0, len);
        }
	}
	/**
	 *
	 * This method converts the content of an {@link InputStream InputStream}
	 * into an {@link String}. 
	 * 
	 * @param content the InputStream that the method reads from.
	 * @return the string read
	 * @throws IOException
	 */
	public static String getInputFromStream(InputStream content) throws IOException {
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(content));
		String line;
		StringBuilder builder = new StringBuilder();
		while ((line = bufferedReader.readLine()) != null) {
			builder.append(line);
		}
		return builder.toString();
	}
}
