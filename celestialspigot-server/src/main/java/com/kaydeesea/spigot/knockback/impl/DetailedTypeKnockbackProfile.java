package com.kaydeesea.spigot.knockback.impl;

import com.kaydeesea.spigot.CelestialSpigot;
import com.kaydeesea.spigot.knockback.DetailedKnockbackProfile;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Arrays;

@Getter
@Setter
public class DetailedTypeKnockbackProfile implements DetailedKnockbackProfile {
    private String name;
    private double frictionH = 2.0;
    private double frictionY = 2.0;
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
    private double slowdown = 0.5;

    public DetailedTypeKnockbackProfile(String name) {
        this.name = name;
    }

    @Override
    public ArrayList<String> getValues() {
        String[] keys = new String[]{
                "friction-horizontal", "friction-vertical",
                "horizontal", "vertical", "vertical-limit",
                "ground-horizontal", "ground-vertical",
                "sprint-horizontal", "sprint-vertical",
                "slowdown",
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
                s.equalsIgnoreCase("inherit-vertical");
    }

    @Override
    public void save() {
        final String path = "knockback.profiles." + this.name;
        CelestialSpigot.INSTANCE.getKnockBack().set(path + ".type", this.getType().name());
        CelestialSpigot.INSTANCE.getKnockBack().set(path + ".friction-horizontal", this.frictionH);
        CelestialSpigot.INSTANCE.getKnockBack().set(path + ".friction-vertical", this.frictionY);
        CelestialSpigot.INSTANCE.getKnockBack().set(path + ".horizontal", this.horizontal);
        CelestialSpigot.INSTANCE.getKnockBack().set(path + ".vertical", this.vertical);
        CelestialSpigot.INSTANCE.getKnockBack().set(path + ".vertical-limit", this.verticalLimit);
        CelestialSpigot.INSTANCE.getKnockBack().set(path + ".ground-horizontal", this.groundH);
        CelestialSpigot.INSTANCE.getKnockBack().set(path + ".ground-vertical", this.groundV);
        CelestialSpigot.INSTANCE.getKnockBack().set(path + ".sprint-horizontal", this.sprintH);
        CelestialSpigot.INSTANCE.getKnockBack().set(path + ".sprint-vertical", this.sprintV);
        CelestialSpigot.INSTANCE.getKnockBack().set(path + ".slowdown", this.slowdown);
        CelestialSpigot.INSTANCE.getKnockBack().set(path + ".enable-vertical-limit", this.enableVerticalLimit);
        CelestialSpigot.INSTANCE.getKnockBack().set(path + ".stop-sprint", this.stopSprint);
        CelestialSpigot.INSTANCE.getKnockBack().set(path + ".inherit-horizontal", this.inheritH);
        CelestialSpigot.INSTANCE.getKnockBack().set(path + ".inherit-vertical", this.inheritY);
        CelestialSpigot.INSTANCE.getKnockBack().set(path + ".inherit-horizontal-value", this.inheritHValue);
        CelestialSpigot.INSTANCE.getKnockBack().set(path + ".inherit-vertical-value", this.inheritYValue);
        CelestialSpigot.INSTANCE.getKnockBack().save();
    }
}
