package com.kaydeesea.spigot.command;

import com.kaydeesea.spigot.knockback.KnockBackProfile;
import com.kaydeesea.spigot.knockback.ProfileType;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import com.kaydeesea.spigot.CelestialSpigot;
import com.kaydeesea.spigot.knockback.NormalTypeKnockbackProfile;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class KnockbackCommand extends Command {

    private final String separator = "§7§m-----------------------------";

    private final String[] help = Stream.of(
            "",
                    "§3Knockback Commands:",
                    " * §b/knockback §flist",
                    " * §b/knockback §finfo §7<profile>",
                    " * §b/knockback §fcreate §7<profile> <type>",
                    " * §b/knockback §fdelete §7<profile>",
                    " * §b/knockback §fload §7<profile>",
                    " * §b/knockback §fview §7<profile>",
                    " * §b/knockback §fedit §7<profile> <variable> <value>", // far better than the old system
                    " * §b/knockback §fset §7<profile> <player>",
                    ""
            )
            .toArray(String[]::new);
    private final List<String> SUB_COMMANDS = Arrays.asList(
            "list",
            "create",
            "delete",
            "load",
            "view",
            "edit",
            "set"
    );

    public KnockbackCommand() {
        super("knockback");
        this.setAliases(Collections.singletonList("kb"));
        this.setPermission("celestial.kb");
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!testPermission(sender)) return true;

        if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
            sendHelp(sender);
            return true;
        }

        String command = args[0].toLowerCase();

        if (command.equals("delete")) {
            if (args.length < 2) {
                sender.sendMessage("§cUsage: /command delete <profile_name>");
                return true;
            }
            if (CelestialSpigot.INSTANCE.getConfig().getCurrentKb().getName().equalsIgnoreCase(args[1])) {
                knockbackCommandMain(sender);
                sender.sendMessage("§cYou cannot delete the profile that is being used.");
                return true;
            }
            if (CelestialSpigot.INSTANCE.getConfig().getKbProfiles().removeIf(profile -> profile.getName().equalsIgnoreCase(args[1]))) {
                CelestialSpigot.INSTANCE.getConfig().set("knockback.profiles." + args[1], null);
                CelestialSpigot.INSTANCE.getConfig().save();
                knockbackCommandMain(sender);
                sender.sendMessage("§aThe profile §e" + args[1] + " §ahas been removed.");
                return true;
            } else {
                sender.sendMessage("§cThis profile doesn't exist.");
            }
        } else if (command.equals("load")) {
            if (args.length < 2) {
                sender.sendMessage("§cUsage: /command load <profile_name>");
                return true;
            }
            KnockBackProfile profile = CelestialSpigot.INSTANCE.getConfig().getKbProfileByName(args[1]);
            if (profile != null) {
                if (CelestialSpigot.INSTANCE.getConfig().getCurrentKb().getName().equalsIgnoreCase(args[1])) {
                    sender.sendMessage("§cThis profile is loaded.");
                    return true;
                }
                CelestialSpigot.INSTANCE.getConfig().setCurrentKb(profile);
                CelestialSpigot.INSTANCE.getConfig().set("knockback.current", profile.getName());
                CelestialSpigot.INSTANCE.getConfig().save();
                knockbackCommandMain(sender);
                sender.sendMessage("§aThe profile §e" + args[1] + " §ahas been loaded.");
                return true;
            } else {
                sender.sendMessage("§cThis profile doesn't exist.");
            }
        } else if (command.equals("info")) {
            if (args.length < 2) {
                sender.sendMessage("§cUsage: /command info <profile_name>");
                return true;
            }
            KnockBackProfile profile = CelestialSpigot.INSTANCE.getConfig().getKbProfileByName(args[1]);
            if (profile != null) {
                sendKnockbackInfo(sender, profile);
            } else {
                sender.sendMessage("§cThis profile doesn't exist.");
            }
        } else if (command.equals("set")) {
            if (args.length < 3) {
                sender.sendMessage("§cUsage: /command set <profile_name> <player>");
                return true;
            }
            KnockBackProfile profile = CelestialSpigot.INSTANCE.getConfig().getKbProfileByName(args[1]);
            if (profile == null) {
                sender.sendMessage("§cA profile with that name could not be found.");
                return true;
            }
            Player target = Bukkit.getPlayer(args[2]);
            if (target == null) {
                sender.sendMessage("§cThat player is not online.");
                return true;
            }
            target.setKnockbackProfile(profile);
        } else if (command.equals("create")) {
            if (args.length < 3) {
                sender.sendMessage("§cUsage: /command create <profile_name> <type>");
                return true;
            }
            if (!isProfileName(args[1])) {
                if (args[2].equalsIgnoreCase("normal")) {
                    NormalTypeKnockbackProfile profile = new NormalTypeKnockbackProfile(args[1]);
                    CelestialSpigot.INSTANCE.getConfig().getKbProfiles().add(profile);
                    profile.save();
                    sender.sendMessage("§aThe profile §e" + args[1] + " §ahas been created.");
                    sendKnockbackInfo(sender, profile);
                } else {
                    StringBuilder types = new StringBuilder();
                    for (ProfileType value : ProfileType.values()) {
                        types.append(value.name()).append(" ");
                    }
                    sender.sendMessage("§cSpecify a type of knockback. Types: " + types);
                }
                return true;
            } else {
                sender.sendMessage("§cA knockback profile with that name already exists.");
            }
        } else if (command.equals("edit")) {
            if (args.length < 4) {
                sender.sendMessage("§cUsage: /command edit <profile_name> <property> <value>");
                return true;
            }
            KnockBackProfile profile = CelestialSpigot.INSTANCE.getConfig().getKbProfileByName(args[1].toLowerCase());
            if (profile == null) {
                sender.sendMessage("§cThis profile doesn't exist.");
                return true;
            }
            if (!org.apache.commons.lang3.math.NumberUtils.isNumber(args[3])) {
                sender.sendMessage("§f" + args[3] + " §c is not a number.");
                return true;
            }
            double value = Double.parseDouble(args[3]);
            String f = args[2].toLowerCase();
            switch (f) {
                case "horfriction":
                    f = "horizontalfriction";
                    break;
                case "verfriction":
                    f = "verticalfriction";
                    break;
                case "fri":
                    f = "friction";
                    break;
                case "hor":
                    f = "horizontal";
                    break;
                case "vert":
                    f = "vertical";
                    break;
                case "extrahor":
                    f = "extrahorizontal";
                    break;
                case "extravert":
                    f = "extravertical";
                    break;
                case "vertmax":
                case "verticalmax":
                case "verticallimit":
                    f = "vertical-limit";
                    break;
            }
            String s = "";
            for (String a : profile.getValues()) {
                if (a.contains(f)) s = a;
            }
            if (!s.isEmpty()) {
                if (profile.getType() == ProfileType.NORMAL) {
                    modifyNormalTypeProfile((NormalTypeKnockbackProfile) profile, s, value);
                    sender.sendMessage("§aChanged §b" + profile.getName() + "§a's §b" + s + " §asetting to §b" + value + "§a.");
                }
            } else {
                sender.sendMessage("§cCouldn't find a §4" + f + " §cproperty in knockback profile " + profile.getName() + ".");
            }
        } else {
            knockbackCommandMain(sender);
        }

        return true;

    }

    private static NormalTypeKnockbackProfile modifyNormalTypeProfile(NormalTypeKnockbackProfile profile, String s, double value) {
        if (s.equalsIgnoreCase("friction")) {
            profile.setFriction(value);
        } else if (s.equalsIgnoreCase("horizontal")) {
            profile.setHorizontal(value);
        } else if (s.equalsIgnoreCase("vertical")) {
            profile.setVertical(value);
        } else if (s.equalsIgnoreCase("vertical-limit")) {
            profile.setVerticalLimit(value);
        } else if (s.equalsIgnoreCase("extra-horizontal")) {
            profile.setExtraHorizontal(value);
        } else if (s.equalsIgnoreCase("extra-vertical")) {
            profile.setExtraVertical(value);
        }
        profile.save();
        return profile;
    }

    private void sendKnockbackInfo(CommandSender sender, KnockBackProfile profile) {
        sender.sendMessage(separator);
        if(profile.getType() == ProfileType.NORMAL) {
            NormalTypeKnockbackProfile prf = (NormalTypeKnockbackProfile) profile;
            for (String value : prf.getValues()) {
                String msg = getNormalKnockbackInfo(value, prf);
                TextComponent message = new TextComponent(msg);
                message.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/knockback edit "+profile.getName()+" "+value+" "));
                if(sender instanceof  Player) {
                    ((Player)sender).spigot().sendMessage(message);
                } else {
                    sender.sendMessage(msg);
                }
            }
        }
        sender.sendMessage(separator);
    }

    private static String getNormalKnockbackInfo(String value, NormalTypeKnockbackProfile prf) {
        String msg = "§b"+ value +": ";
        if(value.equalsIgnoreCase("friction")) {
            msg += prf.getFriction();
        } else if(value.equalsIgnoreCase("horizontal")) {
            msg += prf.getHorizontal();
        } else if(value.equalsIgnoreCase("vertical")) {
            msg += prf.getVertical();
        } else if(value.equalsIgnoreCase("vertical-limit")) {
            msg += prf.getVerticalLimit();
        } else if(value.equalsIgnoreCase("extra-horizontal")) {
            msg += prf.getExtraHorizontal();
        } else if(value.equalsIgnoreCase("extra-vertical")) {
            msg += prf.getExtraVertical();
        }
        return msg;
    }

    private void knockbackCommandMain(CommandSender sender) {
        sender.sendMessage(separator);
        sender.sendMessage("\n" + "§3Knockback Profiles:\n\n"); // most people make this smaller/simpler but for a lot of people its easier to just see them all

        for (KnockBackProfile profile : CelestialSpigot.INSTANCE.getConfig().getKbProfiles()) {
            boolean current = CelestialSpigot.INSTANCE.getConfig().getCurrentKb().getName().equals(profile.getName());
            sender.sendMessage(profile.getName() + (current ? ChatColor.GREEN + " [Active]" : ""));

            for (String values : profile.getValuesString()) {
                sender.sendMessage(" * §b" + values);
            }
            sender.sendMessage("");
        }
        sender.sendMessage(separator);
    }

    private boolean isProfileName(String name) {
        for (KnockBackProfile profile : CelestialSpigot.INSTANCE.getConfig().getKbProfiles()) {
            if (profile.getName().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
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

    private void sendHelp(CommandSender sender) {
        sender.sendMessage(help);
    }

}
