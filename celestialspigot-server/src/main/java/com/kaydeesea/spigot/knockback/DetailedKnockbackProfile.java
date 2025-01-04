package com.kaydeesea.spigot.knockback;

import net.minecraft.server.*;
import org.bukkit.event.player.PlayerVelocityEvent;
import org.bukkit.util.Vector;

public interface DetailedKnockbackProfile extends KnockBackProfile {
    /*
        TYPE: DETAILED
    # Horizontal Multiplier
    HORIZONTAL: 0.4449
    # Vertical Value
    VERTICAL: 0.35555
    # Add a certain value to horizontal before actual calculations
    INHERIT-H-ENABLED: true
    INHERIT-H-VALUE: 1.21
    # Add a certain value to vertical before actual calculations
    INHERIT-V-ENABLED: false
    INHERIT-V-VALUE: 1.0
    # Friction values, actual knockback is divided by this
    FRICTION-H: 2.0
    FRICTION-V: 2.0
    # When player is hit but on ground, this value is multiplied with initial kb
    GROUND-H: 1.06
    GROUND-V: 1.0
    # When player is hit but attacker is spriting, this value is multiplied with initial kb
    SPRINT-H: 1.23
    SPRINT-V: 1.0
    # Should we dynamically limit player's vertical axis?
    Y-LIMIT: false
    # Dynamic vertical axis limit value
    Y-LIMIT-VALUE: 1.2
    # Should the attacker have their sprint reset upon attacking
    STOP-SPRINT: true
    # This value is multiplied in horizontal axis after calculations and sprint reset
    SLOWDOWN: 0.5
     */

    void setFrictionH(double frictionH);
    double getFrictionH();

    void setFrictionY(double frictionY);
    double getFrictionY();

    void setInheritH(boolean inheritH);
    boolean isInheritH();

    void setInheritY(boolean inheritY);
    boolean isInheritY();

    double getInheritHValue();
    void setInheritHValue(double inheritHValue);

    double getInheritYValue();
    void setInheritYValue(double inheritYValue);

    double getHorizontal();
    void setHorizontal(double horizontal);

    double getVertical();
    void setVertical(double vertical);

    double getVerticalLimit();
    void setVerticalLimit(double verticalLimit);

    double getGroundH();
    void setGroundH(double groundH);

    double getGroundV();
    void setGroundV(double groundV);

    double getSprintH();
    void setSprintH(double sprintH);

    double getSprintV();
    void setSprintV(double sprintV);

    boolean isEnableVerticalLimit();
    void setEnableVerticalLimit(boolean enableVerticalLimit);

    boolean isStopSprint();
    void setStopSprint(boolean stopSprint);

    double getSlowdown();
    void setSlowdown(double slowdown);



    default void handleEntityLiving(EntityLiving entityLiving, double d0, double d1, DamageSource source) {
        entityLiving.ai = true;
        double magnitude = MathHelper.sqrt(d0 * d0 + d1 * d1);

        if (isInheritH()) {
            d0 *= getInheritHValue();
            d1 *= getInheritHValue();
        }
        if (isInheritY()) {
            entityLiving.motY *= getInheritYValue();
        }

        entityLiving.motX /= getFrictionH();
        entityLiving.motY /= getFrictionY();
        entityLiving.motZ /= getFrictionH();

        entityLiving.motX -= d0 / magnitude * getHorizontal();
        entityLiving.motY += getVertical();
        entityLiving.motZ -= d1 / magnitude * getHorizontal();


        if (entityLiving.onGround) {
            entityLiving.motX *= getGroundH();
            entityLiving.motY *= getGroundV();
        }
        if(source.getEntity().isSprinting()) {
            entityLiving.motX *= getSprintH();
            entityLiving.motY *= getSprintV();
        }

        if (isEnableVerticalLimit() && entityLiving.motY > getVerticalLimit()) {
            entityLiving.motY = getVerticalLimit();
        }
//        if(isLimitHorizontal()) {
//            double horLimit = getHorizontalLimit();
//            if(entityLiving.motX < 0) {
//                if (entityLiving.motX < -horLimit) {
//                    entityLiving.motX = -horLimit;
//                }
//            } else {
//                if(entityLiving.motX > horLimit) {
//                    entityLiving.motX = horLimit;
//                }
//            }
//            if(entityLiving.motZ < 0) {
//                if(entityLiving.motZ < -horLimit) {
//                    entityLiving.motZ = -horLimit;
//                }
//            } else {
//                if(entityLiving.motZ > horLimit) {
//                    entityLiving.motZ = horLimit;
//                }
//            }
//            System.out.println(entityLiving.motX);
//            System.out.println(entityLiving.motY);
//            System.out.println(entityLiving.motZ);
//        }
        // cSpigot end
    };

    default void handleEntityHuman(EntityHuman victim, Entity source, int i, Vector vector) {
        victim.motX *= getSlowdown();
        victim.motZ *= getSlowdown();
        if(isStopSprint()) victim.setSprinting(false);

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
        return ProfileType.DETAILED;
    }
}
