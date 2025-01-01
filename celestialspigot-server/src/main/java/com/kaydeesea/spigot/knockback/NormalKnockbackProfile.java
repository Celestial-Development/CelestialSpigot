package com.kaydeesea.spigot.knockback;

import net.minecraft.server.*;
import org.bukkit.event.player.PlayerVelocityEvent;
import org.bukkit.util.Vector;

public interface NormalKnockbackProfile extends KnockBackProfile {
    double getFriction();

    void setFriction(double friction);

    double getHorizontal();

    void setHorizontal(double horizontal);

    double getVertical();

    void setVertical(double vertical);

    double getVerticalLimit();

    void setVerticalLimit(double verticalLimit);

    double getExtraHorizontal();

    void setExtraHorizontal(double extraHorizontal);

    double getExtraVertical();

    void setExtraVertical(double extraVertical);


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

    default void handleEntityHuman(EntityHuman victim, Entity source, int i, Vector vector) {
        if (i > 0) {
            source.g(
                    (-MathHelper.sin(victim.yaw * 3.1415927F / 180.0F) * (float) i * getExtraHorizontal()), getExtraVertical(),
                    (MathHelper.cos(victim.yaw * 3.1415927F / 180.0F) * (float) i * getExtraHorizontal())
            );
            victim.motX *= 0.6;
            victim.motZ *= 0.6;
            victim.setSprinting(false);
        }
        if (source instanceof EntityPlayer && source.velocityChanged) {
            EntityPlayer attackedPlayer = (EntityPlayer)source;
            PlayerVelocityEvent event = new PlayerVelocityEvent(attackedPlayer.getBukkitEntity(), attackedPlayer.getBukkitEntity().getVelocity());
            victim.world.getServer().getPluginManager().callEvent(event);
            if (!event.isCancelled()) {
                attackedPlayer.getBukkitEntity().setVelocityDirect(event.getVelocity());
                attackedPlayer.playerConnection.sendPacket(new PacketPlayOutEntityVelocity(attackedPlayer));
            }
            attackedPlayer.velocityChanged = false;
            attackedPlayer.motX = vector.getX();
            attackedPlayer.motY = vector.getY();
            attackedPlayer.motZ = vector.getZ();
        }

    }

    default ProfileType getType() {
        return ProfileType.NORMAL;
    }
}
