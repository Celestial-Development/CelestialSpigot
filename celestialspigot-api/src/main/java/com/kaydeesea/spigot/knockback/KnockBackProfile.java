package com.kaydeesea.spigot.knockback;


import java.util.List;

public interface KnockBackProfile {
    String getName();
    List<String> getValues();
    void save();
    int getHitDelay();
}
