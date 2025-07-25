package com.kaydeesea.spigot.console;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.craftbukkit.command.CraftConsoleCommandSender;

public class PandaConsoleCommandSender extends CraftConsoleCommandSender {
    private static final Logger LOGGER = LogManager.getRootLogger();
    
    @Override
    public void sendRawMessage(String message) {
        // TerminalConsoleAppender supports color codes directly in log messages
        LOGGER.info(message);
    }
}
