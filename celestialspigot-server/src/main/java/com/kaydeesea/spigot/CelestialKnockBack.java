package com.kaydeesea.spigot;

import com.google.common.base.Throwables;
import com.kaydeesea.spigot.knockback.KnockBackProfile;
import com.kaydeesea.spigot.knockback.NormalKnockbackProfile;
import com.kaydeesea.spigot.knockback.ProfileType;
import com.kaydeesea.spigot.knockback.impl.BedWarsTypeKnockbackProfile;
import com.kaydeesea.spigot.knockback.impl.DetailedTypeKnockbackProfile;
import com.kaydeesea.spigot.knockback.impl.NormalTypeKnockbackProfile;
import com.kaydeesea.spigot.util.YamlCommenter;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

public class CelestialKnockBack {
    @Setter
    @Getter
    private KnockBackProfile currentKb;


    @Getter
    private Set<KnockBackProfile> kbProfiles = new HashSet<>();

    private static final String HEADER =
            "This is the main configuration file for knockback.\n"
                    + "All modifiers have their own specific settings defined below.\n"
                    + "Each setting allows you to customize the knockback behavior in detail.\n"
                    + "Please refer to the documentation for detailed explanations of each parameter.\n";

    private final File knockbackFile;

    private final YamlCommenter c;

    private final YamlConfiguration config;

    public CelestialKnockBack() {
        knockbackFile = new File("knockback.yml");
        config = new YamlConfiguration();
        c = new YamlCommenter();
        try {
            config.load(this.knockbackFile);
        } catch (IOException ex) {
            System.out.println("Generating a new knockback.yml file.");
        } catch (InvalidConfigurationException ex) {
            Bukkit.getLogger().log(Level.SEVERE, "Could not load knockback.yml, please correct your syntax errors", ex);
            throw Throwables.propagate(ex);
        }
        this.config.options().copyDefaults(true);

        this.loadConfig();

    }

    public KnockBackProfile getKbProfileByName(String name) {
        for (KnockBackProfile profile : this.kbProfiles) {
            if (profile.getName().equalsIgnoreCase(name)) {
                return profile;
            }
        }

        return null;
    }

