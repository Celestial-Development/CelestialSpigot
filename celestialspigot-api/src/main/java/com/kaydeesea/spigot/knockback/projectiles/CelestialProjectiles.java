package com.kaydeesea.spigot.knockback.projectiles;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.configuration.file.YamlConfiguration;

public class CelestialProjectiles {
    // Projectile settings with default values
    @Setter
    @Getter
    public static float
            potionThrowMultiplier = 0.5f,
            potionThrowOffset = -10.0f,
            potionFallSpeed = 0.05f;

    @Setter
    @Getter
    public static float
            pearlSpeed = 1.5f,
            pearlGravity = 0.03f,
            pearlVerticalOffset = 0.0f;
    @Getter
    @Setter
    private static boolean
            pearlDamage = true,
            rodEnabled = true,
            bowEnabled = true,
            pearlEnabled = true,
            snowballEnabled = true,
            eggEnabled = true,
            explosionEnabled = true;

    @Getter
    @Setter
    public static double
            rodHorizontal = 0.4,
            rodVertical = 0.4,
            bowHorizontal = 0.4,
            bowVertical = 0.4,
            pearlHorizontal = 0.4,
            pearlVertical = 0.4,
            snowballHorizontal = 0.4,
            snowballVertical = 0.4,
            eggHorizontal = 0.4,
            eggVertical = 0.4,
            explosionHorizontal = 0.4,
            explosionVertical = 0.4;


    @Getter
    @Setter
    public static boolean
            limitVertical = false;
    @Getter
    @Setter
    public static double
            verticalLimit = 0.4000000059604645D;

    @Getter
    private static YamlConfiguration config;

    public static void load(YamlConfiguration cfg) {
        config = cfg;
        potionThrowMultiplier = getFloat("projectiles.potions.potion-throw-multiplier", 0.5f);
        potionThrowOffset = getFloat("projectiles.potions.potion-throw-offset", -10.0f);
        potionFallSpeed = getFloat("projectiles.potions.potion-fall-speed", 0.05f);

        pearlDamage = getBoolean("projectiles.pearls.pearl-damage", true);
        pearlGravity = getFloat("projectiles.pearls.pearl-gravity", 0.03F);
        pearlSpeed = getFloat("projectiles.pearls.pearl-speed", 1.5F);
        pearlVerticalOffset = getFloat("projectiles.pearls.vertical-offset", 0.0F);

        pearlEnabled = getBoolean("projectiles.pearls.enabled", true);
        pearlHorizontal = getDouble("projectiles.pearls.horizontal", 0.4);
        pearlVertical = getDouble("projectiles.pearls.vertical", 0.4);

        rodEnabled = getBoolean("projectiles.rod.enabled", true);
        rodHorizontal = getDouble("projectiles.rod.horizontal", 0.4);
        rodVertical = getDouble("projectiles.rod.vertical", 0.4);

        bowEnabled = getBoolean("projectiles.bow.enabled", true);
        bowHorizontal = getDouble("projectiles.bow.horizontal", 0.4);
        bowVertical = getDouble("projectiles.bow.vertical", 0.4);

        snowballEnabled = getBoolean("projectiles.snowball.enabled", true);
        snowballHorizontal = getDouble("projectiles.snowball.horizontal", 0.4);
        snowballVertical = getDouble("projectiles.snowball.vertical", 0.4);

        eggEnabled = getBoolean("projectiles.egg.enabled", true);
        eggHorizontal = getDouble("projectiles.egg.horizontal", 0.4);
        eggVertical = getDouble("projectiles.egg.vertical", 0.4);

        explosionEnabled = getBoolean("projectiles.explosion.enabled", true);
        explosionHorizontal = getDouble("projectiles.explosion.horizontal", 0.4);
        explosionVertical = getDouble("projectiles.explosion.vertical", 0.4);

        limitVertical = getBoolean("projectiles.limit-vertical.enabled", false);
        verticalLimit = getDouble("projectiles.limit-vertical.value", 0.4000000059604645D);
    }

    public static boolean getBoolean(String path, boolean def) {
        config.addDefault(path, def);
        return config.getBoolean(path, def);
    }
    public static double getDouble(String path, double def) {
        config.addDefault(path, def);
        return config.getDouble(path, def);
    }

    public static float getFloat(String path, float def) {
        return (float) getDouble(path, def);
    }

    public int getInt(String path, int def) {
        config.addDefault(path, def);
        return config.getInt(path, def);
    }
}
