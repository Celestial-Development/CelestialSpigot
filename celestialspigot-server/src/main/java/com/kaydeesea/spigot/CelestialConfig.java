package com.kaydeesea.spigot;

import com.google.common.base.Throwables;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.logging.Level;

import com.kaydeesea.spigot.hitdetection.LagCompensator;
import com.kaydeesea.spigot.knockback.*;
import com.kaydeesea.spigot.util.YamlCommenter;
import lombok.Getter;
import lombok.Setter;

import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

@Getter
@Setter
public class CelestialConfig {

    private static final String HEADER = "This is the main configuration file for CelestialSpigot.\n"
                                         + "Modify with caution, and make sure you know what you are doing.\n";

    private File configFile;
    private YamlConfiguration config;

    @Setter
    @Getter
    private KnockBackProfile currentKb;
    @Getter
    private Set<KnockBackProfile> kbProfiles = new HashSet<>();


    private YamlCommenter c = new YamlCommenter();

    private String pingCommandSelf;
    private String pingCommandOther;

    private boolean enablePluginsCommand;
    private boolean enableVersionCommand;
    private boolean enableReloadCommand;
    private boolean improvedHitDetection;
    private boolean firePlayerMoveEvent;
    private boolean fireLeftClickAir;
    private boolean fireLeftClickBlock;
    private boolean entityActivation;
    private boolean invalidArmAnimationKick;
    private boolean mobAIEnabled;
    private boolean doChunkUnload;
    private boolean blockOperations;
    private boolean disableJoinMessage;
    private boolean disableLeaveMessage;

    private int hitDelay;

    private float potionThrowMultiplier;
    private float potionThrowOffset;
    private float potionFallSpeed;
    private boolean smoothHealPotions;

    public CelestialConfig() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream is = classLoader.getResourceAsStream("version.properties");
        Properties prop = new Properties();

        try {
            prop.load(is);

            CelestialBridge.version = (String) prop.getOrDefault("version", "Unknown");
        }
        catch (IOException io) {
            io.printStackTrace();
        }
        finally {
            if (is != null) {
                try {
                    is.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        CelestialSpigot.INSTANCE.setLagCompensator(new LagCompensator());
        this.configFile = new File("settings.yml");
        this.config = new YamlConfiguration();

        try {
            config.load(this.configFile);
        } catch (IOException ex) {
            System.out.println("Generating a new settings.yml file.");
        } catch (InvalidConfigurationException ex) {
            Bukkit.getLogger().log(Level.SEVERE, "Could not load settings.yml, please correct your syntax errors", ex);
            throw Throwables.propagate(ex);
        }

        this.config.options().copyDefaults(true);

        this.loadConfig();
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
                        profile.setFriction(this.getDouble(a, 2.0));
                    if (value.equalsIgnoreCase("horizontal"))
                        profile.setHorizontal(this.getDouble(a, 0.35));
                    if (value.equalsIgnoreCase("vertical"))
                        profile.setVertical(this.getDouble(a, 0.35));
                    if (value.equalsIgnoreCase("vertical-limit"))
                        profile.setVerticalLimit(this.getDouble(a, 0.4));
                    if (value.equalsIgnoreCase("extra-horizontal"))
                        profile.setExtraHorizontal(this.getDouble(a, 0.01));
                    if (value.equalsIgnoreCase("extra-vertical"))
                        profile.setExtraVertical(this.getDouble(a, 0.01));
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
                        profile.setFrictionValue(this.getDouble(a, 2.0));
                    if(value.equalsIgnoreCase("horizontal"))
                        profile.setHorizontal(this.getDouble(a, 0.9055));
                    if(value.equalsIgnoreCase("vertical"))
                        profile.setVertical(this.getDouble(a, 0.8835));
                    if (value.equalsIgnoreCase("vertical-limit"))
                        profile.setVerticalLimit(this.getDouble(a, 0.3534));
                    if(value.equalsIgnoreCase("max-range-reduction"))
                        profile.setMaxRangeReduction(this.getDouble(a, 0.4));
                    if(value.equalsIgnoreCase("range-factor"))
                        profile.setRangeFactor(this.getDouble(a, 0.2));
                    if(value.equalsIgnoreCase("start-range-reduction"))
                        profile.setStartRangeReduction(this.getDouble(a, 3.0));
                    if(value.equalsIgnoreCase("w-tap"))
                        profile.setWTap(this.getBoolean(a, false));
                    if(value.equalsIgnoreCase("slowdown-boolean"))
                        profile.setSlowdownBoolean(this.getBoolean(a, false));
                    if(value.equalsIgnoreCase("friction-boolean"))
                        profile.setFriction(this.getBoolean(a, false));
                }
            }

        }

