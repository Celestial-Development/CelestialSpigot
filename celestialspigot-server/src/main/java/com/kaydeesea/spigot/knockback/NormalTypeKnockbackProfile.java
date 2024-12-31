package com.kaydeesea.spigot.knockback;

import org.bukkit.ChatColor;
import com.kaydeesea.spigot.CelestialSpigot;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Arrays;

@Getter
@Setter
public class NormalTypeKnockbackProfile implements NormalKnockbackProfile {

    @Getter
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
    public String[] getValuesString() {
        return new String[]{
                ChatColor.GREEN + "Type" + ChatColor.WHITE + ": "+ this.getType().name(),
                ChatColor.AQUA + "Friction" + ChatColor.WHITE + ": " + this.friction,
                ChatColor.AQUA + "Horizontal" + ChatColor.WHITE + ": " + this.horizontal,
                ChatColor.AQUA + "Vertical" + ChatColor.WHITE + ": " + this.vertical,
                ChatColor.AQUA + "Vertical Limit" + ChatColor.WHITE + ": " + this.verticalLimit,
                ChatColor.AQUA + "Extra Horizontal" + ChatColor.WHITE + ": " + this.extraHorizontal,
                ChatColor.AQUA + "Extra Vertical" + ChatColor.WHITE + ": " + this.extraVertical,
        };
    }



    @Override
    public ArrayList<String> getValues() {
        String[] a = new String[] {"friction", "horizontal", "vertical", "vertical-limit", "extra-horizontal", "extra-vertical"};
        return new ArrayList<>(Arrays.asList(a));
    }

    public void save() {
        final String path = "knockback.profiles." + this.name;

        CelestialSpigot.INSTANCE.getConfig().set(path + ".type", this.getType().name());
        CelestialSpigot.INSTANCE.getConfig().set(path + ".friction", this.friction);
        CelestialSpigot.INSTANCE.getConfig().set(path + ".horizontal", this.horizontal);
        CelestialSpigot.INSTANCE.getConfig().set(path + ".vertical", this.vertical);
        CelestialSpigot.INSTANCE.getConfig().set(path + ".vertical-limit", this.verticalLimit);
        CelestialSpigot.INSTANCE.getConfig().set(path + ".extra-horizontal", this.extraHorizontal);
        CelestialSpigot.INSTANCE.getConfig().set(path + ".extra-vertical", this.extraVertical);
        CelestialSpigot.INSTANCE.getConfig().save();
    }

}
