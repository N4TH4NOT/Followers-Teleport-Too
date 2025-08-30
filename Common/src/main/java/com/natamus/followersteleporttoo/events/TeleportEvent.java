package com.natamus.followersteleporttoo.events;

import com.natamus.followersteleporttoo.config.ConfigHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Leashable;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class TeleportEvent {
    private static final HashMap<UUID, Date> teleportedfollowers = new HashMap<>();

    public static boolean onPlayerTeleport(Level world, Entity entity, double targetX, double targetY, double targetZ) {
        if (world.isClientSide) {
            return true;
        }

        if (!(entity instanceof Player)) {
            return true;
        }

        ServerPlayer player = (ServerPlayer) entity;
        BlockPos ppos = player.blockPosition();
        Vec3 targetvec = new Vec3(targetX, targetY, targetZ);

        List<Leashable> leashed = Leashable.leashableLeashedTo(entity);
        if (!leashed.isEmpty()) {
            for (Leashable ea : leashed) {
                if (ea instanceof Entity target) {
                    target.teleportTo(targetvec.x, targetvec.y, targetvec.z);

                    if (ConfigHandler.disableFollowerDamageAfterTeleport) {
                        teleportedfollowers.put(target.getUUID(), new Date());
                    }
                }
            }
        }

        for (Entity ea : world.getEntities(null, new AABB(ppos.getX() - 50, ppos.getY() - 50, ppos.getZ() - 50, ppos.getX() + 50, ppos.getY() + 50, ppos.getZ() + 50))) {
            if (ea instanceof TamableAnimal ta && ta.isTame() && !ta.isInSittingPose() && ta.isOwnedBy(player)) {
                ta.teleportTo(targetvec.x, targetvec.y, targetvec.z);

                if (ConfigHandler.disableFollowerDamageAfterTeleport) {
                    teleportedfollowers.put(ta.getUUID(), new Date());
                }
            }
        }

        if (player.getVehicle() != null) {
            getVehicleFrom(entity).teleportTo(targetvec.x, targetvec.y, targetvec.z);
            return false;
        }
        else return true;
    }

    public static float onFollowerDamage(Level level, Entity entity, DamageSource damageSource, float damageAmount) {
        if (!ConfigHandler.disableFollowerDamageAfterTeleport) {
            return damageAmount;
        }

        if (teleportedfollowers.size() > 0) {
            if (!(entity instanceof TamableAnimal)) {
                return damageAmount;
            }

            UUID uuid = entity.getUUID();
            if (teleportedfollowers.containsKey(uuid)) {
                Date lastteleported = teleportedfollowers.get(uuid);

                long ms = ((new Date()).getTime()-lastteleported.getTime());
                if (ms > ConfigHandler.durationInSecondsDamageShouldBeDisabled * 1000L) {
                    teleportedfollowers.remove(uuid);
                    return damageAmount;
                }

                return 0F;
            }
        }

        return damageAmount;
    }

    public static Entity getVehicleFrom(Entity entity) {
        Entity target = entity;
        while (target.getVehicle() != null) {
            target = target.getVehicle();
        }
        return target;
    }
}