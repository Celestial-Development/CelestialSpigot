package com.kaydeesea.spigot.command;

import com.kaydeesea.spigot.knockback.KnockBackProfile;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import com.kaydeesea.spigot.CelestialSpigot;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PotionCommand extends Command {

    private final String separator = "§7§m-----------------------------";

    private final String[] help = Stream.of(
                    "",
                    "§3Potion Commands:",
                    " * §b/potion §fspeed §7<value>",
                    " * §b/potion §fmultiplier §7<value>",
                    " * §b/potion §foffset §7<value>",
                    " * §b/potion §fsmooth §7<true|false>",
                    ""
            )
            .toArray(String[]::new);
    private final List<String> SUB_COMMANDS = Arrays.asList(
            "list",
            "speed",
            "multiplier",
            "offset",
            "smooth"
    );

    public PotionCommand() {
        super("potion");
        setDescription("Set potion values");
        setAliases(Collections.singletonList("pot"));
        setPermission("celestial.pot");
    }

    @Override
    public boolean execute(CommandSender s, String currentAlias, String[] args) {
        if (testPermission(s)) {
            if(args.length == 0) {
                sendHelp(s);
                return true;
            } else if(args.length >= 2) {
                switch (args[0].toLowerCase()) {
                    case "multiplier": {
                        CelestialSpigot.INSTANCE.getConfig().set("potion-throw-multiplier", Float.valueOf(args[1]));
                        CelestialSpigot.INSTANCE.getConfig().setPotionFallSpeed(Float.parseFloat(args[1]));
                        s.sendMessage(ChatColor.WHITE + "You've set potion throw multiplier to: " + ChatColor.AQUA + Float.valueOf(args[1]));
                        break;
                    }
                    case "offset": {
                        CelestialSpigot.INSTANCE.getConfig().set("potion-throw-offset", Float.valueOf(args[1]));
                        CelestialSpigot.INSTANCE.getConfig().setPotionThrowOffset(Float.parseFloat(args[1]));
                        s.sendMessage(ChatColor.WHITE + "You've set potion throw offset to: " + ChatColor.AQUA + Float.valueOf(args[1]));
                        break;
                    }
                    case "speed": {
                        CelestialSpigot.INSTANCE.getConfig().set("potion-fall-speed", Float.valueOf(args[1]));
                        CelestialSpigot.INSTANCE.getConfig().setPotionFallSpeed(Float.parseFloat(args[1]));
                        s.sendMessage(ChatColor.WHITE + "You've set potion fall speed to: " + ChatColor.AQUA + Float.valueOf(args[1]));
                        break;
                    }
                    case "smooth": {
                        CelestialSpigot.INSTANCE.getConfig().set("smooth-heal-potions", Boolean.valueOf(args[1]));
                        CelestialSpigot.INSTANCE.getConfig().setSmoothHealPotions(Boolean.parseBoolean(args[1]));
                        s.sendMessage(ChatColor.WHITE + "You've set smooth heal potions to: " + ChatColor.AQUA + Boolean.valueOf(args[1]));
                        break;
                    }
                    default: {
                        sendHelp(s);
                        return true;
                    }
                }
                CelestialSpigot.INSTANCE.getConfig().save();
            } else {
                sendHelp(s);
            }
        }
        return true;
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage(help);
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        if (args.length > 0
                && SUB_COMMANDS.contains(args[0].toLowerCase())) {
            if (args.length == 2) {
                return CelestialSpigot.INSTANCE.getConfig().getKbProfiles()
                        .stream()
                        .sorted((o1, o2) -> String.CASE_INSENSITIVE_ORDER.compare(o1.getName(), o2.getName()))
                        .map(KnockBackProfile::getName)
                        .collect(Collectors.toList());
            }
        } else {
            return SUB_COMMANDS;
        }

        return super.tabComplete(sender, alias, args);
    }

}

