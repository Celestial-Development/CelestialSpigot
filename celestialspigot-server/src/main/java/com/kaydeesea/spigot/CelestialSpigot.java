package com.kaydeesea.spigot;

import com.kaydeesea.spigot.command.*;
import com.kaydeesea.spigot.hitdetection.LagCompensator;
import lombok.Getter;
import lombok.Setter;
import com.kaydeesea.spigot.handler.MovementHandler;
import com.kaydeesea.spigot.handler.PacketHandler;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.minecraft.server.MinecraftServer;

import org.bukkit.command.Command;

@Getter
public enum CelestialSpigot {

	INSTANCE;

    @Setter
    private CelestialConfig config;

	@Setter
	private CelestialKnockBack knockBack;

	@Getter
    @Setter
	private LagCompensator lagCompensator;

    public static String version = "1.2.0";
    private final Set<PacketHandler> packetHandlers = new HashSet<>();
    private final Set<MovementHandler> movementHandlers = new HashSet<>();

    public void addPacketHandler(PacketHandler handler) {
		this.packetHandlers.add(handler);
	}

	public void addMovementHandler(MovementHandler handler) {
		this.movementHandlers.add(handler);
	}

	public void registerCommands() {
		Map<String, Command> commands = new HashMap<>();

		commands.put("knockback", new KnockbackCommand());
        commands.put("potion", new PotionCommand());
        commands.put("tps", new TPSCommand());
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
		commands.put("plugin", new PluginCommand());
		for (Map.Entry<String, Command> entry : commands.entrySet()) {
			MinecraftServer.getServer().server.getCommandMap().register(entry.getKey(), "Spigot", entry.getValue());
		}
	}

}
