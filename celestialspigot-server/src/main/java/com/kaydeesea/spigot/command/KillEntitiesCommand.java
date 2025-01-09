package com.kaydeesea.spigot.command;

import com.kaydeesea.spigot.CelestialSpigot;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.List;

public class KillEntitiesCommand extends Command {
    public KillEntitiesCommand() {
        super("killentities");
        this.description = "Ping player";
        this.usageMessage = "§e/killentities";
        this.setPermission("celestial.killentities");
    }

    @Override
    public boolean execute(CommandSender sender, final String currentAlias, final String[] args) {
        if (!this.testPermission(sender)) {
            return true;
        }
        if(args.length > 0) {
            EntityType type;
            try {
                type = EntityType.valueOf(args[0].toUpperCase());
            } catch (Exception exception) {
                sender.sendMessage("§cCould not find an entity type named "+args[0]);
                return false;
            }
            int i = 0;
            for (World world : Bukkit.getWorlds()) {
                for (Entity entity : world.getEntities()) {
                    if(entity.getType() == type) {
                        entity.remove();
                        i++;
                    }
                }
            }
            sender.sendMessage(
                    ChatColor.translateAlternateColorCodes('&', CelestialSpigot.INSTANCE.getConfig().getKillEntitiesCommand())
                            .replaceAll("%entities%", i+"")
            );

            return false;
        }
        int i = 0;
        for (World world : Bukkit.getWorlds()) {
            for (Entity entity : world.getEntities()) {
                EntityType entityType = entity.getType( );
                if (entityType.equals(EntityType.DROPPED_ITEM) || entityType.equals(EntityType.PRIMED_TNT) || entityType.equals(EntityType.SKELETON) || entityType.equals(EntityType.ZOMBIE) || entityType.equals(EntityType.COW) || entityType.equals(EntityType.SHEEP) || entityType.equals(EntityType.ENDERMAN) || entityType.equals(EntityType.PIG_ZOMBIE) || entityType.equals(EntityType.PIG) || entityType.equals(EntityType.CREEPER) || entityType.equals(EntityType.BAT) || entityType.equals(EntityType.CHICKEN)|| entityType.equals(EntityType.CAVE_SPIDER) || entityType.equals(EntityType.SPIDER)) {
                    entity.remove( );
                    i++;
                }
            }
        }
        sender.sendMessage(
                ChatColor.translateAlternateColorCodes('&', CelestialSpigot.INSTANCE.getConfig().getKillEntitiesCommand())
                        .replaceAll("%entities%", i+"")
        );
        return false;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        String a = "";
        if(args.length > 0) {
            a = args[0];
        }
        ArrayList<String> entityTypes = new ArrayList<>();
        for (EntityType value : EntityType.values()) {
            if(value.name().toLowerCase().contains(a.toLowerCase())) entityTypes.add(value.name().toLowerCase());
        }
        return entityTypes;
    }
}
