package com.kaydeesea.spigot.command;

import com.google.common.collect.ImmutableList;
import com.kaydeesea.spigot.command.utils.PluginCommandUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.InvalidDescriptionException;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.jar.JarFile;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PluginCommand extends Command {

    private static PluginCommandUtils pluginCommandUtils = new PluginCommandUtils();
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
        if(!testPermission(sender)) return true;
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
            if(args.length < 2) {
                sender.sendMessage("§cSpecify a plugin to enable");
                return true;
            }
            if (args[1].equalsIgnoreCase("all") || args[1].equalsIgnoreCase("*")) {
                pluginCommandUtils.enableAll();
                sender.sendMessage("§aSuccessfully enabled all plugins!");
                return true;
            }

            Plugin target = pluginCommandUtils.getPluginByName(args[1]);

            if (target == null) {
                sender.sendMessage("§cCouldn't find that plugin.");
                return true;
            }


            if (target.isEnabled()) {
                sender.sendMessage("§cPlugin already enabled!");
                return true;
            }

            pluginCommandUtils.enable(target);

            sender.sendMessage("§aEnabled plugin §b"+target.getName());
        }
        else if(cmd.equalsIgnoreCase("disable")) {
            if(args.length < 2) {
                sender.sendMessage("§cSpecify a plugin to disable");
                return true;
            }
            if (args[1].equalsIgnoreCase("all") || args[1].equalsIgnoreCase("*")) {
                pluginCommandUtils.disableAll();
                sender.sendMessage("§aSuccessfully disabled all plugins!");
                return true;
            }

            Plugin target = pluginCommandUtils.getPluginByName(args[1]);

            if (target == null) {
                sender.sendMessage("§cCouldn't find that plugin.");
                return true;
            }


            if (!target.isEnabled()) {
                sender.sendMessage("§cPlugin already disabled!");
                return true;
            }

            pluginCommandUtils.disable(target);

            sender.sendMessage("§aDisabled plugin §b"+target.getName());
        }
        else if(cmd.equalsIgnoreCase("load")) {
            if(args.length < 2) {
                sender.sendMessage("§cSpecify a plugin to load");
                return true;
            }
            for (int i = 1; i < args.length; i++)
                args[i] = args[i].replaceAll("[/\\\\]", "");

            Plugin potential = pluginCommandUtils.getPluginByName(args[1]);

            if (potential != null) {
                sender.sendMessage("§cPlugin is already loaded!");
                return true;
            }

            String name = consolidateStrings(args, 1);
            boolean a = pluginCommandUtils.load(name);
            if(a) {
                sender.sendMessage("§aPlugin §b"+name+" §successfully loaded");
            } else {
                sender.sendMessage("§cCouldn't load plugin "+name);
            }
        }
        else if(cmd.equalsIgnoreCase("unload")) {
            if(args.length < 2) {
                sender.sendMessage("§cSpecify a plugin to unload");
                return true;
            }
            for (int i = 1; i < args.length; i++)
                args[i] = args[i].replaceAll("[/\\\\]", "");

            Plugin plugin = pluginCommandUtils.getPluginByName(args[1]);

            if (plugin == null) {
                sender.sendMessage("§cPlugin is not loaded!");
                return true;
            }

            boolean a = pluginCommandUtils.unload(plugin);
            if(a) {
                sender.sendMessage("§aPlugin §b"+plugin.getName()+" §successfully unloaded");
            } else {
                sender.sendMessage("§cCouldn't unload plugin "+plugin.getName());
            }
        }
        else if(cmd.equalsIgnoreCase("reload")) {
            if(args.length < 2) {
                sender.sendMessage("§cSpecify a plugin to reload");
                return true;
            }
            if (args[1].equalsIgnoreCase("all") || args[1].equalsIgnoreCase("*")) {
                pluginCommandUtils.reloadAll();
                sender.sendMessage("§aSuccessfully reloaded all plugins!");
                return true;
            }

            Plugin target = pluginCommandUtils.getPluginByName(args[1]);

            if (target == null) {
                sender.sendMessage("§cCouldn't find that plugin.");
                return true;
            }

            pluginCommandUtils.reload(target);

            sender.sendMessage("§aReloaded plugin §b"+target.getName());
        }
        else {
            sender.sendMessage(help);
        }
        return true;
    }
    public static String consolidateStrings(String[] args, int start) {
        if (start < 0 || start > args.length)
            throw new IllegalArgumentException("Argument index out of bounds: " + start + "/" + args.length);

        return Stream.of(args).skip(start).collect(Collectors.joining(" "));
    }

    private final List<String> SUB_COMMANDS = Arrays.asList(
            "list", //
            "enable", //
            "disable", //
            "load", //
            "unload", //
            "reload" //
    );

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        if (SUB_COMMANDS.contains(args[0])) {
            List<String> enabledPlugins = pluginCommandUtils.getEnabledPluginNames(false);
            List<String> disabledPlugins = pluginCommandUtils.getDisabledPluginNames(false);
            enabledPlugins.add("all");
            disabledPlugins.add("all");
            if (args[0].equalsIgnoreCase("unload")) {
                if(args.length > 1) {
                    return enabledPlugins
                            .stream()
                            .filter(s -> s.contains(args[1]))
                            .collect(Collectors.toList());
                }
                return enabledPlugins;
            }
            if (args[0].equalsIgnoreCase("reload")) {
                if(args.length > 1) {
                    return enabledPlugins
                            .stream()
                            .filter(s -> s.contains(args[1]))
                            .collect(Collectors.toList());
                }
                return enabledPlugins;
            }
            if(args[0].equalsIgnoreCase("enable")) {
                if(args.length > 1) {
                    return disabledPlugins
                            .stream()
                            .filter(s -> s.contains(args[1]))
                            .collect(Collectors.toList());
                }
                return disabledPlugins;
            }
            if (args[0].equalsIgnoreCase("disable")) {
                if(args.length > 1) {
                    return enabledPlugins
                            .stream()
                            .filter(s -> s.contains(args[1]))
                            .collect(Collectors.toList());
                }
                return enabledPlugins;
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
        } else return SUB_COMMANDS;

        return ImmutableList.of();
    }
}