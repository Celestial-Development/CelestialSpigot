package com.kaydeesea.spigot.command.server;

import com.google.common.collect.ImmutableList;

import java.util.*;

import com.kaydeesea.spigot.CelestialSpigot;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.util.StringUtil;

public class VersionCommand extends BukkitCommand {

    public VersionCommand() {
        super("version");

        this.description = "Gets the version of this server including any plugins in use";
        this.usageMessage = "/version [plugin name]";
        this.setPermission("bukkit.command.version");
        this.setAliases(Arrays.asList("ver", "about"));
    }

    @Override
    public boolean execute(CommandSender sender, String currentAlias, String[] args) {
        if (!testPermission(sender)) {
            return true;
        }

        if (args.length == 0) {
            for (String s : CelestialSpigot.INSTANCE.getConfig().getVersionCommand()) {
                sender.sendMessage(
                        ChatColor.translateAlternateColorCodes('&', s)
                                .replaceAll("%version%", CelestialSpigot.version)
                );
            }
        } else {
            StringBuilder name = new StringBuilder();

            for (String arg : args) {
                if (name.length() > 0) {
                    name.append(' ');
                }

                name.append(arg);
            }

            String pluginName = name.toString();
            Plugin exactPlugin = Bukkit.getPluginManager().getPlugin(pluginName);
            if (exactPlugin != null) {
                this.describeToSender(exactPlugin, sender);
                return true;
            }

            boolean found = false;
            pluginName = pluginName.toLowerCase();

            for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
                if (plugin.getName().toLowerCase().contains(pluginName)) {
                    this.describeToSender(plugin, sender);
                    found = true;
                }
            }

            if (!found) {
                sender.sendMessage(ChatColor.WHITE + "This server is not running any plugin by that name.");
                sender.sendMessage(ChatColor.WHITE + "Use /plugins to get a list of plugins.");
            }
        }

        return true;
    }

    private void describeToSender(Plugin plugin, CommandSender sender) {
        PluginDescriptionFile desc = plugin.getDescription();
        sender.sendMessage(ChatColor.AQUA + "Name: " + ChatColor.WHITE + desc.getName());
        sender.sendMessage(ChatColor.AQUA + "Version: " + ChatColor.WHITE + desc.getVersion());
        if (desc.getDescription() != null) {
            sender.sendMessage(desc.getDescription());
        }

        if (desc.getWebsite() != null) {
            sender.sendMessage(ChatColor.AQUA + "Website: " + ChatColor.WHITE + desc.getWebsite());
        }

        if (!desc.getAuthors().isEmpty()) {
            if (desc.getAuthors().size() == 1) {
                sender.sendMessage(ChatColor.AQUA + "Author: " + this.getAuthors(desc));
            } else {
                sender.sendMessage(ChatColor.AQUA + "Authors: " + this.getAuthors(desc));
            }
        }

    }

    private String getAuthors(PluginDescriptionFile desc) {
        StringBuilder result = new StringBuilder();
        List<String> authors = desc.getAuthors();

        for (int i = 0; i < authors.size(); ++i) {
            if (result.length() > 0) {
                result.append(ChatColor.WHITE);
                if (i < authors.size() - 1) {
                    result.append(", ");
                } else {
                    result.append(" and ");
                }
            }

            result.append(ChatColor.WHITE);
            result.append(authors.get(i));
        }

        return result.toString();
    }

    public List tabComplete(CommandSender sender, String alias, String[] args) {
        Validate.notNull(sender, "Sender cannot be null");
        Validate.notNull(args, "Arguments cannot be null");
        Validate.notNull(alias, "Alias cannot be null");
        if (args.length == 1) {
            if(!testPermission(sender)) return ImmutableList.of();
            List<String> completions = new ArrayList<>();
            if(CelestialSpigot.INSTANCE.getConfig().isTabCompletePlugins()) {
                String toComplete = args[0].toLowerCase();

                for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
                    if (StringUtil.startsWithIgnoreCase(plugin.getName(), toComplete)) {
                        completions.add(plugin.getName());
                    }
                }

            }
            return completions;

        } else {
            return ImmutableList.of();
        }
    }

}
