package com.kaydeesea.spigot.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;

public class ShrugCommand extends Command {
    public ShrugCommand() {
        super("shrug", "Type the shrug emoji in chat", "/shrug", Collections.emptyList());
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (sender instanceof ConsoleCommandSender) {
            sender.sendMessage(ChatColor.RED + "Only players can do this command!");
            return true;
        }
        Player player = (Player) sender;
        player.chat("¯\\_(ツ)_/¯");
        return true;
    }
}
