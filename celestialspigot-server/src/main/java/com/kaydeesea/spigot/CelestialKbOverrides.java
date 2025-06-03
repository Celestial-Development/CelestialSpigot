package com.kaydeesea.spigot;

import com.kaydeesea.spigot.util.YamlCommenter;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class CelestialKbOverrides {

    private final File file;
    private final YamlCommenter commenter;

    private YamlConfiguration config;

    private static final String HEADER =
            "This is the main configuration file for CelestialSpigot's knockback binds.\n\n" +
                    "How does it work?\n\n" +
                    "Simply set up your bind under the section named 'binds' in this format:\n" +
                    "world: knockback profile name\n\n" +
                    "The knockback profile will be automatically applied to people who join that world.\n" +
                    "Once they leave, they will be left with either current knockback (no bind to new world),\n" +
                    "or to another knockback profile set to the world the player is going to.\n";

    @Getter
    private final Map<String, String> binds = new HashMap<>();

    public CelestialKbOverrides() {
        this.file = new File("kb-per-world.yml");
        this.commenter = new YamlCommenter();
        load();
    }

    public void load() {
        try {
            if (!file.exists() && file.createNewFile()) {
                Bukkit.getLogger().log(Level.INFO, "Successfully created " + file.getName());
                Bukkit.getLogger().log(Level.INFO, "This file allows you to change knockback settings based on world.");
            }
            config = YamlConfiguration.loadConfiguration(file);
        } catch (IOException e) {
            Bukkit.getLogger().log(Level.SEVERE, "Couldn't create or load " + file.getName());
            e.printStackTrace();
            Bukkit.getServer().shutdown();
            return;
        }

        ConfigurationSection section = config.getConfigurationSection("binds");
        if (section != null) {
            for (String world : section.getKeys(false)) {
                String kbProfile = section.getString(world);
                if (kbProfile != null) {
                    binds.put(world.toLowerCase(), kbProfile);
                }
            }
        } else {
            // Add defaults
            binds.put("world1", "custom-kb-world-1");
            binds.put("bedwars", "custom-bedwars");
            config.set("binds.world1", "custom-kb-world-1");
            config.set("binds.bedwars", "custom-bedwars");
        }

        save();
    }

    public void save() {
        new Thread(() -> {
            try {
                for (Map.Entry<String, String> entry : binds.entrySet()) {
                    config.set("binds." + entry.getKey(), entry.getValue());
                }
                config.save(file);

                commenter.setHeader(HEADER);
                commenter.saveComments(file);
            } catch (IOException e) {
                Bukkit.getLogger().log(Level.SEVERE, "Failed to save " + file.getName());
                e.printStackTrace();
            }
        }).start();
    }

    public String getProfileForWorld(String worldName) {
        return binds.getOrDefault(worldName.toLowerCase(), null);
    }

    public void setProfileForWorld(String worldName, String profileName) {
        binds.put(worldName.toLowerCase(), profileName);
    }

    public void removeProfileForWorld(String worldName) {
        binds.remove(worldName.toLowerCase());
    }
}
