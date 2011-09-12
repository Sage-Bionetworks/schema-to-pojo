package org.sagebionetworks.schema.util;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileUtil {

	/**
	 * Load a string from a file on the classpath.
	 * 
	 * @param name
	 * @return
	 * @throws IOException
	 */
	public static String loadStringFromClasspathFile(ClassLoader loader, String name) throws IOException {
		InputStream in = loader.getResourceAsStream(name);
		if (in == null)
			throw new IllegalArgumentException("Cannot find: " + name
					+ " on the classpath");
		try {
			BufferedInputStream bufferd = new BufferedInputStream(in);
			byte[] buffer = new byte[1024];
			StringBuilder builder = new StringBuilder();
			int index = -1;
			while ((index = bufferd.read(buffer, 0, buffer.length)) > 0) {
				builder.append(new String(buffer, 0, index, "UTF-8"));
			}
			return builder.toString();
		} finally {
			in.close();
		}
	}
}
