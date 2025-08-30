package com.natamus.followersteleporttoo.neoforge.events;

import com.natamus.followersteleporttoo.events.TeleportEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.EntityTeleportEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

public class NeoForgeTeleportEvent {
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void onPlayerTeleport(EntityTeleportEvent.TeleportCommand e) {
		if (e.isCanceled()) {
			return;
		}

		Entity entity = e.getEntity();
		if (!TeleportEvent.onPlayerTeleport(entity.level(), entity, e.getTargetX(), e.getTargetY(), e.getTargetZ())) {
            e.setCanceled(true);
        }
	}

	@SubscribeEvent
	public static void onFollowerDamage(LivingIncomingDamageEvent e) {
		LivingEntity livingEntity = e.getEntity();

		if (TeleportEvent.onFollowerDamage(livingEntity.level(), livingEntity, e.getSource(), e.getAmount()) == 0F) {
			e.setAmount(0F);
			e.setCanceled(true);
		}
	}
}
