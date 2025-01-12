package com.kaydeesea.spigot.knockback;

import net.minecraft.server.*;
import org.bukkit.Bukkit;
import org.bukkit.event.player.PlayerVelocityEvent;
import org.bukkit.util.Vector;

public interface FoxKnockbackProfile extends KnockBackProfile {
    // 1point1kb setting
    void setOnePoint1kb(boolean point1kb);
    boolean isOnePoint1kb();

    // Vertical limit settings
    void setEnableVerticalLimit(boolean enableVerticalLimit);
    boolean isEnableVerticalLimit();

    void setVerticalLimit(double verticalLimit);
    double getVerticalLimit();

    // Horizontal and vertical knockback values
    void setHorizontal(double horizontal);
    double getHorizontal();

    void setVertical(double vertical);
    double getVertical();

    // Inheritance settings
    void setInheritH(boolean inheritH);
    boolean isInheritH();

    void setInheritY(boolean inheritY);
    boolean isInheritY();

    void setInheritHValue(double inheritHValue);
    double getInheritHValue();

    void setInheritYValue(double inheritYValue);
    double getInheritYValue();

    // Ground knockback values
    void setGroundH(double groundH);
    double getGroundH();

    void setGroundV(double groundV);
    double getGroundV();

    // Sprint knockback values
    void setSprintH(double sprintH);
    double getSprintH();

    void setSprintV(double sprintV);
    double getSprintV();

    //Combo settings will implement some day TODO
    // Other settings
    void setStopSprint(boolean stopSprint);
    boolean isStopSprint();

    void setSlowdown(double slowdown);
    double getSlowdown();



    default void handleEntityLiving(EntityLiving entityLiving, double d0, double d1, DamageSource source) {
        // FoxSpigot start
        entityLiving.ai = true;
        float f1 = MathHelper.sqrt(d0 * d0 + d1 * d1);
        float f2 = 0.4F;
        float f3 = 0.4F;
        double friccaoHorizontalorizontal = 2.0D;
        double friccaoVerticalertical = 2.0D - 0.25D;
        entityLiving.motX /= friccaoHorizontalorizontal;
        entityLiving.motY /= friccaoVerticalertical;
        entityLiving.motZ /= friccaoHorizontalorizontal;
        entityLiving.motX -= d0 / (double) f1 * (double) f2;
        entityLiving.motY += f3;
        entityLiving.motZ -= d1 / (double) f1 * (double) f2;
        if (entityLiving.motY > 0.4000000059604645D) {
            entityLiving.motY = 0.4000000059604645D;
        }
        // FoxSpigot end
    }

    default void handleEntityHuman(EntityHuman attacker, EntityPlayer player) {
        if (player != null && player.velocityChanged) {
            double velX = 0.0D;
            double velY = 0.0D;
            double velZ = 0.0D;
            if (isOnePoint1kb()) {
                Vector v = (new Vector(player.locX - attacker.locX, 0.0D, player.locZ - attacker.locZ)).normalize();
                velX = v.getX();
                velY = getVertical();
                velZ = v.getZ();


                velX *= getHorizontal();
                velZ *= getHorizontal();
                if (attacker.isSprinting() && !player.isSprinting()) {
                    velX *= getSprintH();
                    velY *= getSprintV();
                    velZ *= getSprintH();
                }

                if (attacker.isSprinting()) {
                    attacker.motX *= getSlowdown();
                    attacker.motZ *= getSlowdown();
                    if (isStopSprint()) {
                        attacker.setSprinting(false);
                    }
                }
            } else {
                double entityVelY;
                if (isInheritH()) {
                    entityVelY = player.motX * getInheritHValue();
                    double entityVelZ = player.motZ * getInheritHValue();
                    velX = entityVelY + Math.sin(Math.toRadians(attacker.yaw)) * -1.0D;
                    velZ = entityVelZ + Math.cos(Math.toRadians(attacker.yaw));
                } else {
                    velX = Math.sin(Math.toRadians(attacker.getHeadRotation())) * -1.0D;
                    velZ = Math.cos(Math.toRadians(attacker.getHeadRotation()));
                }

                velX *= getHorizontal();
                velZ *= getHorizontal();
                if (isInheritY()) {
                    entityVelY = player.motY * getInheritYValue();
                    velY = entityVelY + getVertical();
                } else {
                    velY = getVertical();
                }

                if (player.onGround) {
                    velX *= getGroundH();
                    velY *= getGroundV();
                    velZ *= getGroundH();
                }

                int enchLvl = EnchantmentManager.getEnchantmentLevel(Enchantment.KNOCKBACK.id, attacker.inventory.getItemInHand()) + 1;
                if (enchLvl > 0) {
                    velX *= enchLvl;
                    velZ *= enchLvl;
                }

                if (attacker.isSprinting() && !player.isSprinting()) {
                    velX *= getSprintH();
                    velY *= getSprintV();
                    velZ *= getSprintH();
                }

                if (attacker.isSprinting()) {
                    attacker.motX *= getSlowdown();
                    attacker.motZ *= getSlowdown();
                    if (isStopSprint()) {
                        attacker.setSprinting(false);
                    }
                }



                double yOff = player.locY - attacker.locY;
                if (isEnableVerticalLimit() && yOff > getVerticalLimit()) {
                    velY = 0.0D;
                }
            }


            PlayerVelocityEvent event = new PlayerVelocityEvent(player.getBukkitEntity(), new Vector(velX, velY, velZ));
            Bukkit.getPluginManager().callEvent(event);
            if (!event.isCancelled()) {
                player.getBukkitEntity().setVelocityDirect(event.getVelocity());
                player.playerConnection.sendPacket(new PacketPlayOutEntityVelocity(player));
            }
            player.velocityChanged = false;
            player.motX = velX;
            player.motY = velY;
            player.motZ = velZ;
        }

    }

    default ProfileType getType() {
        return ProfileType.FOX;
    }

}
