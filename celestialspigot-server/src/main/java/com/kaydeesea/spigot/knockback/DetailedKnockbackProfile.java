package com.kaydeesea.spigot.knockback;

import net.minecraft.server.*;
import org.bukkit.event.player.PlayerVelocityEvent;
import org.bukkit.util.Vector;

public interface DetailedKnockbackProfile extends KnockBackProfile {

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
        float f1 = MathHelper.sqrt(d0 * d0 + d1 * d1);
        float f2 = 0.4F;

        entityLiving.motX /= getFrictionH();
        entityLiving.motY /= getFrictionY();
        entityLiving.motZ /= getFrictionH();
        entityLiving.motX -= d0 / (double) f1 * (double) f2;
        entityLiving.motY +=f2;
        entityLiving.motZ -= d1 / (double) f1 * (double) f2;
        if (entityLiving.motY > 0.4000000059604645D) {
            entityLiving.motY = 0.4000000059604645D;
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

    default void handleEntityHuman(EntityHuman victim, EntityPlayer source, int i, Vector vector) {

        if (source != null && source.velocityChanged) {
            double velX;
            double velY;
            double velZ;
            double yOff;
            double entityVelY;

            if (isInheritH()) {
                entityVelY = victim.motX * getInheritHValue();
                double entityVelZ = victim.motZ * getInheritHValue();

                velX = entityVelY + Math.sin(Math.toRadians(source.yaw)) * -1.0D;
                velZ = entityVelZ + Math.cos(Math.toRadians(source.yaw));
            } else {
                velX = Math.sin(Math.toRadians(source.getHeadRotation())) * -1.0D;
                velZ = Math.cos(Math.toRadians(source.getHeadRotation()));
            }

            velX *= getHorizontal();
            velZ *= getHorizontal();

            if (isInheritY()) {
                entityVelY = victim.motY * getInheritYValue();
                velY = entityVelY + getVertical();
            } else {
                velY = getVertical();
            }

            if (victim.onGround) {
                velX *= getGroundH();
                velY *= getGroundV();
                velZ *= getGroundH();
            }

            //Knockback enchantment knockback
            int enchLvl = EnchantmentManager.getEnchantmentLevel(Enchantment.KNOCKBACK.id, source.inventory.getItemInHand()) + 1;
            if (enchLvl > 0) {
                velX *= enchLvl;
                velZ *= enchLvl;
            }

            if (source.isSprinting() && !victim.isSprinting()) {
                velX *= getSprintH();
                velY *= getSprintV();
                velZ *= getSprintH();
            }

            if (source.isSprinting()) {
                victim.motX *= getSlowdown();
                victim.motZ *= getSlowdown();
                if(isStopSprint()) victim.setSprinting(false);
            }


            yOff = victim.locY - source.locY;

            if (isEnableVerticalLimit() && yOff > getVerticalLimit()) {
                velY = 0.0D;
            }
            PlayerVelocityEvent event = new PlayerVelocityEvent(source.getBukkitEntity(), source.getBukkitEntity().getVelocity());
            victim.world.getServer().getPluginManager().callEvent(event);
            if (!event.isCancelled()) {
                source.getBukkitEntity().setVelocityDirect(event.getVelocity());
                source.playerConnection.sendPacket(new PacketPlayOutEntityVelocity(source));
            }
            //Update our velocity
            source.velocityChanged = false;
            source.motX = velX;
            source.motY = velY;
            source.motZ = velZ;
        }

    }

    default ProfileType getType() {
        return ProfileType.DETAILED;
    }
}
