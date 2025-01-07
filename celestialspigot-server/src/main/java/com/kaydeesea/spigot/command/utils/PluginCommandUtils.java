package com.kaydeesea.spigot.command.utils;


import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.event.Event;
import org.bukkit.plugin.*;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URLClassLoader;
import java.util.*;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Utilities for managing plugins.
 *
 * @author rylinaux
 */
public class PluginCommandUtils implements PluginManager {

    private final Class<?> pluginClassLoader;
    private final Field pluginClassLoaderPlugin;

    public PluginCommandUtils() {
        try {
            this.pluginClassLoader = Class.forName("org.bukkit.plugin.java.PluginClassLoader");
            this.pluginClassLoaderPlugin = this.pluginClassLoader.getDeclaredField("plugin");
            this.pluginClassLoaderPlugin.setAccessible(true);
        } catch (ClassNotFoundException | NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Enable a plugin.
     *
     * @param plugin the plugin to enable
     */
    @Override
    public void enable(Plugin plugin) {
        if (plugin != null && !plugin.isEnabled()) Bukkit.getPluginManager().enablePlugin(plugin);
    }

    /**
     * Enable all plugins.
     */
    @Override
    public void enableAll() {
        for (Plugin plugin : Bukkit.getPluginManager().getPlugins())
            this.enable(plugin);
    }

    /**
     * Disable a plugin.
     *
     * @param plugin the plugin to disable
     */
    @Override
    public void disable(Plugin plugin) {
        if (plugin != null && plugin.isEnabled()) Bukkit.getPluginManager().disablePlugin(plugin);
    }

    /**
     * Disable all plugins.
     */
    @Override
    public void disableAll() {
        for (Plugin plugin : Bukkit.getPluginManager().getPlugins())
            this.disable(plugin);
    }

    /**
     * Returns the formatted name of the plugin.
     *
     * @param plugin the plugin to format
     * @return the formatted name
     */
    @Override
    public String getFormattedName(Plugin plugin) {
        return this.getFormattedName(plugin, false);
    }

    /**
     * Returns the formatted name of the plugin.
     *
     * @param plugin          the plugin to format
     * @param includeVersions whether to include the version
     * @return the formatted name
     */
    @Override
    public String getFormattedName(Plugin plugin, boolean includeVersions) {
        ChatColor color = plugin.isEnabled() ? ChatColor.GREEN : ChatColor.RED;
        String pluginName = color + plugin.getName();
        if (includeVersions) pluginName += " (" + plugin.getDescription().getVersion() + ")";
        return pluginName;
    }
    
    /**
     * Returns a plugin from a String.
     *
     * @param name the name of the plugin
     * @return the plugin
     */
    @Override
    public Plugin getPluginByName(String name) {
        for (Plugin plugin : Bukkit.getPluginManager().getPlugins())
            if (name.equalsIgnoreCase(plugin.getName())) return plugin;
        return null;
    }

    /**
     * Returns a List of plugin names.
     *
     * @return list of plugin names
     */
    @Override
    public List<String> getPluginNames(boolean fullName) {
        List<String> plugins = new ArrayList<>();
        for (Plugin plugin : Bukkit.getPluginManager().getPlugins())
            plugins.add(fullName ? plugin.getDescription().getFullName() : plugin.getName());
        return plugins;
    }

    /**
     * Returns a List of disabled plugin names.
     *
     * @return list of disabled plugin names
     */
    @Override
    public List<String> getDisabledPluginNames(boolean fullName) {
        List<String> plugins = new ArrayList<>();
        for (Plugin plugin : Bukkit.getPluginManager().getPlugins())
            if (!plugin.isEnabled())
                plugins.add(fullName ? plugin.getDescription().getFullName() : plugin.getName());
        return plugins;
    }

    /**
     * Returns a List of enabled plugin names.
     *
     * @return list of enabled plugin names
     */
    @Override
    public List<String> getEnabledPluginNames(boolean fullName) {
        List<String> plugins = new ArrayList<>();
        for (Plugin plugin : Bukkit.getPluginManager().getPlugins())
            if (plugin.isEnabled())
                plugins.add(fullName ? plugin.getDescription().getFullName() : plugin.getName());
        return plugins;
    }

    /**
     * Get the version of another plugin.
     *
     * @param name the name of the other plugin.
     * @return the version.
     */
    @Override
    public String getPluginVersion(String name) {
        Plugin plugin = this.getPluginByName(name);
        if (plugin != null && plugin.getDescription() != null) return plugin.getDescription().getVersion();
        return null;
    }

    /**
     * Returns the commands a plugin has registered.
     *
     * @param plugin the plugin to deal with
     * @return the commands registered
     */
    @Override
    public String getUsages(Plugin plugin) {
        String parsedCommands = this.getCommandsFromPlugin(plugin).stream().map(s -> {
            String[] parts = s.getKey().split(":");
            // parts length equals 1 means that the key is the command
            return parts.length == 1 ? parts[0] : parts[1];
        }).collect(Collectors.joining(", "));



        if (parsedCommands.isEmpty())
            return "No commands registered.";

        return parsedCommands;

    }

    private List<Map.Entry<String, Command>> getCommandsFromPlugin(Plugin plugin) {
        Map<String, Command> knownCommands = this.getKnownCommands();
        return knownCommands.entrySet().stream()
                .filter(s -> {
                    if (s.getKey().contains(":")) return s.getKey().split(":")[0].equalsIgnoreCase(plugin.getName());
                    else {
                        ClassLoader cl = s.getValue().getClass().getClassLoader();
                        try {
                            return cl.getClass() == this.pluginClassLoader && this.pluginClassLoaderPlugin.get(cl) == plugin;
                        } catch (IllegalAccessException e) {
                            return false;
                        }
                    }
                })
                .collect(Collectors.toList());
    }

    /**
     * Find which plugin has a given command registered.
     *
     * @param command the command.
     * @return the plugin.
     */
    @Override
    public List<String> findByCommand(String command) {
        List<String> plugins = new ArrayList<>();

        for (Map.Entry<String, Command> s : this.getKnownCommands().entrySet()) {
            ClassLoader cl = s.getValue().getClass().getClassLoader();
            if (cl.getClass() != this.pluginClassLoader) {
                String[] parts = s.getKey().split(":");

                if (parts.length == 2 && parts[1].equalsIgnoreCase(command)) {
                    Plugin plugin = Arrays.stream(Bukkit.getPluginManager().getPlugins()).
                            filter(pl -> pl.getName().equalsIgnoreCase(parts[0])).
                            findFirst().orElse(null);

                    if (plugin != null)
                        plugins.add(plugin.getName());
                }
                continue;
            }

            try {
                String[] parts = s.getKey().split(":");
                String cmd = parts[parts.length - 1];

                if (!cmd.equalsIgnoreCase(command))
                    continue;

                JavaPlugin plugin = (JavaPlugin) this.pluginClassLoaderPlugin.get(cl);

                if (plugins.contains(plugin.getName()))
                    continue;

                plugins.add(plugin.getName());
            } catch (IllegalAccessException ignored) {
            }
        }

        return plugins;

    }
    
    

    /**
     * Loads and enables a plugin.
     *
     * @param plugin plugin to load
     * @return status message
     */
    private boolean load(Plugin plugin) {
        return this.load(plugin.getName());
    }

    /**
     * Loads and enables a plugin.
     *
     * @param name plugin's name
     * @return status message
     */
    @Override
    public boolean load(String name) {
        Plugin target;

        File pluginDir = new File("plugins");

        if (!pluginDir.isDirectory())
            return false;

        File pluginFile = new File(pluginDir, name + ".jar");

        if (!pluginFile.isFile()) for (File f : pluginDir.listFiles())
            if (f.getName().endsWith(".jar")) {
                JarFile jarFile;
                try {
                    jarFile = new JarFile(f);
                } catch (IOException e) {
                    continue;
                }

                if (jarFile.getEntry("plugin.yml") == null)
                    continue;

                InputStream stream;
                try {
                    stream = jarFile.getInputStream(jarFile.getEntry("plugin.yml"));
                } catch (IOException e) {
                    continue;
                }

                if (stream == null)
                    continue;

                PluginDescriptionFile desc;
                try {
                    desc = new PluginDescriptionFile(stream);
                } catch (InvalidDescriptionException e) {
                    continue;
                }

                if (desc.getName().equalsIgnoreCase(name)) {
                    pluginFile = f;
                    break;
                }
            }

        try {
            target = Bukkit.getPluginManager().loadPlugin(pluginFile);
        } catch (InvalidDescriptionException | InvalidPluginException e) {
            e.printStackTrace();
            return false;
        }

        target.onLoad();
        Bukkit.getPluginManager().enablePlugin(target);

        // todo this.loadCommands(target);

        return true;

    }

    @Override
    public Map<String, Command> getKnownCommands() {

        SimpleCommandMap commandMap = ((CraftServer) Bukkit.getServer()).getCommandMap();


        return (Map<String, Command>) commandMap.getKnownCommands();
    }




    /**
     * Reload a plugin.
     *
     * @param plugin the plugin to reload
     */
    @Override
    public void reload(Plugin plugin) {
        if (plugin != null) {
            this.unload(plugin);
            this.load(plugin);
        }
    }

    /**
     * Reload all plugins.
     */
    @Override
    public void reloadAll() {
        for (Plugin plugin : Bukkit.getPluginManager().getPlugins())
            this.reload(plugin);
    }

    /**
     * Unload a plugin.
     *
     * @param plugin the plugin to unload
     * @return the message to send to the user.
     */
    @Override
    public synchronized boolean unload(Plugin plugin) {
        String name = plugin.getName();

        org.bukkit.plugin.PluginManager pluginManager = Bukkit.getPluginManager();

        SimpleCommandMap commandMap = null;

        List<Plugin> plugins = null;

        Map<String, Plugin> names = null;
        Map<String, Command> commands = null;
        Map<Event, SortedSet<RegisteredListener>> listeners = null;

        if (pluginManager != null) {

            pluginManager.disablePlugin(plugin);

            try {

                Field pluginsField = Bukkit.getPluginManager().getClass().getDeclaredField("plugins");
                pluginsField.setAccessible(true);
                plugins = (List<Plugin>) pluginsField.get(pluginManager);

                Field lookupNamesField = Bukkit.getPluginManager().getClass().getDeclaredField("lookupNames");
                lookupNamesField.setAccessible(true);
                names = (Map<String, Plugin>) lookupNamesField.get(pluginManager);

                try {
                    Field listenersField = Bukkit.getPluginManager().getClass().getDeclaredField("listeners");
                    listenersField.setAccessible(true);
                    listeners = (Map<Event, SortedSet<RegisteredListener>>) listenersField.get(pluginManager);
                } catch (Exception e) {
                }

                Field commandMapField = Bukkit.getPluginManager().getClass().getDeclaredField("commandMap");
                commandMapField.setAccessible(true);
                commandMap = (SimpleCommandMap) commandMapField.get(pluginManager);

                Field knownCommandsField = SimpleCommandMap.class.getDeclaredField("knownCommands");
                knownCommandsField.setAccessible(true);
                commands = (Map<String, Command>) knownCommandsField.get(commandMap);

            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
                return false;
            }

        }

        pluginManager.disablePlugin(plugin);

        if (listeners != null)
            for (SortedSet<RegisteredListener> set : listeners.values())
                set.removeIf(value -> value.getPlugin() == plugin);

        if (commandMap != null) {
            Map<String, Command> modifiedKnownCommands = new HashMap<>(commands);

            for (Map.Entry<String, Command> entry : new HashMap<>(commands).entrySet()) {
                if (entry.getValue() instanceof PluginCommand) {
                    PluginCommand c = (PluginCommand) entry.getValue();
                    if (c.getPlugin() == plugin) {
                        c.unregister(commandMap);
                        modifiedKnownCommands.remove(entry.getKey());
                    }
                    continue;
                }

                try {
                    this.unregisterNonPluginCommands(plugin, commandMap, modifiedKnownCommands, entry);
                } catch (IllegalStateException e) {
                    if (e.getMessage().equalsIgnoreCase("zip file closed")) {
                        Logger.getLogger(PluginCommandUtils.class.getName()).info("Removing broken command '" + entry.getValue().getName() + "'!");
                        entry.getValue().unregister(commandMap);
                        modifiedKnownCommands.remove(entry.getKey());
                    }
                }
            }

            this.setKnownCommands(modifiedKnownCommands);
        }

        if (plugins != null)
            plugins.remove(plugin);

        if (names != null)
            names.remove(name);


        // Attempt to close the classloader to unlock any handles on the plugin's jar file.
        ClassLoader cl = plugin.getClass().getClassLoader();
        if (cl instanceof URLClassLoader) {
            try {

                Field pluginField = cl.getClass().getDeclaredField("plugin");
                pluginField.setAccessible(true);
                pluginField.set(cl, null);

                Field pluginInitField = cl.getClass().getDeclaredField("pluginInit");
                pluginInitField.setAccessible(true);
                pluginInitField.set(cl, null);

            } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException ex) {
                Logger.getLogger(PluginCommandUtils.class.getName()).log(Level.SEVERE, null, ex);
            }

            try {

                ((URLClassLoader) cl).close();
            } catch (IOException ex) {
                Logger.getLogger(PluginCommandUtils.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

        // Will not work on processes started with the -XX:+DisableExplicitGC flag, but lets try it anyway.
        // This tries to get around the issue where Windows refuses to unlock jar files that were previously loaded into the JVM.
        System.gc();

        return true;

    }

    protected void unregisterNonPluginCommands(Plugin plugin, CommandMap commandMap, Map<String, Command> commands,
                                               Map.Entry<String, Command> entry) {
        Field pluginField = Arrays.stream(((Map.Entry<String, ? extends Command>) entry).getValue().getClass().getDeclaredFields())
                .filter(field -> Plugin.class.isAssignableFrom(field.getType()))
                .findFirst()
                .orElse(null);
        if (pluginField == null) return;

        Plugin owningPlugin;
        try {
            pluginField.setAccessible(true);
            owningPlugin = (Plugin) pluginField.get(((Map.Entry<String, ? extends Command>) entry).getValue());
            if (owningPlugin.getName().equalsIgnoreCase(plugin.getName())) {
                ((Map.Entry<String, ? extends Command>) entry).getValue().unregister(commandMap);
                commands.remove(entry.getKey());
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setKnownCommands(Map<String, Command> knownCommands) {
        SimpleCommandMap commandMap = ((CraftServer) Bukkit.getServer()).getCommandMap();
        commandMap.setKnownCommands(knownCommands);
    }
}