package net.minecraft.server;

import java.util.Iterator;
import java.util.List;

// CraftBukkit start
import java.util.HashMap;
import java.util.Map;

import com.kaydeesea.spigot.CelestialSpigot;
import org.bukkit.craftbukkit.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.PotionSplashEvent;
// CraftBukkit end

public class EntityPotion extends EntityProjectile {

    public ItemStack item;

    public EntityPotion(World world) {
        super(world);
    }

    public EntityPotion(World world, EntityLiving entityliving, int i) {
        this(world, entityliving, new ItemStack(Items.POTION, 1, i));
    }

    public EntityPotion(World world, EntityLiving entityliving, ItemStack itemstack) {
        super(world, entityliving);
        this.item = itemstack;
    }

    public EntityPotion(World world, double d0, double d1, double d2, ItemStack itemstack) {
        super(world, d0, d1, d2);
        this.item = itemstack;
    }

    protected float m() {
        return CelestialSpigot.INSTANCE.getConfig().getPotionFallSpeed();
    }

    protected float j() {
        return CelestialSpigot.INSTANCE.getConfig().getPotionThrowMultiplier();
    }

    protected float l() {
        return CelestialSpigot.INSTANCE.getConfig().getPotionThrowOffset();
    }

    public void setPotionValue(int i) {
        if (this.item == null) {
            this.item = new ItemStack(Items.POTION, 1, 0);
        }

        this.item.setData(i);
    }

    public int getPotionValue() {
        if (this.item == null) {
            this.item = new ItemStack(Items.POTION, 1, 0);
        }

        return this.item.getData();
    }

    protected void a(MovingObjectPosition movingobjectposition) {
        if (!this.world.isClientSide) {
            List<MobEffect> list = Items.POTION.h(this.item);

            if (true || list != null && !list.isEmpty()) { // CraftBukkit - Call event even if no effects to apply
                AxisAlignedBB affectArea = this.getBoundingBox().grow(4.0D, 2.0D, 4.0D);
                List<Entity> entitiesAround = this.world.a(EntityLiving.class, affectArea);

                if (true || !entitiesAround.isEmpty()) { // CraftBukkit - Run code even if there are no entities around
                    Iterator<Entity> iterator = entitiesAround.iterator();
                    Map<LivingEntity, Double> affected = new HashMap<>();
                    while (iterator.hasNext()) {
                        EntityLiving entityliving = (EntityLiving) iterator.next();
                        double d0 = this.h(entityliving);

                        if (d0 < 16.0D) {
                            double d1 = 1.0D - Math.sqrt(d0) / 4.0D;

                            if (entityliving == movingobjectposition.entity) {
                                d1 = 1.0D;
                            } else if (Math.hypot(this.motX, this.motZ) > 0.4D && d1 > 0.8D) {
                                d1 = 1.0D;
                            }

                            // CraftBukkit start
                            affected.put((LivingEntity) entityliving.getBukkitEntity(), d1);
                        }
                    }

                    PotionSplashEvent event = CraftEventFactory.callPotionSplashEvent(this, affected);
                    if (!event.isCancelled() && list != null && !list.isEmpty()) { // do not process effects if there are no effects to process
                        for ( LivingEntity victim : event.getAffectedEntities() ) {
                            if (!(victim instanceof CraftLivingEntity)) continue;
                            EntityLiving entityliving = ((CraftLivingEntity) victim).getHandle();

                            if (this.shooter != null && entityliving instanceof EntityPlayer) {
                                EntityPlayer entityPlayer = ((EntityPlayer) entityliving);
                                if (entityPlayer.getBukkitEntity() != null) {
                                    // Carbon start - Entity Hider/Visibility Patch
                                    if (!entityPlayer.getBukkitEntity().canSeeEntity(this.shooter.getBukkitEntity())) {
                                        continue;
                                    }
                                    // Carbon end
                                }
                            }

                            double d1 = event.getIntensity(victim);
                            // CraftBukkit end

                            for ( MobEffect mobeffect : list ) {
                                int i = mobeffect.getEffectId();

                                // CraftBukkit start - Abide by PVP settings - for players only!
                                if (!this.world.pvpMode && this.getShooter() instanceof EntityPlayer && entityliving instanceof EntityPlayer && entityliving != this.getShooter()) {
                                    // Block SLOWER_MOVEMENT, SLOWER_DIG, HARM, BLINDNESS, HUNGER, WEAKNESS and POISON potions
                                    if (i == 2 || i == 4 || i == 7 || i == 15 || i == 17 || i == 18 || i == 19)
                                        continue;
                                }
                                // CraftBukkit end

                                if (MobEffectList.byId[i].isInstant()) {
                                    MobEffectList.byId[i].applyInstantEffect(this, this.getShooter(), entityliving, mobeffect.getAmplifier(), d1);
                                } else {
                                    int j = (int) (d1 * (double) mobeffect.getDuration() + 0.5D);

                                    if (j > 20) {
                                        entityliving.addEffect(new MobEffect(i, j, mobeffect.getAmplifier()));
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Carbon start - Entity Hider/Visibility Patch
            if (this.getShooter() != null && this.getShooter() instanceof EntityHuman) {
                int x = (int) Math.round(this.locX);
                int y = (int) Math.round(this.locY);
                int z = (int) Math.round(this.locZ);
                BlockPosition customLocation = new BlockPosition(x, y, z);
                this.world.a((EntityHuman)this.getShooter(), 2002, customLocation, this.getPotionValue());
            } else {
                this.world.triggerEffect(2002, new BlockPosition(this), this.getPotionValue());
            }
            // Carbon end
            this.die();
        }

    }

    public void a(NBTTagCompound nbttagcompound) {
        super.a(nbttagcompound);
        if (nbttagcompound.hasKeyOfType("Potion", 10)) {
            this.item = ItemStack.createStack(nbttagcompound.getCompound("Potion"));
        } else {
            this.setPotionValue(nbttagcompound.getInt("potionValue"));
        }

        if (this.item == null) {
            this.die();
        }

    }

    public void b(NBTTagCompound nbttagcompound) {
        super.b(nbttagcompound);
        if (this.item != null) {
            nbttagcompound.set("Potion", this.item.save(new NBTTagCompound()));
        }

    }
}
