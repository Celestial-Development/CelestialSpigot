package com.kaydeesea.spigot;

import lombok.Getter;
import lombok.Setter;
import com.kaydeesea.spigot.command.PotionCommand;
import com.kaydeesea.spigot.command.TPSCommand;
import com.kaydeesea.spigot.handler.MovementHandler;
import com.kaydeesea.spigot.command.KnockbackCommand;
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

    public static String version = "1.2.0";
    private Set<PacketHandler> packetHandlers = new HashSet<>();
    private Set<MovementHandler> movementHandlers = new HashSet<>();

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

		for (Map.Entry<String, Command> entry : commands.entrySet()) {
			MinecraftServer.getServer().server.getCommandMap().register(entry.getKey(), "Spigot", entry.getValue());
		}
	}

}
