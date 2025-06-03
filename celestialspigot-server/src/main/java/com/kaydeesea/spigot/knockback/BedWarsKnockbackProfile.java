package com.kaydeesea.spigot.knockback;

import net.minecraft.server.*;
import org.bukkit.event.player.PlayerVelocityEvent;
import org.bukkit.util.Vector;

public interface BedWarsKnockbackProfile extends KnockBackProfile {
    boolean isFriction();
    double getFrictionValue();
    double getHorizontal();
    double getMaxRangeReduction();
    double getRangeFactor();
    double getStartRangeReduction();
    double getVertical();
    double getVerticalLimit();
    boolean isWTap();
    boolean isSlowdownBoolean();
    double getSlowdownValue();


    default double distance(Entity entity1, Entity entity2) {
        if (!entity1.getBukkitEntity().getWorld().equals(entity2.getBukkitEntity().getWorld())) {
            return -1.0;
        }
        return entity1.getBukkitEntity().getLocation().distance(entity2.getBukkitEntity().getLocation());
    }

    default double rangeReduction(double distance) {
        double startRangeReduction = getStartRangeReduction();
        double rangeFactor = getRangeFactor();
        double maxRangeReduction = getMaxRangeReduction();
        if (distance >= startRangeReduction) {
            return Math.min((distance - startRangeReduction) * rangeFactor, maxRangeReduction);
        }
        return 0.0;
    }

    default void handleEntityLiving(EntityLiving victim, double d0, double d1, DamageSource source) {
        victim.ai = true;

        double frictionValue = getFrictionValue();
        boolean friction = isFriction();
        if (friction) {
            victim.motX /= frictionValue;
            victim.motZ /= frictionValue;
        }
        double vertical = getVertical();
        double verticalLimit = getVerticalLimit();
        double distance = this.distance(victim, source.getEntity());
        double rangeReduction = this.rangeReduction(distance);
        double magnitude = Math.sqrt(d0 * d0 + d1 * d1);
        double horizontal = getHorizontal();
        double horizontalReduction = horizontal - rangeReduction;

        victim.motX -= d0 / magnitude * horizontalReduction;
        victim.motZ -= d1 / magnitude * horizontalReduction;

        victim.motY += vertical;
        if (victim.motY > verticalLimit) {
            victim.motY = verticalLimit;
        }

    }


    default void handleEntityHuman(EntityHuman attacker, EntityPlayer player, int i, Vector vector) {
        if (isSlowdownBoolean()) {
            double slow = getSlowdownValue();
            attacker.motX *= slow;
            attacker.motZ *= slow;
        }

        if (isWTap()) {
            attacker.setSprinting(false);
        }

        if (player != null && player.velocityChanged) {
            PlayerVelocityEvent event = new PlayerVelocityEvent(player.getBukkitEntity(), player.getBukkitEntity().getVelocity());
            attacker.world.getServer().getPluginManager().callEvent(event);
            if (!event.isCancelled()) {
                player.getBukkitEntity().setVelocityDirect(event.getVelocity());
                player.playerConnection.sendPacket(new PacketPlayOutEntityVelocity(player));
            }
            player.velocityChanged = false;
            player.motX = vector.getX();
            player.motY = vector.getY();
            player.motZ = vector.getZ();
        }
    }


}
