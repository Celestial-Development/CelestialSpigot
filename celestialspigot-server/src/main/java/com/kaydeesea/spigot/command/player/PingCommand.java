package com.kaydeesea.spigot.command.player;

import com.kaydeesea.spigot.CelestialSpigot;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class PingCommand extends Command {

	public PingCommand() {
		super("ping");
		setDescription("Shows a player's ping");
		setUsage("/ping");
	}

	@Override
	public boolean execute(CommandSender sender, String currentAlias, String[] args) {
		// If sender is a player send the player their own ping
		if ((args.length == 0) && sender instanceof Player) {
			String finalString = ChatColor.translateAlternateColorCodes('&', CelestialSpigot.INSTANCE.getConfig().getPingCommandSelf())
					.replace("%ping%", ((Integer) ((CraftPlayer) sender).getHandle().ping).toString());
			sender.sendMessage(finalString);
			
			// Otherwise send the ping of the argument player if valid
		} else if (args.length == 1) {
			
			Player pingPlayer = Bukkit.getPlayer(args[0]);
			if (pingPlayer != null && Bukkit.getOnlinePlayers().contains(pingPlayer)) {
				String finalString = ChatColor.translateAlternateColorCodes('&', CelestialSpigot.INSTANCE.getConfig().getPingCommandOther())
						.replace("%player%", pingPlayer.getName())
						.replace("%ping%", ((Integer) ((CraftPlayer) pingPlayer).getHandle().ping).toString());
				sender.sendMessage(finalString);
			} else {
				sender.sendMessage(ChatColor.RED + "Invalid player!");
			}
			
			// Message on improper usage
		} else {
			sender.sendMessage(ChatColor.RED + "Usage: /ping <player>");
		}

		return true;
	}

}
