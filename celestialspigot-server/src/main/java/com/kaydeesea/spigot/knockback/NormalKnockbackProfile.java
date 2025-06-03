package com.kaydeesea.spigot.knockback;

import net.minecraft.server.*;
import org.bukkit.Bukkit;
import org.bukkit.event.player.PlayerVelocityEvent;
import org.bukkit.util.Vector;

public interface NormalKnockbackProfile extends KnockBackProfile {
    double getFriction();
    double getHorizontal();
    double getVertical();
    double getVerticalLimit();
    double getExtraHorizontal();
    double getExtraVertical();


    default void handleEntityLiving(EntityLiving entityLiving, double d0, double d1, DamageSource source) {
        // cSpigot start
        entityLiving.ai = true;
        double magnitude = MathHelper.sqrt(d0 * d0 + d1 * d1);

        entityLiving.motX /= getFriction();
        entityLiving.motY /= getFriction();
        entityLiving.motZ /= getFriction();

        entityLiving.motX -= d0 / magnitude * getHorizontal();
        entityLiving.motY += getVertical();
        entityLiving.motZ -= d1 / magnitude * getHorizontal();

        if (entityLiving.motY > getVerticalLimit()) {
            entityLiving.motY = getVerticalLimit();
        }
        // cSpigot end
    };

    default void handleEntityHuman(EntityHuman attacker, EntityPlayer player, int i, Vector vector) {
        if (i > 0) {
            player.g(
                    (double) (-MathHelper.sin(attacker.yaw * 3.1415926F / 180.0F) * (float) i) * getExtraHorizontal(),
                    getExtraVertical(),
                    (double) (MathHelper.cos(attacker.yaw * 3.1415926F / 180.0F) * (float) i) * getExtraHorizontal());
        }
        attacker.motX *= 0.6;
        attacker.motZ *= 0.6;
        attacker.setSprinting(false);

        if (player != null && player.velocityChanged) {
            //Send and check PlayerVelocityEvent
            PlayerVelocityEvent event = new PlayerVelocityEvent(player.getBukkitEntity(), player.getBukkitEntity().getVelocity());
            Bukkit.getPluginManager().callEvent(event);

            if (!event.isCancelled()) {
                player.getBukkitEntity().setVelocityDirect(event.getVelocity());
                player.playerConnection.sendPacket(new PacketPlayOutEntityVelocity(player));
            }

            //Update Player connection and new velocity
            player.velocityChanged = false;
            player.motX = vector.getX();
            player.motY = vector.getY();
            player.motZ = vector.getZ();
        }

    }
}
