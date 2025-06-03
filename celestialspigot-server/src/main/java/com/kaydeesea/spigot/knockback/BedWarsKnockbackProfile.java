package com.kaydeesea.spigot.knockback;

import net.minecraft.server.*;
import org.bukkit.event.player.PlayerVelocityEvent;
import org.bukkit.util.Vector;

public interface BedWarsKnockbackProfile extends KnockBackProfile {
    boolean isFriction();
    void setFriction(boolean value);

    double getFrictionValue();
    void setFrictionValue(double value);

    double getHorizontal();
    void setHorizontal(double value);

    double getMaxRangeReduction();
    void setMaxRangeReduction(double value);

    double getRangeFactor();
    void setRangeFactor(double value);

    double getStartRangeReduction();
    void setStartRangeReduction(double value);

    double getVertical();
    void setVertical(double value);

    double getVerticalLimit();
    void setVerticalLimit(double value);

    boolean isWTap();
    void setWTap(boolean value);

    boolean isSlowdownBoolean();
    void setSlowdownBoolean(boolean value);

    double getSlowdownValue();
    void setSlowdownValue(double value);


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

        double magnitude = Math.sqrt(d0 * d0 + d1 * d1);
        if (magnitude < 1e-4) return; // prevent division by 0

        double distance = this.distance(victim, source.getEntity());
        double rangeReduction = this.rangeReduction(distance);

        double horizontal = Math.max(getHorizontal() - rangeReduction, 0.0);
        double vertical = getVertical();
        double verticalLimit = getVerticalLimit();

        if (isFriction()) {
            double frictionValue = Math.max(1.0, getFrictionValue());
            victim.motX /= frictionValue;
            victim.motZ /= frictionValue;
        }

        Vector direction = new Vector(d0, 0, d1).normalize().multiply(horizontal);
        victim.motX -= direction.getX();
        victim.motZ -= direction.getZ();

        victim.motY += vertical;
        if (victim.motY > verticalLimit) {
            victim.motY = verticalLimit;
        }

        victim.velocityChanged = true;
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
