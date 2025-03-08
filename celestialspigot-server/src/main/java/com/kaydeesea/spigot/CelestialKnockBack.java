package com.kaydeesea.spigot;

import com.google.common.base.Throwables;
import com.kaydeesea.spigot.knockback.KnockBackProfile;
import com.kaydeesea.spigot.knockback.NormalKnockbackProfile;
import com.kaydeesea.spigot.knockback.ProfileType;
import com.kaydeesea.spigot.knockback.impl.BedWarsTypeKnockbackProfile;
import com.kaydeesea.spigot.knockback.impl.DetailedTypeKnockbackProfile;
import com.kaydeesea.spigot.knockback.impl.FoxTypeKnockbackProfile;
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


    @Getter
    private final YamlConfiguration config;

    public CelestialKnockBack() {
        knockbackFile = new File("knockback.yml");
        config = new YamlConfiguration();
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

    public boolean reload() {
        try {
            config.load(this.knockbackFile);
        } catch (IOException ignored) {

        } catch (InvalidConfigurationException ex) {
            Bukkit.getLogger().log(Level.SEVERE, "Could not load knockback.yml, please correct your syntax errors", ex);
            return false;
        }
        this.loadConfig();
        return true;
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
        if(!this.config.isConfigurationSection("knockback.profiles")) {
            this.config.createSection("knockback.profiles");
        }
        ArrayList<String> profiles = new ArrayList<>(this.config.getConfigurationSection("knockback.profiles").getKeys(false));
        if(profiles.isEmpty()) {
            profiles.add("default");
        }
        for (String key : profiles) {
            final String path = "knockback.profiles." + key;
            ProfileType type = ProfileType.NORMAL;
            try {
                type = ProfileType.valueOf(this.config.getString(path+".type", "NORMAL"));
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
                        profile.setFriction(this.config.getDouble(a, profile.getFriction()));
                    else if (value.equalsIgnoreCase("horizontal"))
                        profile.setHorizontal(this.config.getDouble(a, profile.getHorizontal()));
                    else if (value.equalsIgnoreCase("vertical"))
                        profile.setVertical(this.config.getDouble(a, profile.getVertical()));
                    else if (value.equalsIgnoreCase("vertical-limit"))
                        profile.setVerticalLimit(this.config.getDouble(a, profile.getVerticalLimit()));
                    else if (value.equalsIgnoreCase("extra-horizontal"))
                        profile.setExtraHorizontal(this.config.getDouble(a, profile.getExtraHorizontal()));
                    else if (value.equalsIgnoreCase("extra-vertical"))
                        profile.setExtraVertical(this.config.getDouble(a, profile.getExtraVertical()));
                    else if (value.equalsIgnoreCase("hit-delay"))
                        profile.setHitDelay(this.config.getInt(a, profile.getHitDelay()));
                }
            }
            else if(type.equals(ProfileType.BEDWARS)) {
                BedWarsTypeKnockbackProfile profile = (BedWarsTypeKnockbackProfile) getKbProfileByName(key);

                if (profile == null) {
                    profile = new BedWarsTypeKnockbackProfile(key);
                    this.kbProfiles.add(profile);
                }
                for (String value : profile.getValues()) {
                    String a = path + "." + value;
                    if(value.equalsIgnoreCase("friction"))
                        profile.setFrictionValue(this.config.getDouble(a, profile.getFrictionValue()));
                    else if(value.equalsIgnoreCase("horizontal"))
                        profile.setHorizontal(this.config.getDouble(a, profile.getHorizontal()));
                    else if(value.equalsIgnoreCase("vertical"))
                        profile.setVertical(this.config.getDouble(a, profile.getVertical()));
                    else if (value.equalsIgnoreCase("vertical-limit"))
                        profile.setVerticalLimit(this.config.getDouble(a, profile.getVerticalLimit()));
                    else if(value.equalsIgnoreCase("max-range-reduction"))
                        profile.setMaxRangeReduction(this.config.getDouble(a, profile.getMaxRangeReduction()));
                    else if(value.equalsIgnoreCase("range-factor"))
                        profile.setRangeFactor(this.config.getDouble(a, profile.getRangeFactor()));
                    else if(value.equalsIgnoreCase("start-range-reduction"))
                        profile.setStartRangeReduction(this.config.getDouble(a, profile.getStartRangeReduction()));
                    else if(value.equalsIgnoreCase("w-tap"))
                        profile.setWTap(this.config.getBoolean(a, profile.isWTap()));
                    else if(value.equalsIgnoreCase("slowdown-boolean"))
                        profile.setSlowdownBoolean(this.config.getBoolean(a, profile.isSlowdownBoolean()));
                    else if(value.equalsIgnoreCase("friction-boolean"))
                        profile.setFriction(this.config.getBoolean(a, profile.isFriction()));
                    else if (value.equalsIgnoreCase("hit-delay"))
                        profile.setHitDelay(this.config.getInt(a, profile.getHitDelay()));
                    else if (value.equalsIgnoreCase("slowdown-value")) {
                        profile.setSlowdownValue(this.config.getDouble(a, profile.getSlowdownValue()));
                    }
                }
            }
            else if (type.equals(ProfileType.DETAILED)) {
                DetailedTypeKnockbackProfile profile = (DetailedTypeKnockbackProfile) getKbProfileByName(key);

                if (profile == null) {
                    profile = new DetailedTypeKnockbackProfile(key);
                    this.kbProfiles.add(profile);
                }

                for (String value : profile.getValues()) {
                    String configPath = path + "." + value;

                    if (value.equalsIgnoreCase("friction-horizontal"))
                        profile.setFrictionH(this.config.getDouble(configPath, profile.getFrictionH()));
                    else if (value.equalsIgnoreCase("friction-vertical"))
                        profile.setFrictionY(this.config.getDouble(configPath, profile.getFrictionY()));
                    else if (value.equalsIgnoreCase("horizontal"))
                        profile.setHorizontal(this.config.getDouble(configPath, profile.getHorizontal()));
                    else if (value.equalsIgnoreCase("vertical"))
                        profile.setVertical(this.config.getDouble(configPath, profile.getVertical()));
                    else if (value.equalsIgnoreCase("vertical-limit"))
                        profile.setVerticalLimit(this.config.getDouble(configPath, profile.getVerticalLimit()));
                    else if (value.equalsIgnoreCase("ground-horizontal"))
                        profile.setGroundH(this.config.getDouble(configPath, profile.getGroundH()));
                    else if (value.equalsIgnoreCase("ground-vertical"))
                        profile.setGroundV(this.config.getDouble(configPath, profile.getGroundV()));
                    else if (value.equalsIgnoreCase("sprint-horizontal"))
                        profile.setSprintH(this.config.getDouble(configPath, profile.getSprintH()));
                    else if (value.equalsIgnoreCase("sprint-vertical"))
                        profile.setSprintV(this.config.getDouble(configPath, profile.getSprintV()));
                    else if (value.equalsIgnoreCase("slowdown"))
                        profile.setSlowdown(this.config.getDouble(configPath, profile.getSlowdown()));
                    else if (value.equalsIgnoreCase("enable-vertical-limit"))
                        profile.setEnableVerticalLimit(this.config.getBoolean(configPath, profile.isEnableVerticalLimit()));
                    else if (value.equalsIgnoreCase("stop-sprint"))
                        profile.setStopSprint(this.config.getBoolean(configPath, profile.isStopSprint()));
                    else if (value.equalsIgnoreCase("inherit-horizontal"))
                        profile.setInheritH(this.config.getBoolean(configPath, profile.isInheritH()));
                    else if (value.equalsIgnoreCase("inherit-vertical"))
                        profile.setInheritY(this.config.getBoolean(configPath, profile.isInheritY()));
                    else if (value.equalsIgnoreCase("inherit-horizontal-value"))
                        profile.setInheritHValue(this.config.getDouble(configPath, profile.getInheritHValue()));
                    else if (value.equalsIgnoreCase("inherit-vertical-value"))
                        profile.setInheritYValue(this.config.getDouble(configPath, profile.getInheritYValue()));
                    else if (value.equalsIgnoreCase("hit-delay"))
                        profile.setHitDelay(this.config.getInt(configPath, profile.getHitDelay()));
                }
            }
            else if (type.equals(ProfileType.FOX)) {
                FoxTypeKnockbackProfile profile = (FoxTypeKnockbackProfile) getKbProfileByName(key);

                if (profile == null) {
                    profile = new FoxTypeKnockbackProfile(key);
                    this.kbProfiles.add(profile);
                }

                for (String value : profile.getValues()) {
                    String configPath = path + "." + value;

                    if(value.equalsIgnoreCase("1-point-1-kb"))
                        profile.setOnePoint1kb(this.config.getBoolean(configPath, profile.isOnePoint1kb()));
                    else if (value.equalsIgnoreCase("horizontal"))
                        profile.setHorizontal(this.config.getDouble(configPath, profile.getHorizontal()));
                    else if (value.equalsIgnoreCase("vertical"))
                        profile.setVertical(this.config.getDouble(configPath, profile.getVertical()));
                    else if (value.equalsIgnoreCase("vertical-limit"))
                        profile.setVerticalLimit(this.config.getDouble(configPath, profile.getVerticalLimit()));
                    else if (value.equalsIgnoreCase("ground-horizontal"))
                        profile.setGroundH(this.config.getDouble(configPath, profile.getGroundH()));
                    else if (value.equalsIgnoreCase("ground-vertical"))
                        profile.setGroundV(this.config.getDouble(configPath, profile.getGroundV()));
                    else if (value.equalsIgnoreCase("sprint-horizontal"))
                        profile.setSprintH(this.config.getDouble(configPath, profile.getSprintH()));
                    else if (value.equalsIgnoreCase("sprint-vertical"))
                        profile.setSprintV(this.config.getDouble(configPath, profile.getSprintV()));
                    else if (value.equalsIgnoreCase("slowdown"))
                        profile.setSlowdown(this.config.getDouble(configPath, profile.getSlowdown()));
                    else if (value.equalsIgnoreCase("enable-vertical-limit"))
                        profile.setEnableVerticalLimit(this.config.getBoolean(configPath, profile.isEnableVerticalLimit()));
                    else if (value.equalsIgnoreCase("stop-sprint"))
                        profile.setStopSprint(this.config.getBoolean(configPath, profile.isStopSprint()));
                    else if (value.equalsIgnoreCase("inherit-horizontal"))
                        profile.setInheritH(this.config.getBoolean(configPath, profile.isInheritH()));
                    else if (value.equalsIgnoreCase("inherit-vertical"))
                        profile.setInheritY(this.config.getBoolean(configPath, profile.isInheritY()));
                    else if (value.equalsIgnoreCase("inherit-horizontal-value"))
                        profile.setInheritHValue(this.config.getDouble(configPath, profile.getInheritHValue()));
                    else if (value.equalsIgnoreCase("inherit-vertical-value"))
                        profile.setInheritYValue(this.config.getDouble(configPath, profile.getInheritYValue()));
                    else if (value.equalsIgnoreCase("hit-delay"))
                        profile.setHitDelay(this.config.getInt(configPath, profile.getHitDelay()));
                }
            }

        }

        this.currentKb = this.getKbProfileByName(this.config.getString("knockback.current", "default"));

        if (this.currentKb == null) {
            this.currentKb = defaultProfile;
            this.config.set("knockback.current", "default");
        }

        save();
    }
    public void save() {
        try {
            this.config.save(this.knockbackFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    


}
