package com.kaydeesea.spigot.command.knockback;

import com.kaydeesea.spigot.knockback.KnockBackProfile;
import com.kaydeesea.spigot.knockback.projectiles.CelestialProjectiles;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import com.kaydeesea.spigot.CelestialSpigot;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class PotionCommand extends Command {


    private final List<String> SUB_COMMANDS = Arrays.asList(
            "list",
            "speed",
            "multiplier",
            "offset"
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
                    case "list": {
                        for (String s1 : CelestialSpigot.INSTANCE.getConfig().getPotionInfoCommand()) {
                            s.sendMessage(
                                    org.bukkit.ChatColor.translateAlternateColorCodes('&', s1)
                                            .replaceAll("%multiplier%", CelestialProjectiles.getPotionThrowMultiplier()+"")
                                            .replaceAll("%offset%", CelestialProjectiles.getPotionThrowOffset()+"")
                                            .replaceAll("%speed%", CelestialProjectiles.getPotionFallSpeed()+"")
                            );
                        }
                    }
                    case "multiplier": {
                        CelestialProjectiles.getConfig().set("projectiles.potions.potion-throw-multiplier", Float.valueOf(args[1]));
                        CelestialProjectiles.setPotionFallSpeed(Float.parseFloat(args[1]));
                        s.sendMessage(ChatColor.WHITE + "You've set potion throw multiplier to: " + ChatColor.AQUA + Float.valueOf(args[1]));
                        break;
                    }
                    case "offset": {
                        CelestialProjectiles.getConfig().set("projectiles.potions.potion-throw-offset", Float.valueOf(args[1]));
                        CelestialProjectiles.setPotionThrowOffset(Float.parseFloat(args[1]));
                        s.sendMessage(ChatColor.WHITE + "You've set potion throw offset to: " + ChatColor.AQUA + Float.valueOf(args[1]));
                        break;
                    }
                    case "speed": {
                        CelestialProjectiles.getConfig().set("projectiles.potions.potion-fall-speed", Float.valueOf(args[1]));
                        CelestialProjectiles.setPotionFallSpeed(Float.parseFloat(args[1]));
                        s.sendMessage(ChatColor.WHITE + "You've set potion fall speed to: " + ChatColor.AQUA + Float.valueOf(args[1]));
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
        for (String s1 : CelestialSpigot.INSTANCE.getConfig().getPotionCommand()) {
            sender.sendMessage(
                    org.bukkit.ChatColor.translateAlternateColorCodes('&', s1)
            );
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        if (args.length > 0
                && SUB_COMMANDS.contains(args[0].toLowerCase())) {
            if (args.length == 2) {
                return CelestialSpigot.INSTANCE.getKnockBack().getKbProfiles()
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

