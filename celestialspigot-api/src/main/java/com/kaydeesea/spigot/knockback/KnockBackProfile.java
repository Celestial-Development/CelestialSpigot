package com.kaydeesea.spigot.knockback;


import java.util.ArrayList;

public interface KnockBackProfile {
    String getName();
    String[] getValuesString();
    ProfileType getType();
    ArrayList<String> getValues();
    void save();
}
