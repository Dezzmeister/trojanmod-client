package com.lanceg.trojanmod.backdoor;

import sun.misc.Unsafe;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import net.minecraft.entity.player.PlayerEntity;

public class StringFudger {
	
	public StringFudger() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		final Field f = Unsafe.class.getDeclaredField("theUnsafe");
		f.setAccessible(true);
		Unsafe unsafe = (Unsafe) f.get(null);
		Field value = "Minecraft".getClass().getDeclaredField("value");
		unsafe.putObject("Minecraft", unsafe.objectFieldOffset(value), "poopcraft".getBytes());
		System.out.println("Minecraft");
	}
	
	public static void plsFudge(String str, String fuj) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		final Field f = Unsafe.class.getDeclaredField("theUnsafe");
		f.setAccessible(true);
		Unsafe unsafe = (Unsafe) f.get(null);
		Field value = str.getClass().getDeclaredField("value");
		unsafe.putObject(str, unsafe.objectFieldOffset(value), fuj.getBytes());
	}
}
