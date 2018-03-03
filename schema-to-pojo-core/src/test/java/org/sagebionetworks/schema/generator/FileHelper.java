package org.sagebionetworks.schema.generator;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

public class FileHelper {

	/**
	 * Load static content from a file on the classpath
	 * @param name
	 * @return
	 * @throws IOException
	 */
	public static String loadFileAsStringFromClasspath(ClassLoader loader, String name) throws IOException{
		InputStream in = loader.getResourceAsStream(name);
		if(in == null) throw new IllegalArgumentException("Cannot find: "+name+" on the classpath");
		return readStreamAsString(in);
		
	}

	/**
	 * Read a string from the given stream.
	 * @param in
	 * @return
	 * @throws IOException
	 * @throws UnsupportedEncodingException
	 */
	public static String readStreamAsString(InputStream in) throws IOException,	UnsupportedEncodingException {
		try{
			BufferedInputStream bufferd = new BufferedInputStream(in);
			byte[] buffer = new byte[1024];
			StringBuilder builder = new StringBuilder();
			int index = -1;
			while((index = bufferd.read(buffer, 0, buffer.length)) >0){
				builder.append(new String(buffer, 0, index, "UTF-8"));
			}
			return builder.toString();
		}finally{
			in.close();
		}
	}
	
	/**
	 * Recursively delete all files in a directory and then delete the directory.
	 * @param directory
	 * @return
	 */
	public static boolean recursiveDirectoryDelete(File directory) {
		if (directory == null)
			return false;
		if (!directory.exists())
			return true;
		if (!directory.isDirectory())
			return false;

		String[] list = directory.list();
		if (list != null) {
			for (int i = 0; i < list.length; i++) {
				File entry = new File(directory, list[i]);

				if (entry.isDirectory()) {
					if (!recursiveDirectoryDelete(entry))
						return false;
				} else {
					if (!entry.delete())
						return false;
				}
			}
		}
		return directory.delete();
	}
}
