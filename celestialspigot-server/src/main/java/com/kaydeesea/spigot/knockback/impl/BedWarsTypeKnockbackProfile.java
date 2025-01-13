package com.kaydeesea.spigot.knockback.impl;

import com.kaydeesea.spigot.CelestialSpigot;
import com.kaydeesea.spigot.knockback.BedWarsKnockbackProfile;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Arrays;

@Getter
@Setter
public class BedWarsTypeKnockbackProfile implements BedWarsKnockbackProfile {
    private String name;
    private double frictionValue = 2.0D;
    private double horizontal = 0.9055D;
    private double vertical = 0.8835D;
    private double verticalLimit = 0.3534D;
    private double maxRangeReduction = 0.4D;
    private double rangeFactor = 0.2D;
    private double startRangeReduction = 3.0D;
    private int hitDelay = 20;
    private boolean wTap = false;
    private boolean slowdownBoolean = false;
    private boolean friction = false;

    public BedWarsTypeKnockbackProfile(String name) {
        this.name = name;
    }

    @Override
    public ArrayList<String> getValues() {
        String[] keys = new String[]{
                "friction", "horizontal", "vertical", "vertical-limit",
                "max-range-reduction", "range-factor", "start-range-reduction",
                "hit-delay",
                "w-tap", "slowdown-boolean", "friction-boolean"
        };
        return new ArrayList<>(Arrays.asList(keys));
    }
    public boolean isValueBoolean(String s) {
        return s.equalsIgnoreCase("w-tap") ||
                s.equalsIgnoreCase("slowdown-boolean") ||
                s.equalsIgnoreCase("friction-boolean");
    }

    @Override
    public void save() {
        final String path = "knockback.profiles." + this.name;
        CelestialSpigot.INSTANCE.getKnockBack().getConfig().set(path + ".type", this.getType().name());
        CelestialSpigot.INSTANCE.getKnockBack().getConfig().set(path + ".friction", this.frictionValue);
        CelestialSpigot.INSTANCE.getKnockBack().getConfig().set(path + ".horizontal", this.horizontal);
        CelestialSpigot.INSTANCE.getKnockBack().getConfig().set(path + ".vertical", this.vertical);
        CelestialSpigot.INSTANCE.getKnockBack().getConfig().set(path + ".vertical-limit", this.verticalLimit);
        CelestialSpigot.INSTANCE.getKnockBack().getConfig().set(path + ".max-range-reduction", this.maxRangeReduction);
        CelestialSpigot.INSTANCE.getKnockBack().getConfig().set(path + ".range-factor", this.rangeFactor);
        CelestialSpigot.INSTANCE.getKnockBack().getConfig().set(path + ".start-range-reduction", this.startRangeReduction);
        CelestialSpigot.INSTANCE.getKnockBack().getConfig().set(path + ".hit-delay", this.hitDelay);
        CelestialSpigot.INSTANCE.getKnockBack().getConfig().set(path + ".w-tap", this.wTap);
        CelestialSpigot.INSTANCE.getKnockBack().getConfig().set(path + ".slowdown-boolean", this.slowdownBoolean);
        CelestialSpigot.INSTANCE.getKnockBack().getConfig().set(path + ".friction-boolean", this.friction);
        CelestialSpigot.INSTANCE.getKnockBack().save();
    }

}
