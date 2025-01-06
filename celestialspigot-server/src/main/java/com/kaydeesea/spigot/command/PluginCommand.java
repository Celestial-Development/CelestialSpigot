package com.kaydeesea.spigot.command;

import com.google.common.collect.ImmutableList;
import com.kaydeesea.spigot.CelestialSpigot;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.InvalidDescriptionException;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.util.StringUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.jar.JarFile;
import java.util.stream.Stream;

public class PluginCommand extends Command {

    private final String separator = "§7§m-----------------------------";

    private final String[] help = Stream.of(
                    "",
                    "§3Plugin Commands:",
                    " * §b/plugin §flist",
                    " * §b/plugin §fenable §7<plugin>",
                    " * §b/plugin §fdisable §7<plugin>",
                    " * §b/plugin §freload §7<plugin>",
                    " * §b/plugin §fload §7<file>",
                    " * §b/plugin §funload §7<plugin>",
                    ""
            )
            .toArray(String[]::new);

    public PluginCommand() {
        super("plugin");
        setDescription("Manage plugins from in-game");
        setAliases(Arrays.asList("manageplugin", "pluginsmanager"));
        setUsage("/plugin (enable/disable/load/unload/reload)");
        setPermission("celestial.plugin");
    }

    @Override
    public boolean execute(CommandSender sender, String currentAlias, String[] args) {
        if(args.length == 0 || args[0].equalsIgnoreCase("help")) {
            sender.sendMessage(help);
            return true;
        }
        String cmd = args[0];
        if(cmd.equalsIgnoreCase("list")) {
            sender.sendMessage(separator);
            int pl = Bukkit.getPluginManager().getPlugins().length;
            sender.sendMessage("§3Plugins (§b"+pl+"§3):");
            String plugins = "";
            int a = 0;
            for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
                a++;
                plugins += "§b"+plugin.getName()+" (§3"+plugin.getDescription().getVersion()+"§b)";
                if(a != pl) {
                    plugins += ", ";
                }
            }
            sender.sendMessage(plugins);
            sender.sendMessage(separator);
        }
        else if(cmd.equalsIgnoreCase("enable")) {

        }

        return true;
    }

    private final List<String> SUB_COMMANDS = Arrays.asList(
            "list",
            "enable",
            "disable",
            "load",
            "unload",
            "reload"
    );

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        if (SUB_COMMANDS.contains(args[0])) {
            if (args[0].equalsIgnoreCase("reload")) {
                ArrayList<String> plugins = new ArrayList<>();
                for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
                    plugins.add(plugin.getName());
                }
                return plugins;
            }
            if (args[0].equalsIgnoreCase("disable")) {
                ArrayList<String> plugins = new ArrayList<>();
                for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
                    plugins.add(plugin.getName());
                }
                return plugins;
            }
            if(args[0].equalsIgnoreCase("load")) {
                List<String> files = new ArrayList<>();

                for (File pluginFile : new File("plugins").listFiles()) {
                    try {
                        if (pluginFile.isDirectory())
                            continue;

                        if (!pluginFile.getName().toLowerCase().endsWith(".jar"))
                            if (!new File("plugins", pluginFile.getName() + ".jar").exists())
                                continue;

                        JarFile jarFile = null;
                        try {
                            jarFile = new JarFile(pluginFile);
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

                        PluginDescriptionFile descriptionFile;
                        try {
                            descriptionFile = new PluginDescriptionFile(stream);
                        } catch (InvalidDescriptionException e) {
                            continue;
                        }

                        files.add(pluginFile.getName().substring(0, pluginFile.getName().length() - ".jar".length()));

                        for (Plugin plugin : Bukkit.getPluginManager().getPlugins())
                            if (plugin.getName().equalsIgnoreCase(descriptionFile.getName()))
                                files.remove(pluginFile.getName().substring(0, pluginFile.getName().length() - ".jar".length()));

                    } catch (Exception ignored) {
                    }
                }
                return files;
            }
            System.out.println(Arrays.toString(args));
        } else return SUB_COMMANDS;

        return ImmutableList.of();
    }
}