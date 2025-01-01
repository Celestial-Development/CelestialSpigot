package com.kaydeesea.spigot.knockback;


import java.util.ArrayList;

public interface KnockBackProfile {
    String getName();
    ProfileType getType();
    ArrayList<String> getValues();
    void save();
}