        this.currentKb = this.getKbProfileByName(this.getString("knockback.current", "default"));

        if (this.currentKb == null) {
            this.currentKb = defaultProfile;
        }

        this.pingCommandSelf = this.getString("ping-command-self", "&bYour ping is: &3%ping%");
        this.pingCommandOther = this.getString("ping-command-other", "&b%player%'s ping is: &3%ping%");


        this.enableVersionCommand = this.getBoolean("enable-version-command", true);
        this.enableReloadCommand = this.getBoolean("enable-reload-command", true);
        this.enablePluginsCommand = this.getBoolean("enable-plugins-command", true);
        this.improvedHitDetection = this.getBoolean("improved-hit-detection", true);
        this.firePlayerMoveEvent = this.getBoolean("fire-player-move-event", true);
        this.fireLeftClickAir = this.getBoolean("fire-left-click-air", false);
        this.fireLeftClickBlock = this.getBoolean("fire-left-click-block", false);
        this.entityActivation = this.getBoolean("entity-activation", false);
        this.invalidArmAnimationKick = this.getBoolean("invalid-arm-animation-kick", false);
        this.mobAIEnabled = this.getBoolean("mob-ai", true);
        this.doChunkUnload = this.getBoolean("do-chunk-unload", true);
        this.blockOperations = this.getBoolean("block-operations", false);
        this.disableJoinMessage = this.getBoolean("disable-join-message", true);
        this.disableLeaveMessage = this.getBoolean("disable-leave-message", true);

        this.hitDelay = this.getInt("hit-delay", 20);
        this.potionThrowMultiplier = this.getFloat("potions.potion-throw-multiplier", 0.5f);
        this.potionThrowOffset = this.getFloat("potions.potion-throw-offset", -10.0f);
        this.potionFallSpeed = this.getFloat("potions.potion-fall-speed", 0.05f);

        this.smoothHealPotions = this.getBoolean("potions.smooth-heal-potions", true);

        CelestialBridge.disableOpPermissions = this.getBoolean("disable-op", false);

        save();
    }
    public void loadComments() {
        c.setHeader(HEADER);
        c.addComment("knockback.profiles", "KnockBack profiles, you can create profiles in-game by /knockback create <name> <type>");
        c.addComment("knockback.current", "Currently selected knockback profile");

        // Add comments for mob AI setting
        c.addComment("mob-ai", "Setting to enable or disable mob artificial intelligence");

        // Add comments for command-related settings
        c.addComment("enable-version-command", "Enable or disable the version command");
        c.addComment("enable-reload-command", "Enable or disable the reload command");
        c.addComment("enable-plugins-command", "Enable or disable the plugins command");

        // Add comments for player event-related settings
        c.addComment("fire-player-move-event", "Enable firing events when players move");
        c.addComment("fire-left-click-air", "Enable firing events when players left-click air");
        c.addComment("fire-left-click-block", "Enable firing events when players left-click blocks");

        // Add comments for entity and game settings
        c.addComment("entity-activation", "Enable or disable entity activation rules");
        c.addComment("invalid-arm-animation-kick", "Kick players for invalid arm animations");
        c.addComment("do-chunk-unload", "Enable or disable chunk unloading");
        c.addComment("block-operations", "Enable or disable block operations");
        c.addComment("disable-join-message", "Enable or disable join messages");
        c.addComment("disable-leave-message", "Enable or disable leave messages");

        c.addComment("hit-delay", "Change the hit Delay (ticks)");

        // Add comments for potion-related settings
        c.addComment("potions.potion-throw-multiplier", "Set the multiplier for potion throwing speed");
        c.addComment("potions.potion-throw-offset", "Set the offset angle for potion throws");
        c.addComment("potions.potion-fall-speed", "Set the falling speed for potions");
        c.addComment("potions.smooth-heal-potions", "Enable smooth healing effects for potions");

        // Add comments for CelestialBridge settings
        c.addComment("disable-op", "Enable or disable operator permissions");
    }

    public KnockBackProfile getKbProfileByName(String name) {
        for (KnockBackProfile profile : this.kbProfiles) {
            if (profile.getName().equalsIgnoreCase(name)) {
                return profile;
            }
        }

        return null;
    }

    public void save() {
        loadComments();
        try {
            this.config.save(this.configFile);
            this.c.saveComments(this.configFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void set(String path, Object val) {
        this.config.set(path, val);

        try {
            this.config.save(this.configFile);
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
