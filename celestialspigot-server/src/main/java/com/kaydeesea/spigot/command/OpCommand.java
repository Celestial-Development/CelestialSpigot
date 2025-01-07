package com.kaydeesea.spigot.command;

import com.google.common.collect.ImmutableList;
import joptsimple.internal.Strings;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OpCommand extends Command {

    private final ChatColor color = ChatColor.AQUA;

    private final String[] HELP_MESSAGE = {
            color + "&m--------&7&m" + StringUtils.repeat("-", 37) + color + "&m--------",
            color + "CelestialSpigot &7(OP Commands)",
            color + "&m--------&7&m" + StringUtils.repeat("-", 37) + color + "&m--------",
            "&7 * " + color + "/op &8<&7player&8> &8(&7&oGives a player operator status&8)",
            "&7 * " + color + "/deop &8<&7player&8> &8(&7&oRemoves a player's operator status&8)",
            color + "&m--------&7&m" + StringUtils.repeat("-", 37) + color + "&m--------"
    };

    public OpCommand() {
        super("op");
        this.description = "Grant operator status to specified player.";
        this.usageMessage = Strings.join(HELP_MESSAGE, "\n");
        this.setPermission("bukkit.command.op");
    }

    @Override
    public boolean execute(CommandSender sender, String currentAlias, String[] args) {
        if (!sender.hasPermission("bukkit.command.op")) {
            sender.sendMessage("&cNo permission.");
        } else {
            if (args.length != 1 || args[0].isEmpty()) {
                sender.sendMessage(HELP_MESSAGE);
            } else {
                OfflinePlayer player = Bukkit.getOfflinePlayer(args[0]);
                player.setOp(true);
                sender.sendMessage("&7You've granted " + color + player.getName() + " &7permissions for " + color + "operator&7!");
                if (player.isOnline()) {
                    player.getPlayer().sendMessage(" ");
                    player.getPlayer().sendMessage("&7You've been granted " + color + "Operator Status &7!");
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
            if (!(sender instanceof Player)) {
                return ImmutableList.of();
            }
            String lastWord = args[0];
            if (lastWord.isEmpty()) {
                return ImmutableList.of();
            }
            Player senderPlayer = (Player) sender;
            ArrayList<String> matchedPlayers = new ArrayList<>();
            for (Player player : sender.getServer().getOnlinePlayers()) {
                String name = player.getName();
                if (!senderPlayer.canSee(player) || player.isOp()) {
                    continue;
                }
                if (StringUtil.startsWithIgnoreCase(name, lastWord)) {
                    matchedPlayers.add(name);
                }
            }
            Collections.sort(matchedPlayers, String.CASE_INSENSITIVE_ORDER);
            return matchedPlayers;
        }
        return ImmutableList.of();
    }
}
