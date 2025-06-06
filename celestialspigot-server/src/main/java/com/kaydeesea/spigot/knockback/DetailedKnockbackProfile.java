package com.kaydeesea.spigot.knockback;

import com.kaydeesea.spigot.CelestialKnockBack;
import com.kaydeesea.spigot.util.NotchUtil;
import net.minecraft.server.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerVelocityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public interface DetailedKnockbackProfile extends KnockBackProfile {
    double REDUCER = 2.25;

    double getHorizontal();
    double getVertical();
    boolean isInheritH();
    double getInheritHValue();
    boolean isInheritY();
    double getInheritYValue();
    double getFrictionH();
    double getFrictionY();
    double getGroundH();
    double getGroundV();
    double getSprintH();
    double getSprintV();
    boolean isEnableVerticalLimit();
    double getVerticalLimit();
    boolean isStopSprint();
    double getSlowdown();
    boolean isCombo();
    int getComboTicks();
    double getComboVelocity();
    double getComboHeight();

    default void handleEntityLiving(EntityLiving entity, double d0, double d1, DamageSource source) {
        if (entity.random.nextDouble() >= entity.getAttributeInstance(GenericAttributes.c).getValue()) {
            entity.ai = true;

            float magnitude = MathHelper.sqrt((d0 * d0) + (d1 * d1));
            double verticalLimit = 0.4000000059604645D;

            // Legacy knockback support
            float mutliplier = 0.4F;

            if (getFrictionH() != 0) {
                entity.motX /= getFrictionH();
                entity.motZ /= getFrictionY();
            }

            if (getFrictionY() != 0) {
                entity.motY /= getFrictionY();
            }

            entity.motX -= d0 / (double) magnitude * (double) mutliplier;
            entity.motY += mutliplier;
            entity.motZ -= d1 / (double) magnitude * (double) mutliplier;

            if (entity.motY > verticalLimit) {
                entity.motY = verticalLimit;
            }
        }
    };

    default void handleEntityHuman(EntityHuman attacker, EntityPlayer player, Vector vector) {
        double velX, velY, velZ;

        if (isInheritH()) {
            double entityVelX = player.motX * getInheritHValue();
            double entityVelZ = player.motZ * getInheritHValue();

            velX = entityVelX + Math.sin(Math.toRadians(attacker.yaw)) * -1.0;
            velZ = entityVelZ + Math.cos(Math.toRadians(attacker.yaw));
        } else {
            velX = Math.sin(Math.toRadians(attacker.getHeadRotation())) * -1.0;
            velZ = Math.cos(Math.toRadians(attacker.getHeadRotation()));
        }

        velX *= getHorizontal();
        velZ *= getHorizontal();

        if (isInheritY()) {
            double entityVelY = player.motY * getInheritYValue();
            velY = entityVelY + getVertical();
        } else {
            velY = getVertical();
        }

        if (player.onGround) {
            velX *= getGroundH();
            velY *= getGroundV();
            velZ *= getGroundH();
        }

        //Knockback enchantment knockback
        int enchLvl = EnchantmentManager.getEnchantmentLevel(Enchantment.KNOCKBACK.id, attacker.inventory.getItemInHand()) + 1;
        if (enchLvl > 0) {
            velX *= enchLvl;
            velZ *= enchLvl;
        }

        if (attacker.shouldDealSprintKnockback) {
            velX *= getSprintH();
            velY *= getSprintV();
            velZ *= getSprintH();
            attacker.shouldDealSprintKnockback = false;
        }

        if (attacker.isSprinting()) {
            attacker.motX *= getSlowdown();
            attacker.motZ *= getSlowdown();
            attacker.shouldDealSprintKnockback = false;

            if (isStopSprint()) {
                attacker.setSprinting(false);
            }
        }

        if (isCombo()) {
            double yOff = player.locY - attacker.locY;

            if (yOff > getComboHeight()) {
                attacker.ticksDown = MinecraftServer.currentTick;
            }
            if (yOff > getComboHeight() || MinecraftServer.currentTick - attacker.ticksDown < getComboTicks()) {
                velY = getComboVelocity();
                vector.setY(getComboVelocity());
            }
        }

        double yOff = player.locY - attacker.locY;
        if (isEnableVerticalLimit() && yOff > getVerticalLimit()) {
            velY = 0.0D;
        }

        if (player.getBukkitEntity().hasMetadata("NPC") || player.velocityChanged) {
            Vector vector1 = new Vector(velX, velY, velZ);
            vector1 = this.reduceEnchant(player.getBukkitEntity(), vector1);

            // Inlined logic from CelestialKnockBack
            if (player.getBukkitEntity().hasMetadata("NPC") || player.getBukkitEntity().hasMetadata("bolt-npc")) {
                player.motX = 0;
                player.motY = 0;
                player.motZ = 0;
                player.g(vector1.getX(), vector1.getY(), vector1.getZ());
                player.playerConnection.lastMotionTick = MinecraftServer.currentTick;
                if (player.fallDistance > 0) {
                    player.fallDistance = 0;
                }
            } else {
                PlayerVelocityEvent event = new PlayerVelocityEvent(player.getBukkitEntity(), vector1);
                Bukkit.getServer().getPluginManager().callEvent(event);

                if (!event.isCancelled()) {
                    vector1 = event.getVelocity();
                    player.getBukkitEntity().setVelocity(vector1);
                    player.playerConnection.sendPacket(new PacketPlayOutEntityVelocity(player.getId(), vector1.getX(), vector1.getY(), vector1.getZ()));
                    if (player.fallDistance > 0) {
                        player.fallDistance = 0.0f;
                    }
                }
                player.playerConnection.lastMotionTick = MinecraftServer.currentTick;
                player.velocityChanged = false;
                player.motX = vector.getX();
                player.motY = vector.getY();
                player.motZ = vector.getZ();
            }
        }

    }
    default Vector reduceEnchant(Player player, Vector vector) {
        Player entity = NotchUtil.getLastAttacker(player);
        if (entity == null) return vector;

        ItemStack itemStack = entity.getItemInHand();
        if (itemStack == null) return vector;

        int level = itemStack.getEnchantmentLevel(org.bukkit.enchantments.Enchantment.KNOCKBACK);
        if (level >= 1) {
            double x = vector.getX() / REDUCER;
            double z = vector.getZ() / REDUCER;

            vector.setX(x);
            vector.setZ(z);
        }
        return vector;
    }
}
