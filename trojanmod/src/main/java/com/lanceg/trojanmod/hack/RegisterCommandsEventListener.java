package com.lanceg.trojanmod.hack;

import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class RegisterCommandsEventListener {
	
	@SubscribeEvent
	public void registerCommands(RegisterCommandsEvent event) {
		AlterSpeedCommand.register(event.getDispatcher());
	}
}
