package com.kaydeesea.spigot.command.server;

import com.kaydeesea.spigot.CelestialSpigot;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import java.lang.management.*;
import org.bukkit.*;
import com.kaydeesea.spigot.util.DateUtil;

public class TPSCommand extends Command
{
    public TPSCommand() {
        super("tps");
        this.description = "Gets the current ticks per second for the server";
        this.usageMessage = "/tps";
        this.setPermission("bukkit.command.tps");
    }

    private static String formatTPS(final double tps) {
        return ((tps > 18.0)
                ? ChatColor.GREEN
                : ((tps > 16.0)
                ? ChatColor.YELLOW
                : ChatColor.RED)
        ) + ((tps > 20.0) ? "*" : "") + Math.min(Math.round(tps * 100.0) / 100.0, 20.0);
    }
    private static String formatLag(final double tps) {
        return ((tps == 0)
                ? ChatColor.GREEN
                : ((tps > -5.0 && tps < 0)
                ? ChatColor.YELLOW
                : ChatColor.RED)
        ).toString() + Math.min(Math.round(tps * 100.0) / 100.0, 20.0);
    }


    @Override
    public boolean execute(final CommandSender sender, final String currentAlias, final String[] args) {
        if (!this.testPermission(sender)) {
            return true;
        }
        final double tpsNow = Bukkit.spigot().getTPS()[0];

        int totalEntities = 0;

        for (final World world : Bukkit.getServer().getWorlds()) {
            totalEntities += world.getEntities().size();
        }

        final double lag = (double)Math.round((1.0 - tpsNow / 20.0) * 100.0);
        final long usedMemory = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 2L / 1048576L;
        final long allocatedMemory = Runtime.getRuntime().totalMemory() / 1048576L;
        final World world2 = (sender instanceof Player) ? ((Player)sender).getWorld() : Bukkit.getWorlds().get(0);
        final Chunk[] loadedChunks = world2.getLoadedChunks();

        for (String s : CelestialSpigot.INSTANCE.getConfig().getTpsCommand()) {
            sender.sendMessage(
                    ChatColor.translateAlternateColorCodes('&', s)
                            .replaceAll("%uptime%", DateUtil.formatDateDiff(ManagementFactory.getRuntimeMXBean().getStartTime()))
                            .replaceAll("%tps%", formatTPS(tpsNow))
                            .replaceAll("%lag%", formatLag(Math.round(lag * 10000.0) / 10000.0))
                            .replaceAll("%entities%", String.valueOf(totalEntities))
                            .replaceAll("%loadedChunks%", String.valueOf(loadedChunks.length))
                            .replaceAll("%usedMemory%", String.valueOf(usedMemory))
                            .replaceAll("%allocatedMemory%", String.valueOf(allocatedMemory))
            );
        }

        return true;
    }
}
