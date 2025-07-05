package com.kaydeesea.spigot.command.emojis;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;

public class CryCommand extends Command {
    public CryCommand() {
        super("cry", "Type the crying emoji in chat", "/shrug", Collections.emptyList());
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (sender instanceof ConsoleCommandSender) {
            sender.sendMessage(ChatColor.RED + "Only players can do this command!");
            return true;
        }
        Player player = (Player) sender;
        player.chat("｡･ﾟﾟ･(>_<)･ﾟﾟ･｡");
        return true;
    }
}