package com.kaydeesea.spigot.command.server;

import com.kaydeesea.spigot.CelestialSpigot;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class SetSlotsCommand extends Command {

    public SetSlotsCommand() {
        super("setslots");
        this.description = "Set server slots";
        this.usageMessage = "§7/setslots <amount>";
        this.setPermission("bukkit.command.setslots");
    }
    private void setMaxPlayers(final int amount) {
        Bukkit.getServer().setMaxPlayers(amount);
    }
    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!this.testPermission(sender)) {
            return true;
        }
        if (args.length != 1) {
            sender.sendMessage(this.usageMessage);
            return true;
        }
        if (!isInt(args[0])) {
            sender.sendMessage(this.usageMessage);
            return true;
        }
        int slots = Integer.parseInt(args[0]);
        try {
            setMaxPlayers(slots);
        } catch (Exception e) {
            e.printStackTrace( );
        }
        sender.sendMessage(
                ChatColor.translateAlternateColorCodes('&', CelestialSpigot.INSTANCE.getConfig().getSetSlotsCommand())
                        .replaceAll("%slots%", slots+"")
        );
        return false;
    }

    public boolean isInt(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}