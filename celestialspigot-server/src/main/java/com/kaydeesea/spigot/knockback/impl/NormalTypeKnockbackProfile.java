package com.kaydeesea.spigot.knockback.impl;

import com.kaydeesea.spigot.CelestialSpigot;
import com.kaydeesea.spigot.knockback.NormalKnockbackProfile;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Arrays;

@Getter
@Setter
public class NormalTypeKnockbackProfile implements NormalKnockbackProfile {

    private String name;
    private double friction = 2.0D;
    private double horizontal = 0.35D;
    private double vertical = 0.35D;
    private double verticalLimit = 0.4D;
    private double extraHorizontal = 0.425D;
    private double extraVertical = 0.085D;

    public NormalTypeKnockbackProfile(String name) {
        this.name = name;
    }

    @Override
    public ArrayList<String> getValues() {
        String[] a = new String[] {"friction", "horizontal", "vertical", "vertical-limit", "extra-horizontal", "extra-vertical"};
        return new ArrayList<>(Arrays.asList(a));
    }

    public void save() {
        final String path = "knockback.profiles." + this.name;
        CelestialSpigot.INSTANCE.getKnockBack().set(path + ".type", this.getType().name());
        CelestialSpigot.INSTANCE.getKnockBack().set(path + ".friction", this.friction);
        CelestialSpigot.INSTANCE.getKnockBack().set(path + ".horizontal", this.horizontal);
        CelestialSpigot.INSTANCE.getKnockBack().set(path + ".vertical", this.vertical);
        CelestialSpigot.INSTANCE.getKnockBack().set(path + ".vertical-limit", this.verticalLimit);
        CelestialSpigot.INSTANCE.getKnockBack().set(path + ".extra-horizontal", this.extraHorizontal);
        CelestialSpigot.INSTANCE.getKnockBack().set(path + ".extra-vertical", this.extraVertical);
        CelestialSpigot.INSTANCE.getKnockBack().save();
    }

}
