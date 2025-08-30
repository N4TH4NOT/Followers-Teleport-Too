package com.natamus.followersteleporttoo.forge.events;

import com.natamus.followersteleporttoo.events.TeleportEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.EntityTeleportEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.listener.Priority;
import net.minecraftforge.eventbus.api.bus.BusGroup;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;

import java.lang.invoke.MethodHandles;

public class ForgeTeleportEvent {
	public static void registerEventsInBus() {
		BusGroup.DEFAULT.register(MethodHandles.lookup(), ForgeTeleportEvent.class);
	}

	@SubscribeEvent(priority = Priority.MONITOR)
	public static void onPlayerTeleport(EntityTeleportEvent.TeleportCommand e, boolean wasCancelled) {
		if (wasCancelled) {
			return;
		}

		Entity entity = e.getEntity();
		if (!TeleportEvent.onPlayerTeleport(entity.level(), entity, e.getTargetX(), e.getTargetY(), e.getTargetZ())) {
            e.setTargetX(e.getPrevX());
            e.setTargetY(e.getPrevY());
            e.setTargetZ(e.getPrevZ());
        }
	}

	@SubscribeEvent
	public static boolean onFollowerDamage(LivingHurtEvent e) {
		LivingEntity livingEntity = e.getEntity();

		if (TeleportEvent.onFollowerDamage(livingEntity.level(), livingEntity, e.getSource(), e.getAmount()) == 0F) {
			e.setAmount(0F);
			return true;
		}
		return false;
	}
}
