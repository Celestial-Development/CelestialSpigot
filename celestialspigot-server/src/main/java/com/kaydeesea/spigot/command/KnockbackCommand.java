package com.kaydeesea.spigot.command;

import com.kaydeesea.spigot.knockback.*;
import com.kaydeesea.spigot.knockback.impl.BedWarsTypeKnockbackProfile;
import com.kaydeesea.spigot.knockback.impl.DetailedTypeKnockbackProfile;
import com.kaydeesea.spigot.knockback.impl.NormalTypeKnockbackProfile;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.*;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import com.kaydeesea.spigot.CelestialSpigot;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class KnockbackCommand extends Command {
    private static final Map<String, String> ALIAS_MAP = new HashMap<>();

    static {
        ALIAS_MAP.put("horfriction", "horizontalfriction");
        ALIAS_MAP.put("verfriction", "verticalfriction");
        ALIAS_MAP.put("fri", "friction");
        ALIAS_MAP.put("hor", "horizontal");
        ALIAS_MAP.put("vert", "vertical");
        ALIAS_MAP.put("extrahor", "extrahorizontal");
        ALIAS_MAP.put("extravert", "extravertical");
        ALIAS_MAP.put("maxrangereduction", "max-range-reduction");
        ALIAS_MAP.put("rangefactor", "range-factor");
        ALIAS_MAP.put("startrangereduction", "start-range-reduction");
        ALIAS_MAP.put("wtap", "w-tap");
        ALIAS_MAP.put("slowdownboolean", "slowdown-boolean");
        ALIAS_MAP.put("frictionboolean", "friction-boolean");
        ALIAS_MAP.put("slowdownval", "slowdown-value");
        ALIAS_MAP.put("slowdownvalue", "slowdown-value");
        ALIAS_MAP.put("vertmax", "vertical-limit");
        ALIAS_MAP.put("verticalmax", "vertical-limit");
        ALIAS_MAP.put("verticallimit", "vertical-limit");
    }
    private final String separator = "§7§m-----------------------------";

    private final String[] help = Stream.of(
            "",
                    "§3Knockback Commands:",
                    " * §b/knockback §flist",
                    " * §b/knockback §freload",
                    " * §b/knockback §fimplementations",
                    " * §b/knockback §finfo §7<profile>",
                    " * §b/knockback §fcreate §7<profile> <type>",
                    " * §b/knockback §fdelete §7<profile>",
                    " * §b/knockback §fload §7<profile>",
                    " * §b/knockback §fedit §7<profile> <variable> <value>", // far better than the old system
                    " * §b/knockback §fset §7<profile> <player>",
                    ""
            )
            .toArray(String[]::new);
    private final List<String> SUB_COMMANDS = Arrays.asList(
            "list",
            "reload",
            "implementations",
            "create",
            "delete",
            "load",
            "info",
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

        if (command.equalsIgnoreCase("delete") || command.equalsIgnoreCase("remove")) {
            if (args.length < 2) {
                sender.sendMessage("§cUsage: /knockback delete <profile_name>");
                return true;
            }
            if (CelestialSpigot.INSTANCE.getKnockBack().getCurrentKb().getName().equalsIgnoreCase(args[1])) {
                sender.sendMessage("§cYou cannot delete the profile that is being used.");
                return true;
            }
            KnockBackProfile profile = CelestialSpigot.INSTANCE.getKnockBack().getKbProfileByName(args[1]);
            if (profile != null) {
                CelestialSpigot.INSTANCE.getKnockBack().getKbProfiles().remove(profile);
                for (String value : profile.getValues()) {
                    CelestialSpigot.INSTANCE.getKnockBack().getConfig().set("knockback.profiles." + args[1]+"."+value, null);
                }
                CelestialSpigot.INSTANCE.getKnockBack().getConfig().set("knockback.profiles." + args[1], null);
                CelestialSpigot.INSTANCE.getKnockBack().save();
                sender.sendMessage("§aThe profile §e" + args[1] + " §ahas been removed.");
                return true;
            } else {
                sender.sendMessage("§cThis profile doesn't exist.");
            }
        }
        else if (command.equalsIgnoreCase("load") || command.equalsIgnoreCase("setactive")) {
            if (args.length < 2) {
                sender.sendMessage("§cUsage: /knockback load <profile_name>");
                return true;
            }
            KnockBackProfile profile = CelestialSpigot.INSTANCE.getKnockBack().getKbProfileByName(args[1]);
            if (profile != null) {
                if (CelestialSpigot.INSTANCE.getKnockBack().getCurrentKb().getName().equalsIgnoreCase(args[1])) {
                    sender.sendMessage("§cThis profile is loaded.");
                    return true;
                }
                CelestialSpigot.INSTANCE.getKnockBack().setCurrentKb(profile);
                for (Player onlinePlayer : Bukkit.getServer().getOnlinePlayers()) {
                    onlinePlayer.setKnockbackProfile(profile);
                }
                CelestialSpigot.INSTANCE.getKnockBack().getConfig().set("knockback.current", profile.getName());
                CelestialSpigot.INSTANCE.getKnockBack().save();
                sender.sendMessage("§aThe profile §e" + args[1] + " §ahas been loaded.");
                return true;
            } else {
                sender.sendMessage("§cThis profile doesn't exist.");
            }
        }
        else if (command.equalsIgnoreCase("info") || command.equalsIgnoreCase("information") || command.equalsIgnoreCase("view")) {
            if (args.length < 2) {
                sender.sendMessage("§cUsage: /knockback info <profile_name>");
                return true;
            }
            KnockBackProfile profile = CelestialSpigot.INSTANCE.getKnockBack().getKbProfileByName(args[1]);
            if (profile != null) {
                sendKnockbackInfo(sender, profile);
            } else {
                sender.sendMessage("§cThis profile doesn't exist.");
            }
        }
        else if (command.equalsIgnoreCase("implementations")) {
            StringBuilder types = new StringBuilder();
            for (ProfileType value : ProfileType.values()) {
                types.append(value.name()).append(" ");
            }
            sender.sendMessage("§bTypes: §3" + types);
        }
        else if (command.equalsIgnoreCase("set")) {
            if (args.length < 3) {
                sender.sendMessage("§cUsage: /knockback set <profile_name> <player>");
                return true;
            }
            KnockBackProfile profile = CelestialSpigot.INSTANCE.getKnockBack().getKbProfileByName(args[1]);
            if (profile == null) {
                sender.sendMessage("§cA profile with that name could not be found.");
                return true;
            }
            Player target = Bukkit.getPlayer(args[2]);
            if (target == null) {
                sender.sendMessage("§cThat player is not online.");
                return true;
            }
            sender.sendMessage("§aSuccessfully changed player knockback profile.");
            target.setKnockbackProfile(profile);
        }
        else if (command.equalsIgnoreCase("reload")) {
            if(CelestialSpigot.INSTANCE.getKnockBack().reload()) {
                sender.sendMessage("§aSuccessfully reloaded the knockback.yml file.");
            } else {
                sender.sendMessage("§cThere was an error while reloading the knockback.yml file, please correct syntax errors.");
            }
        }
        else if (command.equalsIgnoreCase("create") || command.equalsIgnoreCase("add")) {
            if (args.length < 3) {
                sender.sendMessage("§cUsage: /knockback create <profile_name> <type>");
                return true;
            }
            if (!isProfileName(args[1])) {
                if (args[2].equalsIgnoreCase("normal")) {
                    NormalTypeKnockbackProfile profile = new NormalTypeKnockbackProfile(args[1]);
                    CelestialSpigot.INSTANCE.getKnockBack().getKbProfiles().add(profile);
                    profile.save();
                    sender.sendMessage("§aThe profile §e" + args[1] + " §ahas been created.");
                    sendKnockbackInfo(sender, profile);
                }
                else if(args[2].equalsIgnoreCase("bedwars")) {
                    BedWarsTypeKnockbackProfile profile = new BedWarsTypeKnockbackProfile(args[1]);
                    CelestialSpigot.INSTANCE.getKnockBack().getKbProfiles().add(profile);
                    profile.save();
                    sender.sendMessage("§aThe profile §e" + args[1] + " §ahas been created.");
                    sendKnockbackInfo(sender, profile);
                }
                else if(args[2].equalsIgnoreCase("detailed")) {
                    DetailedTypeKnockbackProfile profile = new DetailedTypeKnockbackProfile(args[1]);
                    CelestialSpigot.INSTANCE.getKnockBack().getKbProfiles().add(profile);
                    profile.save();
                    sender.sendMessage("§aThe profile §e" + args[1] + " §ahas been created.");
                    sendKnockbackInfo(sender, profile);
                }
                else {
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
        }
        else if (command.equalsIgnoreCase("edit") || command.equalsIgnoreCase("modify")) {
            if (args.length < 4) {
                sender.sendMessage("§cUsage: /knockback edit <profile_name> <property> <value>");
                return true;
            }
            KnockBackProfile profile = CelestialSpigot.INSTANCE.getKnockBack().getKbProfileByName(args[1].toLowerCase());
            if (profile == null) {
                sender.sendMessage("§cThis profile doesn't exist.");
                return true;
            }
            String input = args[2].toLowerCase();
            String resolvedKey = ALIAS_MAP.getOrDefault(input, input);

            // Case-insensitive search from profile values
            String s = profile.getValues().stream()
                    .filter(v -> v.equalsIgnoreCase(resolvedKey))
                    .findFirst()
                    .orElse(""); // or null, or handle not found case
            if (!s.isEmpty()) {
                if (profile instanceof DetailedTypeKnockbackProfile) {
                    if(((DetailedTypeKnockbackProfile) profile).isValueBoolean(s)) {
                        if (!args[3].equalsIgnoreCase("false") && !args[3].equalsIgnoreCase("true")) {
                            sender.sendMessage("§4" + args[3] + " §c is not a boolean (true/false).");
                            return true;
                        }
                        boolean value = Boolean.parseBoolean(args[3]);
                        modifyDetailedTypeProfile((DetailedTypeKnockbackProfile) profile, s, value);
                        sender.sendMessage("§aChanged §b" + profile.getName() + "§a's §b" + s + " §asetting to §b" + value + "§a.");
                        return true;
                    }
                    if (!org.apache.commons.lang3.math.NumberUtils.isNumber(args[3])) {
                        sender.sendMessage("§4" + args[3] + " §c is not a number.");
                        return true;
                    }
                    double value = Double.parseDouble(args[3]);
                    modifyDetailedTypeProfile((DetailedTypeKnockbackProfile) profile, s, value);
                    sender.sendMessage("§aChanged §b" + profile.getName() + "§a's §b" + s + " §asetting to §b" + value + "§a.");

                }
                if (profile instanceof NormalTypeKnockbackProfile) {
                    if (!org.apache.commons.lang3.math.NumberUtils.isNumber(args[3])) {
                        sender.sendMessage("§4" + args[3] + " §c is not a number.");
                        return true;
                    }
                    double value = Double.parseDouble(args[3]);
                    modifyNormalTypeProfile((NormalTypeKnockbackProfile) profile, s, value);
                    sender.sendMessage("§aChanged §b" + profile.getName() + "§a's §b" + s + " §asetting to §b" + value + "§a.");
                }
                if (profile instanceof BedWarsTypeKnockbackProfile) {
                    if(((BedWarsTypeKnockbackProfile) profile).isValueBoolean(s)) {
                        if (!args[3].equalsIgnoreCase("false") && !args[3].equalsIgnoreCase("true")) {
                            sender.sendMessage("§4" + args[3] + " §c is not a boolean (true/false).");
                            return true;
                        }
                        boolean value = Boolean.parseBoolean(args[3]);
                        modifyBedWarsTypeProfile((BedWarsTypeKnockbackProfile) profile, s, value);
                        sender.sendMessage("§aChanged §b" + profile.getName() + "§a's §b" + s + " §asetting to §b" + value + "§a.");
                        return true;
                    }
                    if (!org.apache.commons.lang3.math.NumberUtils.isNumber(args[3])) {
                        sender.sendMessage("§4" + args[3] + " §c is not a number.");
                        return true;
                    }
                    double value = Double.parseDouble(args[3]);
                    modifyBedWarsTypeProfile((BedWarsTypeKnockbackProfile) profile, s, value);
                    sender.sendMessage("§aChanged §b" + profile.getName() + "§a's §b" + s + " §asetting to §b" + value + "§a.");
                }
            } else {
                sender.sendMessage("§cCouldn't find a §4" + args[2] + " §cproperty in knockback profile " + profile.getName() + ".");
            }
        }
        else if (command.equalsIgnoreCase("list")) {
            knockbackCommandMain(sender);
        }
        else {
            sendHelp(sender);
        }

        return true;

    }

    private static void modifyDetailedTypeProfile(DetailedTypeKnockbackProfile profile, String s, double value) {
        if (s.equalsIgnoreCase("friction-horizontal")) {
            profile.setFrictionH(value);
        } else if (s.equalsIgnoreCase("friction-vertical")) {
            profile.setFrictionY(value);
        } else if (s.equalsIgnoreCase("horizontal")) {
            profile.setHorizontal(value);
        } else if (s.equalsIgnoreCase("vertical")) {
            profile.setVertical(value);
        } else if (s.equalsIgnoreCase("vertical-limit")) {
            profile.setVerticalLimit(value);
        } else if (s.equalsIgnoreCase("ground-horizontal")) {
            profile.setGroundH(value);
        } else if (s.equalsIgnoreCase("ground-vertical")) {
            profile.setGroundV(value);
        } else if (s.equalsIgnoreCase("sprint-horizontal")) {
            profile.setSprintH(value);
        } else if (s.equalsIgnoreCase("sprint-vertical")) {
            profile.setSprintV(value);
        } else if (s.equalsIgnoreCase("slowdown")) {
            profile.setSlowdown(value);
        } else if (s.equalsIgnoreCase("inherit-horizontal-value")) {
            profile.setInheritHValue(value);
        } else if (s.equalsIgnoreCase("inherit-vertical-value")) {
            profile.setInheritYValue(value);
        } else if (s.equalsIgnoreCase("hit-delay")) {
            profile.setHitDelay((int) value);
            for (Player onlinePlayer : Bukkit.getServer().getOnlinePlayers()) {
                if(onlinePlayer.getKnockbackProfile() == profile) {
                    ((CraftPlayer) onlinePlayer).getHandle().maxNoDamageTicks = profile.getHitDelay();
                }
            }
        }
        profile.save();
    }

    private static void modifyDetailedTypeProfile(DetailedTypeKnockbackProfile profile, String s, boolean value) {
        if (s.equalsIgnoreCase("enable-vertical-limit")) {
            profile.setEnableVerticalLimit(value);
        } else if (s.equalsIgnoreCase("stop-sprint")) {
            profile.setStopSprint(value);
        } else if (s.equalsIgnoreCase("inherit-horizontal")) {
            profile.setInheritH(value);
        } else if (s.equalsIgnoreCase("inherit-vertical")) {
            profile.setInheritY(value);
        }
        profile.save();
    }

    private static void modifyBedWarsTypeProfile(BedWarsTypeKnockbackProfile profile, String s, double value) {
        if (s.equalsIgnoreCase("friction")) {
            profile.setFrictionValue(value);
        } else if (s.equalsIgnoreCase("horizontal")) {
            profile.setHorizontal(value);
        } else if (s.equalsIgnoreCase("vertical")) {
            profile.setVertical(value);
        } else if (s.equalsIgnoreCase("vertical-limit")) {
            profile.setVerticalLimit(value);
        } else if(s.equalsIgnoreCase("max-range-reduction")) {
            profile.setMaxRangeReduction(value);
        } else if(s.equalsIgnoreCase("range-factor")) {
            profile.setRangeFactor(value);
        } else if(s.equalsIgnoreCase("start-range-reduction")) {
            profile.setStartRangeReduction(value);
        } else if(s.equalsIgnoreCase("slowdown-value")) {
            profile.setSlowdownValue(value);
        } else if(s.equalsIgnoreCase("hit-delay")) {
            profile.setHitDelay((int) value);
            for (Player onlinePlayer : Bukkit.getServer().getOnlinePlayers()) {
                if(onlinePlayer.getKnockbackProfile() == profile) {
                    ((CraftPlayer) onlinePlayer).getHandle().maxNoDamageTicks = profile.getHitDelay();
                }
            }
        }
        profile.save();
    }
    private static void modifyBedWarsTypeProfile(BedWarsTypeKnockbackProfile profile, String s, boolean value) {
        if (s.equalsIgnoreCase("w-tap")) {
            profile.setWTap(value);
        } else if (s.equalsIgnoreCase("slowdown-boolean")) {
            profile.setSlowdownBoolean(value);
        } else if (s.equalsIgnoreCase("friction-boolean")) {
            profile.setFriction(value);
        }
        profile.save();
    }

    private static void modifyNormalTypeProfile(NormalTypeKnockbackProfile profile, String s, double value) {
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
        } else if(s.equalsIgnoreCase("hit-delay")) {
            profile.setHitDelay((int) value);
            for (Player onlinePlayer : Bukkit.getServer().getOnlinePlayers()) {
                if(onlinePlayer.getKnockbackProfile() == profile) {
                    ((CraftPlayer) onlinePlayer).getHandle().maxNoDamageTicks = profile.getHitDelay();
                }
            }
        }
        profile.save();
    }

    private void sendKnockbackInfo(CommandSender sender, KnockBackProfile profile) {
        sender.sendMessage(separator);
        String name = "§bName: §3"+profile.getName();
        sender.sendMessage(name);
        String type = "§bType: §3"+profile.getType().name();
        sender.sendMessage(type);
        if(profile instanceof DetailedTypeKnockbackProfile) {
            DetailedTypeKnockbackProfile prf = (DetailedTypeKnockbackProfile) profile;
            for (String value : prf.getValues()) {
                String msg = getDetailedKnockbackInfo(value, prf);
                TextComponent message = new TextComponent(msg);
                BaseComponent[] component = new ComponentBuilder("Click to edit "+value).color(ChatColor.AQUA).create();
                message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, component));
                message.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/knockback edit "+profile.getName()+" "+value+" "));
                if(sender instanceof  Player) {
                    ((Player)sender).spigot().sendMessage(message);
                } else {
                    sender.sendMessage(msg);
                }
            }
        }
        if(profile instanceof NormalTypeKnockbackProfile) {
            NormalTypeKnockbackProfile prf = (NormalTypeKnockbackProfile) profile;
            for (String value : prf.getValues()) {
                String msg = getNormalKnockbackInfo(value, prf);
                TextComponent message = new TextComponent(msg);
                BaseComponent[] component = new ComponentBuilder("Click to edit "+value).color(ChatColor.AQUA).create();
                message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, component));
                message.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/knockback edit "+profile.getName()+" "+value+" "));
                if(sender instanceof  Player) {
                    ((Player)sender).spigot().sendMessage(message);
                } else {
                    sender.sendMessage(msg);
                }
            }
        }
        if(profile instanceof BedWarsTypeKnockbackProfile) {
            BedWarsTypeKnockbackProfile prf = (BedWarsTypeKnockbackProfile) profile;
            for (String value : prf.getValues()) {
                String msg = getBedWarsKnockbackInfo(value, prf);
                TextComponent message = new TextComponent(msg);
                BaseComponent[] component = new ComponentBuilder("Click to edit "+value).color(ChatColor.AQUA).create();
                message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, component));
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
        String msg = "§b"+ value +": §3";
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
        } else if(value.equalsIgnoreCase("hit-delay")) {
            msg += prf.getHitDelay();
        }
        return msg;
    }
    private static String getDetailedKnockbackInfo(String value, DetailedTypeKnockbackProfile prf) {
        String msg = "§b" + value + ": §3";
        if (value.equalsIgnoreCase("friction-horizontal")) {
            msg += prf.getFrictionH();
        } else if (value.equalsIgnoreCase("friction-vertical")) {
            msg += prf.getFrictionY();
        } else if (value.equalsIgnoreCase("horizontal")) {
            msg += prf.getHorizontal();
        } else if (value.equalsIgnoreCase("vertical")) {
            msg += prf.getVertical();
        } else if (value.equalsIgnoreCase("vertical-limit")) {
            msg += prf.getVerticalLimit();
        } else if (value.equalsIgnoreCase("ground-horizontal")) {
            msg += prf.getGroundH();
        } else if (value.equalsIgnoreCase("ground-vertical")) {
            msg += prf.getGroundV();
        } else if (value.equalsIgnoreCase("sprint-horizontal")) {
            msg += prf.getSprintH();
        } else if (value.equalsIgnoreCase("sprint-vertical")) {
            msg += prf.getSprintV();
        } else if (value.equalsIgnoreCase("slowdown")) {
            msg += prf.getSlowdown();
        } else if (value.equalsIgnoreCase("inherit-horizontal")) {
            msg += prf.isInheritH();
        } else if (value.equalsIgnoreCase("inherit-vertical")) {
            msg += prf.isInheritY();
        } else if (value.equalsIgnoreCase("inherit-horizontal-value")) {
            msg += prf.getInheritHValue();
        } else if (value.equalsIgnoreCase("inherit-vertical-value")) {
            msg += prf.getInheritYValue();
        } else if (value.equalsIgnoreCase("enable-vertical-limit")) {
            msg += prf.isEnableVerticalLimit();
        } else if (value.equalsIgnoreCase("stop-sprint")) {
            msg += prf.isStopSprint();
        } else if(value.equalsIgnoreCase("hit-delay")) {
            msg += prf.getHitDelay();
        }
        return msg;
    }
    private static String getBedWarsKnockbackInfo(String value, BedWarsTypeKnockbackProfile prf) {
        String msg = "§b"+ value +": §3";
        if(value.equalsIgnoreCase("friction")) {
            msg += prf.getFrictionValue();
        } else if(value.equalsIgnoreCase("horizontal")) {
            msg += prf.getHorizontal();
        } else if(value.equalsIgnoreCase("vertical")) {
            msg += prf.getVertical();
        } else if(value.equalsIgnoreCase("vertical-limit")) {
            msg += prf.getVerticalLimit();
        } else if(value.equalsIgnoreCase("max-range-reduction")) {
            msg += prf.getMaxRangeReduction();
        } else if(value.equalsIgnoreCase("range-factor")) {
            msg += prf.getRangeFactor();
        } else if(value.equalsIgnoreCase("start-range-reduction")) {
            msg += prf.getStartRangeReduction();
        } else if(value.equalsIgnoreCase("w-tap")) {
            msg += prf.isWTap();
        } else if(value.equalsIgnoreCase("slowdown-boolean")) {
            msg += prf.isSlowdownBoolean();
        } else if(value.equalsIgnoreCase("friction-boolean")) {
            msg += prf.isFriction();
        } else if(value.equalsIgnoreCase("hit-delay")) {
            msg += prf.getHitDelay();
        } else if(value.equalsIgnoreCase("slowdown-value")) {
            msg += prf.getSlowdownValue();
        }

        return msg;
    }

    private void knockbackCommandMain(CommandSender sender) {
        sender.sendMessage(separator);

        sender.sendMessage("§3KnockBack Profiles:"); // most people make this smaller/simpler but for a lot of people its easier to just see them all

        for (KnockBackProfile profile : CelestialSpigot.INSTANCE.getKnockBack().getKbProfiles()) {
            String s = "§3Name: §b"+profile.getName()+" §3Type: §b"+profile.getType().name();
            if(CelestialSpigot.INSTANCE.getKnockBack().getCurrentKb() == profile) s += " §aCurrent";

            TextComponent message = new TextComponent(s);
            message.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/knockback info "+profile.getName()));
            if(sender instanceof  Player) {
                ((Player)sender).spigot().sendMessage(message);
            } else {
                sender.sendMessage(s);
            }
        }
        sender.sendMessage(separator);

    }

    private boolean isProfileName(String name) {
        for (KnockBackProfile profile : CelestialSpigot.INSTANCE.getKnockBack().getKbProfiles()) {
            if (profile.getName().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        if (args.length > 0 && SUB_COMMANDS.contains(args[0].toLowerCase())) {
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

    private void sendHelp(CommandSender sender) {
        sender.sendMessage(help);
    }

}
