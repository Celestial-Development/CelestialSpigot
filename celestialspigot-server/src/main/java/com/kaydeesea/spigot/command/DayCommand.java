package com.kaydeesea.spigot.command;

import com.kaydeesea.spigot.CelestialSpigot;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DayCommand extends Command {

    public DayCommand() {
        super("day");
        setDescription("Changes the world time to day");
        setUsage("/day");
        setPermission("celestial.day");
    }

    @Override
    public boolean execute(CommandSender sender, String currentAlias, String[] args) {
        // If sender is a player send the player their own ping
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cThis command can only be used by a player.");
            return true;
        }
        Player player = (Player)sender;
        if (!this.testPermission(player)) {
            player.sendMessage("§cYou don't have the permission to use this command.");
            return true;
        }
        World world = player.getWorld();
        world.setTime(1000L);
        player.sendMessage(
                ChatColor.translateAlternateColorCodes('&', CelestialSpigot.INSTANCE.getConfig().getDayCommand())
        );
        return true;


    }

}
