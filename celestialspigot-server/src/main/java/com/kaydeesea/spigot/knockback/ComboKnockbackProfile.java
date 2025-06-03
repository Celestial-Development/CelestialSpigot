package com.kaydeesea.spigot.knockback;

import net.minecraft.server.*;
import org.bukkit.event.player.PlayerVelocityEvent;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public interface ComboKnockbackProfile extends KnockBackProfile {

    // Constants — you can make them configurable
    long getComboResetMS();

    // These could be configured from file or overridden by implementation
    double getBaseHorizontal();
    double getHorizontalScalePerHit();
    double getBaseVertical();
    double getVerticalScalePerHit();
    double getMaxHorizontal();
    double getMaxVertical();

    // Internal tracking
    Map<UUID, Integer> comboCountMap = new HashMap<>();
    Map<UUID, Long> lastHitTimeMap = new HashMap<>();

    default int getComboCount(UUID victimId, UUID attackerId) {
        UUID key = generateKey(victimId, attackerId);
        long lastHit = lastHitTimeMap.getOrDefault(key, 0L);
        long now = System.currentTimeMillis();

        if ((now - lastHit) > getComboResetMS()) {
            comboCountMap.put(key, 1);
        } else {
            comboCountMap.put(key, comboCountMap.getOrDefault(key, 0) + 1);
        }
        lastHitTimeMap.put(key, now);
        return comboCountMap.get(key);
    }

    default UUID generateKey(UUID victimId, UUID attackerId) {
        // You can make this a Pair or just XOR the UUIDs for uniqueness
        return UUID.nameUUIDFromBytes((victimId.toString() + attackerId.toString()).getBytes());
    }

    default void handleEntityLiving(EntityLiving victim, double d0, double d1, DamageSource source) {
        victim.ai = true;

        if (source == null || source.getEntity() == null) return;

        Entity attacker = source.getEntity();
        if (!(attacker instanceof EntityHuman)) return;

        UUID victimId = victim.getBukkitEntity().getUniqueId();
        UUID attackerId = attacker.getBukkitEntity().getUniqueId();


        int combo = getComboCount(victimId, attackerId);

        double horizontal = Math.min(getBaseHorizontal() + getHorizontalScalePerHit() * combo, getMaxHorizontal());
        double vertical = Math.min(getBaseVertical() + getVerticalScalePerHit() * combo, getMaxVertical());

        Vector direction = new Vector(d0, 0, d1).normalize().multiply(horizontal);
        victim.motX = direction.getX();
        victim.motZ = direction.getZ();

        victim.motY = 0;
        victim.motY += vertical;

        victim.velocityChanged = true;
    }

    default void handleEntityHuman(EntityHuman attacker, EntityPlayer player, int i, Vector vector) {
        if (player != null) {
            player.motX = vector.getX();
            player.motY = vector.getY();
            player.motZ = vector.getZ();
            player.velocityChanged = true;

            PlayerVelocityEvent event = new PlayerVelocityEvent(player.getBukkitEntity(), vector);
            attacker.world.getServer().getPluginManager().callEvent(event);
            if (!event.isCancelled()) {
                player.getBukkitEntity().setVelocityDirect(event.getVelocity());
            }
        }
    }
}
