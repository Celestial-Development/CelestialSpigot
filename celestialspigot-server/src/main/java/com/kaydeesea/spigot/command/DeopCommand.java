package com.kaydeesea.spigot.command;

import com.google.common.collect.ImmutableList;
import com.kaydeesea.spigot.CelestialSpigot;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class DeopCommand extends Command {
    
    private final ChatColor color = ChatColor.AQUA;

    public DeopCommand() {
        super("deop");
        this.description = "Remove specified player's operator status.";
        this.setPermission("bukkit.command.deop");
    }

    @Override
    public boolean execute(CommandSender sender, String currentAlias, String[] args) {
        if (!sender.hasPermission("bukkit.command.deop")) {
            sender.sendMessage("§cNo permission.");
        } else {
            if (args.length != 1 || args[0].isEmpty()) {
                for (String s : CelestialSpigot.INSTANCE.getConfig().getOpCommand()) {
                    sender.sendMessage(
                            ChatColor.translateAlternateColorCodes('&', s)
                    );
                }
            } else {
                OfflinePlayer player = Bukkit.getOfflinePlayer(args[0]);
                player.setOp(false);
                sender.sendMessage(
                        ChatColor.translateAlternateColorCodes('&', CelestialSpigot.INSTANCE.getConfig().getOpTakeCommand())
                                .replaceAll("%player%", player.getName())
                );
                if (player.isOnline()) {
                    player.getPlayer().sendMessage(" ");
                    player.getPlayer().sendMessage("§7Your " + color + "operator status §7has been removed.");
                    player.getPlayer().sendMessage(" ");
                }
            }
        }
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        Validate.notNull(sender, "Sender cannot be null");
        Validate.notNull(args, "Arguments cannot be null");
        Validate.notNull(alias, "Alias cannot be null");
        if (args.length == 1) {
            List<String> completions = new ArrayList<>();
            for (OfflinePlayer player : Bukkit.getOperators()) {
                String playerName = player.getName();
                if (StringUtil.startsWithIgnoreCase(playerName, args[0])) {
                    completions.add(playerName);
                }
            }
            return completions;
        }
        return ImmutableList.of();
    }
}
