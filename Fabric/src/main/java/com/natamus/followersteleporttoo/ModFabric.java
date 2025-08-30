package com.natamus.followersteleporttoo;

import com.natamus.collective.check.RegisterMod;
import com.natamus.collective.check.ShouldLoadCheck;
import com.natamus.collective.events.CollectiveEvents;
import com.natamus.collective.fabric.callbacks.CollectiveEntityEvents;
import com.natamus.followersteleporttoo.events.TeleportEvent;
import com.natamus.followersteleporttoo.util.Reference;
import net.fabricmc.api.ModInitializer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

public class ModFabric implements ModInitializer {
	
	@Override
	public void onInitialize() {
		if (!ShouldLoadCheck.shouldLoad(Reference.MOD_ID)) {
			return;
		}

		setGlobalConstants();
		ModCommon.init();

		loadEvents();

		RegisterMod.register(Reference.NAME, Reference.MOD_ID, Reference.VERSION, Reference.ACCEPTED_VERSIONS);
	}

	private void loadEvents() {
		CollectiveEntityEvents.ON_ENTITY_TELEPORT_COMMAND.register(TeleportEvent::onPlayerTeleport);
		CollectiveEntityEvents.ON_LIVING_DAMAGE_CALC.register(TeleportEvent::onFollowerDamage);
	}

	private static void setGlobalConstants() {

	}
}
