package org.bukkit.command;

import static org.bukkit.util.Java15Compat.Arrays_copyOfRange;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.command.defaults.*;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.github.paperspigot.event.ServerExceptionEvent;
import org.github.paperspigot.exception.ServerCommandException;
import org.github.paperspigot.exception.ServerTabCompleteException;

public class SimpleCommandMap implements CommandMap {
    private static final Pattern PATTERN_ON_SPACE = Pattern.compile(" ", Pattern.LITERAL);
    @Setter
    @Getter
    public Map<String, Command> knownCommands = new HashMap<String, Command>();
    private final Server server;

    public SimpleCommandMap(final Server server) {
        this.server = server;
        setDefaultCommands();
    }

    private void setDefaultCommands() {
        if(server.reloadCommandEnabled()) {
            register("bukkit", new ReloadCommand("reload"));
        }
        if(server.pluginsCommandEnabled()) {
            register("bukkit", new PluginsCommand("plugins"));
        }
        register("bukkit", new co.aikar.timings.TimingsCommand("timings")); // Spigot
    }

    public void setFallbackCommands() {
        register("bukkit", new HelpCommand());
    }

    /**
     * {@inheritDoc}
     */
    public void registerAll(String fallbackPrefix, List<Command> commands) {
        if (commands != null) {
            for (Command c : commands) {
                register(fallbackPrefix, c);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public boolean register(String fallbackPrefix, Command command) {
        return register(command.getName(), fallbackPrefix, command);
    }

    /**
     * {@inheritDoc}
     */
    public boolean register(String label, String fallbackPrefix, Command command) {
        command.timings = co.aikar.timings.TimingsManager.getCommandTiming(fallbackPrefix, command); // Spigot
        label = label.toLowerCase().trim();
        fallbackPrefix = fallbackPrefix.toLowerCase().trim();
        boolean registered = register(label, command, false, fallbackPrefix);

        Iterator<String> iterator = command.getAliases().iterator();
        while (iterator.hasNext()) {
            if (!register(iterator.next(), command, true, fallbackPrefix)) {
                iterator.remove();
            }
        }

        // If we failed to register under the real name, we need to set the command label to the direct address
        if (!registered) {
            command.setLabel(fallbackPrefix + ":" + label);
        }

        // Register to us so further updates of the commands label and aliases are postponed until its reregistered
        command.register(this);

        return registered;
    }

    /**
     * Registers a command with the given name is possible. Also uses
     * fallbackPrefix to create a unique name.
     *
     * @param label the name of the command, without the '/'-prefix.
     * @param command the command to register
     * @param isAlias whether the command is an alias
     * @param fallbackPrefix a prefix which is prepended to the command for a
     *     unique address
     * @return true if command was registered, false otherwise.
     */
    private synchronized boolean register(String label, Command command, boolean isAlias, String fallbackPrefix) {
        knownCommands.put(fallbackPrefix + ":" + label, command);
        if ((command instanceof VanillaCommand || isAlias) && knownCommands.containsKey(label)) {
            // Request is for an alias/fallback command and it conflicts with
            // a existing command or previous alias ignore it
            // Note: This will mean it gets removed from the commands list of active aliases
            return false;
        }

        boolean registered = true;

        // If the command exists but is an alias we overwrite it, otherwise we return
        Command conflict = knownCommands.get(label);
        if (conflict != null && conflict.getLabel().equals(label)) {
            return false;
        }

        if (!isAlias) {
            command.setLabel(label);
        }
        knownCommands.put(label, command);

        return registered;
    }

    /**
     * {@inheritDoc}
     */
    public boolean dispatch(CommandSender sender, String commandLine) throws CommandException {
        String[] args = PATTERN_ON_SPACE.split(commandLine);

        if (args.length == 0) {
            return false;
        }

        String sentCommandLabel = args[0].toLowerCase();

        Command target = getCommand(sentCommandLabel);

        if (target == null) {
            return false;
        }

        try {
            target.timings.startTiming(); // Spigot
            // Note: we don't return the result of target.execute as thats success / failure, we return handled (true) or not handled (false)
            target.execute(sender, sentCommandLabel, Arrays_copyOfRange(args, 1, args.length));
            target.timings.stopTiming(); // Spigot
        } catch (CommandException ex) {
            target.timings.stopTiming(); // Spigot
            throw ex;
        } catch (Throwable ex) {
            target.timings.stopTiming(); // Spigot
            String msg = "Unhandled exception executing '" + commandLine + "' in " + target;
            server.getPluginManager().callEvent(new ServerExceptionEvent(new ServerCommandException(ex, target, sender, args))); // Paper
            throw new CommandException(msg, ex);
        }

        // return true as command was handled
        return true;
    }

    public synchronized void clearCommands() {
        for (Map.Entry<String, Command> entry : knownCommands.entrySet()) {
            entry.getValue().unregister(this);
        }
        knownCommands.clear();
        setDefaultCommands();
    }

    public Command getCommand(String name) {
        Command target = knownCommands.get(name.toLowerCase());
        return target;
    }

    public List<String> tabComplete(CommandSender sender, String cmdLine) {
        return tabComplete(sender, cmdLine, null); // PaperSpigot - location tab-completes, code moved below
    }

    // PaperSpigot start - location tab-completes
    /*
        this code was copied, except for the noted change, from tabComplete(CommandSender sender, String cmdLine)
     */
    public List<String> tabComplete(CommandSender sender, String cmdLine, Location location) {
        Validate.notNull(sender, "Sender cannot be null");
        Validate.notNull(cmdLine, "Command line cannot null");

        int spaceIndex = cmdLine.indexOf(' ');

        if (spaceIndex == -1) {
            ArrayList<String> completions = new ArrayList<String>();
            Map<String, Command> knownCommands = this.knownCommands;

            final String prefix = (sender instanceof Player ? "/" : "");

            for (Map.Entry<String, Command> commandEntry : knownCommands.entrySet()) {
                Command command = commandEntry.getValue();

                if (!command.testPermissionSilent(sender)) {
                    continue;
                }

                String name = commandEntry.getKey(); // Use the alias, not command name

                if (StringUtil.startsWithIgnoreCase(name, cmdLine)) {
                    completions.add(prefix + name);
                }
            }

            Collections.sort(completions, String.CASE_INSENSITIVE_ORDER);
            return completions;
        }

        String commandName = cmdLine.substring(0, spaceIndex);
        Command target = getCommand(commandName);

        if (target == null) {
            return null;
        }

        if (!target.testPermissionSilent(sender)) {
            return null;
        }

        String argLine = cmdLine.substring(spaceIndex + 1, cmdLine.length());
        String[] args = PATTERN_ON_SPACE.split(argLine, -1);

        try {
            return target.tabComplete(sender, commandName, args, location); // PaperSpigot - add location argument
        } catch (CommandException ex) {
            throw ex;
        } catch (Throwable ex) {
            String msg = "Unhandled exception executing tab-completer for '" + cmdLine + "' in " + target;
            server.getPluginManager().callEvent(new ServerExceptionEvent(new ServerTabCompleteException(msg, ex, target, sender, args))); // Paper
            throw new CommandException(msg, ex);
        }
    }
    // PaperSpigot end

    public Collection<Command> getCommands() {
        return Collections.unmodifiableCollection(knownCommands.values());
    }

    public void registerServerAliases() {
        Map<String, String[]> values = server.getCommandAliases();

        for (String alias : values.keySet()) {
            if (alias.contains(":") || alias.contains(" ")) {
                server.getLogger().warning("Could not register alias " + alias + " because it contains illegal characters");
                continue;
            }

            String[] commandStrings = values.get(alias);
            List<String> targets = new ArrayList<String>();
            StringBuilder bad = new StringBuilder();

            for (String commandString : commandStrings) {
                String[] commandArgs = commandString.split(" ");
                Command command = getCommand(commandArgs[0]);

                if (command == null) {
                    if (bad.length() > 0) {
                        bad.append(", ");
                    }
                    bad.append(commandString);
                } else {
                    targets.add(commandString);
                }
            }

            if (bad.length() > 0) {
                server.getLogger().warning("Could not register alias " + alias + " because it contains commands that do not exist: " + bad);
                continue;
            }

            // We register these as commands so they have absolute priority.
            if (targets.size() > 0) {
                knownCommands.put(alias.toLowerCase(), new FormattedCommandAlias(alias.toLowerCase(), targets.toArray(new String[targets.size()])));
            } else {
                knownCommands.remove(alias.toLowerCase());
            }
        }
    }
}
