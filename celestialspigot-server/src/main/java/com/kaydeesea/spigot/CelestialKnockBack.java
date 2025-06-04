package com.kaydeesea.spigot;

import com.google.common.base.Throwables;
import com.kaydeesea.spigot.knockback.KnockBackProfile;
import com.kaydeesea.spigot.knockback.NormalKnockbackProfile;
import com.kaydeesea.spigot.knockback.ProfileType;
import com.kaydeesea.spigot.knockback.impl.BedWarsTypeKnockbackProfile;
import com.kaydeesea.spigot.knockback.impl.ComboTypeKnockbackProfile;
import com.kaydeesea.spigot.knockback.impl.DetailedTypeKnockbackProfile;
import com.kaydeesea.spigot.knockback.impl.NormalTypeKnockbackProfile;
import com.kaydeesea.spigot.util.YamlCommenter;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

public class CelestialKnockBack {
    @Getter
    private KnockBackProfile currentKb;
    private final YamlCommenter c;


    public void setCurrentKb(KnockBackProfile currentKb) {
        getConfig().set("knockback.current", currentKb.getName());
        for (Player onlinePlayer : Bukkit.getServer().getOnlinePlayers()) {
            if(onlinePlayer.getKnockbackProfile() == this.currentKb) onlinePlayer.setKnockbackProfile(currentKb);
        }
        save();

        this.currentKb = currentKb;
    }
    public void createKB(KnockBackProfile profile) {
        getKbProfiles().add(profile);
        profile.save();
    }
    public void deleteKB(KnockBackProfile knockBackProfile) {
        if(getCurrentKb() == knockBackProfile) return;
        getKbProfiles().remove(knockBackProfile);
        for (String value : knockBackProfile.getValues()) {
            getConfig().set("knockback.profiles." + knockBackProfile.getName()+"."+value, null);
        }
        getConfig().set("knockback.profiles." + knockBackProfile.getName(), null);
        save();
    }

    @Getter
    private Set<KnockBackProfile> kbProfiles = new HashSet<>();

    private static final String HEADER =
            "This is the main configuration file for knockback.\n"
                    + "All modifiers have their own specific settings defined below.\n"
                    + "Each setting allows you to customize the knockback behavior in detail.\n"
                    + "Please refer to the documentation for detailed explanations of each parameter.\n";

    private final File knockbackFile;


    @Getter
    private YamlConfiguration config;

    public CelestialKnockBack() {
        knockbackFile = new File("knockback.yml");
        config = new YamlConfiguration();
        c = new YamlCommenter();
        if(!knockbackFile.exists()) {
            try {
                this.knockbackFile.createNewFile();
            } catch (Exception ex) {
                System.out.println("Could not load knockback.yml, please correct your syntax errors");
                ex.printStackTrace();
                throw Throwables.propagate(ex);
            }
        }
        this.config = YamlConfiguration.loadConfiguration(knockbackFile);

        this.config.options().copyDefaults(true);

        new Thread(this::loadConfig).start();
    }

    public boolean reload() {
        try {
            config.load(this.knockbackFile);
        } catch (IOException ignored) {
            return false;
        } catch (InvalidConfigurationException ex) {
            System.out.println("Could not load knockback.yml, please correct your syntax errors");
            ex.printStackTrace();
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

        this.c.setHeader(HEADER);

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

                for (NormalTypeKnockbackProfile.NormalValues value : NormalTypeKnockbackProfile.NormalValues.values()) {
                    String a = path + "." + value.getKey();
                    if (value.isDouble()) {
                        profile.setValueByKey(value, this.config.getDouble(a, (Double) profile.getValueByKey(value)));
                    } else if (value.isInteger()) {
                        profile.setValueByKey(value, this.config.getInt(a, (Integer) profile.getValueByKey(value)));
                    }
                }
                if(key.equalsIgnoreCase("default")) {
                    profile.save();
                }
            }
            else if(type.equals(ProfileType.COMBO)) {
                ComboTypeKnockbackProfile profile = (ComboTypeKnockbackProfile) getKbProfileByName(key);

                if (profile == null) {
                    profile = new ComboTypeKnockbackProfile(key);
                    this.kbProfiles.add(profile);
                }
                for (ComboTypeKnockbackProfile.ComboValues value : ComboTypeKnockbackProfile.ComboValues.values()) {
                    String a = path + "." + value.getKey();
                    if(value.isDouble()) {
                        profile.setValueByKey(value, this.config.getDouble(a, (Double) profile.getValueByKey(value)));
                    } else if(value.isInteger()) {
                        profile.setValueByKey(value, this.config.getInt(a, (Integer) profile.getValueByKey(value)));
                    }
                }
            }
            else if(type.equals(ProfileType.BEDWARS)) {
                BedWarsTypeKnockbackProfile profile = (BedWarsTypeKnockbackProfile) getKbProfileByName(key);

                if (profile == null) {
                    profile = new BedWarsTypeKnockbackProfile(key);
                    this.kbProfiles.add(profile);
                }
                for (BedWarsTypeKnockbackProfile.BedWarsValues value : BedWarsTypeKnockbackProfile.BedWarsValues.values()) {
                    String a = path + "." + value.getKey();
                    if(value.isBoolean()) {
                        profile.setValueByKey(value, this.config.getBoolean(a, (Boolean) profile.getValueByKey(value)));
                    } else if(value.isDouble()) {
                        profile.setValueByKey(value, this.config.getDouble(a, (Double) profile.getValueByKey(value)));
                    } else if(value.isInteger()) {
                        profile.setValueByKey(value, this.config.getInt(a, (Integer) profile.getValueByKey(value)));
                    }
                }
            }
            else if (type.equals(ProfileType.DETAILED)) {
                DetailedTypeKnockbackProfile profile = (DetailedTypeKnockbackProfile) getKbProfileByName(key);

                if (profile == null) {
                    profile = new DetailedTypeKnockbackProfile(key);
                    this.kbProfiles.add(profile);
                }

                for (DetailedTypeKnockbackProfile.DetailedValues value : DetailedTypeKnockbackProfile.DetailedValues.values()) {
                    String a = path + "." + value.getKey();
                    if (value.isBoolean()) {
                        profile.setValueByKey(value, this.config.getBoolean(a, (Boolean) profile.getValueByKey(value)));
                    } else if (value.isDouble()) {
                        profile.setValueByKey(value, this.config.getDouble(a, (Double) profile.getValueByKey(value)));
                    } else if (value.isInteger()) {
                        profile.setValueByKey(value, this.config.getInt(a, (Integer) profile.getValueByKey(value)));
                    }
                }
            }
        }

        this.currentKb = this.getKbProfileByName(this.config.getString("knockback.current", "default"));

        if (this.currentKb == null) {
            setCurrentKb(defaultProfile);
        }

        save();
    }
    public void save() {
        new Thread(() -> {
            try {
                this.config.save(this.knockbackFile);
                this.c.saveComments(this.knockbackFile);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }


    


}
