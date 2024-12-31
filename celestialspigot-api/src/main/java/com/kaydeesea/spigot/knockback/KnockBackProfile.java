package com.kaydeesea.spigot.knockback;

import net.minecraft.server.EntityHuman;
import net.minecraft.server.EntityLiving;

import java.util.ArrayList;

public interface KnockBackProfile {
    String getName();
    String[] getValuesString();
    ProfileType getType();
    ArrayList<String> getValues();
    void save();

    void handleEntityLiving(EntityLiving entityLiving, float f, double d0, double d1);

    void handleEntityHuman(EntityHuman entityHuman, int i);
}
