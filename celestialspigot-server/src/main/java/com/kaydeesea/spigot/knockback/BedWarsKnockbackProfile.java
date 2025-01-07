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
        // pratSpigot start
        victim.ai = true;
        double magnitude = Math.sqrt(d0 * d0 + d1 * d1);
        double horizontal = getHorizontal();
        double vertical = getVertical();
        double verticalLimit = getVerticalLimit();
        double frictionValue = getFrictionValue() - (1.0 - horizontal);
        boolean friction = isFriction();
        if (friction) {
            victim.motX /= frictionValue;
            victim.motZ /= frictionValue;
        }
        double distance = this.distance(victim, source.getEntity());
        double rangeReduction = this.rangeReduction(distance);
        double horizontalReduction = horizontal - rangeReduction;
        victim.motX -= d0 / magnitude * horizontalReduction;
        victim.motZ -= d1 / magnitude * horizontalReduction;
        victim.motY += vertical;
        if (victim.motY > verticalLimit) {
            victim.motY = verticalLimit;
        }
        // pratSpigot end
    };

    default void handleEntityHuman(EntityHuman victim, EntityPlayer source, int i, Vector vector) {
        boolean wtap = isWTap();
        boolean slowdownBoolean = isSlowdownBoolean();
        if (slowdownBoolean) {
            victim.motX *= 0.6;
            victim.motY *= 0.6;
        }
        if (wtap) {
            victim.setSprinting(false);
        }
        if (source != null && source.velocityChanged) {
            PlayerVelocityEvent event = new PlayerVelocityEvent(source.getBukkitEntity(), source.getBukkitEntity().getVelocity());
            victim.world.getServer().getPluginManager().callEvent(event);
            if (!event.isCancelled()) {
                source.getBukkitEntity().setVelocityDirect(event.getVelocity());
                source.playerConnection.sendPacket(new PacketPlayOutEntityVelocity(source));
            }
            source.velocityChanged = false;
            source.motX = vector.getX();
            source.motY = vector.getY();
            source.motZ = vector.getZ();
        }


    }

    default ProfileType getType() {
        return ProfileType.BEDWARS;
    }
}
