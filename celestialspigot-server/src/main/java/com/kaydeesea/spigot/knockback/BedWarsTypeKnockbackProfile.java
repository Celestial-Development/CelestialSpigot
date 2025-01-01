package com.kaydeesea.spigot.knockback;

import com.kaydeesea.spigot.CelestialSpigot;
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
                "w-tap", "slowdown-boolean", "friction-boolean"
        };
        return new ArrayList<>(Arrays.asList(keys));
    }

    @Override
    public void save() {
        final String path = "knockback.profiles." + this.name;
        CelestialSpigot.INSTANCE.getConfig().set(path + ".type", this.getType().name());
        CelestialSpigot.INSTANCE.getConfig().set(path + ".friction", this.frictionValue);
        CelestialSpigot.INSTANCE.getConfig().set(path + ".horizontal", this.horizontal);
        CelestialSpigot.INSTANCE.getConfig().set(path + ".vertical", this.vertical);
        CelestialSpigot.INSTANCE.getConfig().set(path + ".vertical-limit", this.verticalLimit);
        CelestialSpigot.INSTANCE.getConfig().set(path + ".max-range-reduction", this.maxRangeReduction);
        CelestialSpigot.INSTANCE.getConfig().set(path + ".range-factor", this.rangeFactor);
        CelestialSpigot.INSTANCE.getConfig().set(path + ".start-range-reduction", this.startRangeReduction);
        CelestialSpigot.INSTANCE.getConfig().set(path + ".w-tap", this.wTap);
        CelestialSpigot.INSTANCE.getConfig().set(path + ".slowdown-boolean", this.slowdownBoolean);
        CelestialSpigot.INSTANCE.getConfig().set(path + ".friction-boolean", this.friction);
        CelestialSpigot.INSTANCE.getConfig().save();
    }

}
