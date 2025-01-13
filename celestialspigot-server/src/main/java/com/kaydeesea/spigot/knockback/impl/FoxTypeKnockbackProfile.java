package com.kaydeesea.spigot.knockback.impl;

import com.kaydeesea.spigot.CelestialSpigot;
import com.kaydeesea.spigot.knockback.FoxKnockbackProfile;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Arrays;

@Getter
@Setter
public class FoxTypeKnockbackProfile implements FoxKnockbackProfile {
    private String name;
    private boolean onePoint1kb = false;
    private boolean inheritH = true;
    private boolean inheritY = false;
    private double inheritHValue = 1.0;
    private double inheritYValue = 0.5;
    private double horizontal = 0.35;
    private double vertical = 0.35;
    private boolean enableVerticalLimit = false;
    private double verticalLimit = 0.4;
    private double groundH = 1.06;
    private double groundV = 1.0;
    private double sprintH = 1.23;
    private double sprintV = 1.0;
    private boolean stopSprint = true;
    private int hitDelay = 20;
    private double slowdown = 0.5;

    public FoxTypeKnockbackProfile(String name) {
        this.name = name;
    }


    @Override
    public ArrayList<String> getValues() {
        String[] keys = new String[]{
                "1-point-1-kb",
                "horizontal", "vertical", "vertical-limit",
                "ground-horizontal", "ground-vertical",
                "sprint-horizontal", "sprint-vertical",
                "slowdown", "hit-delay",
                "enable-vertical-limit", "stop-sprint",
                "inherit-horizontal", "inherit-vertical",
                "inherit-horizontal-value", "inherit-vertical-value"
        };
        return new ArrayList<>(Arrays.asList(keys));
    }

    public boolean isValueBoolean(String s) {
        return s.equalsIgnoreCase("enable-vertical-limit") ||
                s.equalsIgnoreCase("stop-sprint") ||
                s.equalsIgnoreCase("inherit-horizontal") ||
                s.equalsIgnoreCase("inherit-vertical")  ||
                s.equalsIgnoreCase("1-point-1-kb");
    }

    @Override
    public void save() {
        final String path = "knockback.profiles." + this.name;
        CelestialSpigot.INSTANCE.getKnockBack().getConfig().set(path + ".type", this.getType().name());
        CelestialSpigot.INSTANCE.getKnockBack().getConfig().set(path + ".1-point-1-kb", this.onePoint1kb);
        CelestialSpigot.INSTANCE.getKnockBack().getConfig().set(path + ".horizontal", this.horizontal);
        CelestialSpigot.INSTANCE.getKnockBack().getConfig().set(path + ".vertical", this.vertical);
        CelestialSpigot.INSTANCE.getKnockBack().getConfig().set(path + ".vertical-limit", this.verticalLimit);
        CelestialSpigot.INSTANCE.getKnockBack().getConfig().set(path + ".ground-horizontal", this.groundH);
        CelestialSpigot.INSTANCE.getKnockBack().getConfig().set(path + ".ground-vertical", this.groundV);
        CelestialSpigot.INSTANCE.getKnockBack().getConfig().set(path + ".sprint-horizontal", this.sprintH);
        CelestialSpigot.INSTANCE.getKnockBack().getConfig().set(path + ".sprint-vertical", this.sprintV);
        CelestialSpigot.INSTANCE.getKnockBack().getConfig().set(path + ".slowdown", this.slowdown);
        CelestialSpigot.INSTANCE.getKnockBack().getConfig().set(path + ".hit-delay", this.hitDelay);
        CelestialSpigot.INSTANCE.getKnockBack().getConfig().set(path + ".enable-vertical-limit", this.enableVerticalLimit);
        CelestialSpigot.INSTANCE.getKnockBack().getConfig().set(path + ".stop-sprint", this.stopSprint);
        CelestialSpigot.INSTANCE.getKnockBack().getConfig().set(path + ".inherit-horizontal", this.inheritH);
        CelestialSpigot.INSTANCE.getKnockBack().getConfig().set(path + ".inherit-vertical", this.inheritY);
        CelestialSpigot.INSTANCE.getKnockBack().getConfig().set(path + ".inherit-horizontal-value", this.inheritHValue);
        CelestialSpigot.INSTANCE.getKnockBack().getConfig().set(path + ".inherit-vertical-value", this.inheritYValue);
        CelestialSpigot.INSTANCE.getKnockBack().save();
    }
}