    private void loadConfig() {
        final NormalKnockbackProfile defaultProfile = new NormalTypeKnockbackProfile("default");

        this.kbProfiles = new HashSet<>();
        this.kbProfiles.add(defaultProfile);
        ArrayList<String> profiles = new ArrayList<>(this.getKeys("knockback.profiles"));
        if(profiles.isEmpty()) {
            profiles.add("default");
        }
        for (String key : profiles) {
            final String path = "knockback.profiles." + key;
            ProfileType type = ProfileType.NORMAL;
            try {
                type = ProfileType.valueOf(this.getString(path+".type", "NORMAL"));
            } catch (Exception ex) {
                System.out.println("No profile type set for profile "+key);
            }
            if(type == ProfileType.NORMAL) {
                NormalTypeKnockbackProfile profile = (NormalTypeKnockbackProfile) getKbProfileByName(key);

                if (profile == null) {
                    profile = new NormalTypeKnockbackProfile(key);
                    this.kbProfiles.add(profile);
                }
                for (String value : profile.getValues()) {
                    String a = path + "." + value;
                    if (value.equalsIgnoreCase("friction"))
                        profile.setFriction(this.getDouble(a, profile.getFriction()));
                    if (value.equalsIgnoreCase("horizontal"))
                        profile.setHorizontal(this.getDouble(a, profile.getHorizontal()));
                    if (value.equalsIgnoreCase("vertical"))
                        profile.setVertical(this.getDouble(a, profile.getVertical()));
                    if (value.equalsIgnoreCase("vertical-limit"))
                        profile.setVerticalLimit(this.getDouble(a, profile.getVerticalLimit()));
                    if (value.equalsIgnoreCase("extra-horizontal"))
                        profile.setExtraHorizontal(this.getDouble(a, profile.getExtraHorizontal()));
                    if (value.equalsIgnoreCase("extra-vertical"))
                        profile.setExtraVertical(this.getDouble(a, profile.getExtraVertical()));
                }
            } else if(type.equals(ProfileType.BEDWARS)) {
                BedWarsTypeKnockbackProfile profile = (BedWarsTypeKnockbackProfile) getKbProfileByName(key);

                if (profile == null) {
                    profile = new BedWarsTypeKnockbackProfile(key);
                    this.kbProfiles.add(profile);
                }
                for (String value : profile.getValues()) {
                    String a = path + "." + value;
                    if(value.equalsIgnoreCase("friction"))
                        profile.setFrictionValue(this.getDouble(a, profile.getFrictionValue()));
                    if(value.equalsIgnoreCase("horizontal"))
                        profile.setHorizontal(this.getDouble(a, profile.getHorizontal()));
                    if(value.equalsIgnoreCase("vertical"))
                        profile.setVertical(this.getDouble(a, profile.getVertical()));
                    if (value.equalsIgnoreCase("vertical-limit"))
                        profile.setVerticalLimit(this.getDouble(a, profile.getVerticalLimit()));
                    if(value.equalsIgnoreCase("max-range-reduction"))
                        profile.setMaxRangeReduction(this.getDouble(a, profile.getMaxRangeReduction()));
                    if(value.equalsIgnoreCase("range-factor"))
                        profile.setRangeFactor(this.getDouble(a, profile.getRangeFactor()));
                    if(value.equalsIgnoreCase("start-range-reduction"))
                        profile.setStartRangeReduction(this.getDouble(a, profile.getStartRangeReduction()));
                    if(value.equalsIgnoreCase("w-tap"))
                        profile.setWTap(this.getBoolean(a, profile.isWTap()));
                    if(value.equalsIgnoreCase("slowdown-boolean"))
                        profile.setSlowdownBoolean(this.getBoolean(a, profile.isSlowdownBoolean()));
                    if(value.equalsIgnoreCase("friction-boolean"))
                        profile.setFriction(this.getBoolean(a, profile.isFriction()));
                }
            } else if (type.equals(ProfileType.DETAILED)) {
                DetailedTypeKnockbackProfile profile = (DetailedTypeKnockbackProfile) getKbProfileByName(key);

                if (profile == null) {
                    profile = new DetailedTypeKnockbackProfile(key);
                    this.kbProfiles.add(profile);
                }

                for (String value : profile.getValues()) {
                    String configPath = path + "." + value;

                    if (value.equalsIgnoreCase("friction-horizontal"))
                        profile.setFrictionH(this.getDouble(configPath, profile.getFrictionH()));
                    else if (value.equalsIgnoreCase("friction-vertical"))
                        profile.setFrictionY(this.getDouble(configPath, profile.getFrictionY()));
                    else if (value.equalsIgnoreCase("horizontal"))
                        profile.setHorizontal(this.getDouble(configPath, profile.getHorizontal()));
                    else if (value.equalsIgnoreCase("vertical"))
                        profile.setVertical(this.getDouble(configPath, profile.getVertical()));
                    else if (value.equalsIgnoreCase("vertical-limit"))
                        profile.setVerticalLimit(this.getDouble(configPath, profile.getVerticalLimit()));
                    else if (value.equalsIgnoreCase("ground-horizontal"))
                        profile.setGroundH(this.getDouble(configPath, profile.getGroundH()));
                    else if (value.equalsIgnoreCase("ground-vertical"))
                        profile.setGroundV(this.getDouble(configPath, profile.getGroundV()));
                    else if (value.equalsIgnoreCase("sprint-horizontal"))
                        profile.setSprintH(this.getDouble(configPath, profile.getSprintH()));
                    else if (value.equalsIgnoreCase("sprint-vertical"))
                        profile.setSprintV(this.getDouble(configPath, profile.getSprintV()));
                    else if (value.equalsIgnoreCase("slowdown"))
                        profile.setSlowdown(this.getDouble(configPath, profile.getSlowdown()));
                    else if (value.equalsIgnoreCase("enable-vertical-limit"))
                        profile.setEnableVerticalLimit(this.getBoolean(configPath, profile.isEnableVerticalLimit()));
                    else if (value.equalsIgnoreCase("stop-sprint"))
                        profile.setStopSprint(this.getBoolean(configPath, profile.isStopSprint()));
                    else if (value.equalsIgnoreCase("inherit-horizontal"))
                        profile.setInheritH(this.getBoolean(configPath, profile.isInheritH()));
                    else if (value.equalsIgnoreCase("inherit-vertical"))
                        profile.setInheritY(this.getBoolean(configPath, profile.isInheritY()));
                    else if (value.equalsIgnoreCase("inherit-horizontal-value"))
                        profile.setInheritHValue(this.getDouble(configPath, profile.getInheritHValue()));
                    else if (value.equalsIgnoreCase("inherit-vertical-value"))
                        profile.setInheritYValue(this.getDouble(configPath, profile.getInheritYValue()));
                }
            }


        }

        this.currentKb = this.getKbProfileByName(this.getString("knockback.current", "default"));

        if (this.currentKb == null) {
            this.currentKb = defaultProfile;
        }

        save();
    }
    public void save() {
        c.setHeader(HEADER);
        c.addComment("knockback.profiles", "KnockBack profiles, you can create profiles in-game by /knockback create <name> <type>");
        c.addComment("knockback.current", "Currently selected knockback profile");
        try {
            this.config.save(this.knockbackFile);
            this.c.saveComments(this.knockbackFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public void set(String path, Object val) {
        this.config.set(path, val);

        try {
            this.config.save(this.knockbackFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Set<String> getKeys(String path) {
        if (!this.config.isConfigurationSection(path)) {
            this.config.createSection(path);
            return new HashSet<>();
        }

        return this.config.getConfigurationSection(path).getKeys(false);
    }

    public boolean getBoolean(String path, boolean def) {
        this.config.addDefault(path, def);
        return this.config.getBoolean(path, this.config.getBoolean(path));
    }

    public double getDouble(String path, double def) {
        this.config.addDefault(path, def);
        return this.config.getDouble(path, this.config.getDouble(path));
    }

    public float getFloat(String path, float def) {
        return (float) this.getDouble(path, def);
    }

    public int getInt(String path, int def) {
        this.config.addDefault(path, def);
        return config.getInt(path, this.config.getInt(path));
    }

    public <T> List getList(String path, T def) {
        this.config.addDefault(path, def);
        return this.config.getList(path, this.config.getList(path));
    }

    public String getString(String path, String def) {
        this.config.addDefault(path, def);
        return this.config.getString(path, this.config.getString(path));
    }


}
