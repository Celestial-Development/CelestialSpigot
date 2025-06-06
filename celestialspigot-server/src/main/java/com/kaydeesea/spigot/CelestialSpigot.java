package com.kaydeesea.spigot;

import com.kaydeesea.spigot.command.emojis.ShrugCommand;
import com.kaydeesea.spigot.command.emojis.TableFlipCommand;
import com.kaydeesea.spigot.command.knockback.KnockbackCommand;
import com.kaydeesea.spigot.command.knockback.PotionCommand;
import com.kaydeesea.spigot.command.op.DeopCommand;
import com.kaydeesea.spigot.command.op.OpCommand;
import com.kaydeesea.spigot.command.player.DayCommand;
import com.kaydeesea.spigot.command.player.NightCommand;
import com.kaydeesea.spigot.command.player.PingCommand;
import com.kaydeesea.spigot.command.server.*;
import com.kaydeesea.spigot.hitdetection.LagCompensator;
import lombok.Getter;
import lombok.Setter;
import com.kaydeesea.spigot.handler.MovementHandler;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.minecraft.server.MinecraftServer;

import org.bukkit.command.Command;

@Getter
public class CelestialSpigot {

	public static CelestialSpigot INSTANCE = new CelestialSpigot();

    @Setter
    private CelestialConfig config;

	@Setter
	private CelestialKnockBack knockBack;

	@Setter
	private CelestialKbOverrides kbOverrides;

    @Setter
	private LagCompensator lagCompensator;

	public static String version = "1.5.0";
    private final Set<MovementHandler> movementHandlers = new HashSet<>();
	public void addMovementHandler(MovementHandler handler) {
		this.movementHandlers.add(handler);
	}

	public void registerCommands() {
		Map<String, Command> commands = new HashMap<>();

		commands.put("knockback", new KnockbackCommand());
        commands.put("potion", new PotionCommand());
        commands.put("tps", new TPSCommand());
		commands.put("deop", new DeopCommand());
		commands.put("op", new OpCommand());
		if(getConfig().isEnablePingCommand()) {
			commands.put("ping", new PingCommand());
		}
		if(getConfig().isEnableVersionCommand()) {
			commands.put("version", new VersionCommand());
		}
		if(getConfig().isEnableDayCommand()) {
			commands.put("day", new DayCommand());
		}
		if(getConfig().isEnableNightCommand()) {
			commands.put("night", new NightCommand());
		}
		if(getConfig().isEnableKillEntitiesCommand()) {
			commands.put("killentities", new KillEntitiesCommand());
		}
		if(getConfig().isEnableSetSlotsCommand()) {
			commands.put("setslots", new SetSlotsCommand());
		}
		if(getConfig().isEnablePluginCommand()) {
			commands.put("plugin", new PluginCommand());
		}
		if(getConfig().isEnableShrugCommand()) {
			commands.put("shrug", new ShrugCommand());
		}
		if(getConfig().isEnableTableFlipCommand()) {
			commands.put("tableflip", new TableFlipCommand());
		}
		for (Map.Entry<String, Command> entry : commands.entrySet()) {
			MinecraftServer.getServer().server.getCommandMap().register(entry.getKey(), "Spigot", entry.getValue());
		}
	}

}
