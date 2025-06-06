package com.kaydeesea.spigot.command.emojis;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;

public class TableFlipCommand extends Command {
    public TableFlipCommand() {
        super("tableflip", "Type the table flip emoji in chat", "/tableflip", Collections.emptyList());
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (sender instanceof ConsoleCommandSender) {
            sender.sendMessage(ChatColor.RED + "Only players can do this command!");
            return true;
        }
        Player player = (Player) sender;
        player.chat("(╯°□°)╯︵ ┻━┻");
        return true;
    }
}
