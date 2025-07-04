package com.kaydeesea.spigot.command.knockback;

import com.kaydeesea.spigot.knockback.*;
import com.kaydeesea.spigot.knockback.impl.BedWarsTypeKnockbackProfile;
import com.kaydeesea.spigot.knockback.impl.ComboTypeKnockbackProfile;
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
                sender.sendMessage("§cYou cannot delete the profile that is being used. Please select any other profile");
                return true;
            }
            KnockBackProfile profile = CelestialSpigot.INSTANCE.getKnockBack().getKbProfileByName(args[1]);
            if (profile != null) {
                CelestialSpigot.INSTANCE.getKnockBack().deleteKB(profile);
                sender.sendMessage("§aThe profile §e" + profile.getName() + " §ahas been removed.");
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
                sender.sendMessage("§aThe profile §e" + args[1] + " §ahas been loaded.");
                sender.sendMessage("§2The profile has been applied to all players. §b(besides players with KB-overrides per world.)");
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
            String name = args[1];
            String typeRaw = args[2];

            ProfileType type = ProfileType.fromRaw(typeRaw);
            if (type == null) {
                StringBuilder types = new StringBuilder();
                for (ProfileType value : ProfileType.values()) {
                    types.append(value.raw).append(" ");
                }
                sender.sendMessage("§cSpecify a valid knockback type. Types: " + types);
                return true;
            }

            KnockBackProfile profile;
            if(type == ProfileType.NORMAL) profile = new NormalTypeKnockbackProfile(name);
            else if(type == ProfileType.BEDWARS) profile = new BedWarsTypeKnockbackProfile(name);
            else if(type == ProfileType.COMBO) profile = new ComboTypeKnockbackProfile(name);
            else if(type == ProfileType.DETAILED) profile = new DetailedTypeKnockbackProfile(name);
            else return true;
            CelestialSpigot.INSTANCE.getKnockBack().createKB(profile);
            sender.sendMessage("§aThe profile §e" + args[1] + " §ahas been created. §bRemember to set it as active.");
            sendKnockbackInfo(sender, profile);

            return true;
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
                    DetailedTypeKnockbackProfile dkb = (DetailedTypeKnockbackProfile) profile;

                    DetailedTypeKnockbackProfile.DetailedValues v = DetailedTypeKnockbackProfile.DetailedValues.getValueByKey(s);
                    if(v == null) {
                        sender.sendMessage("§4A wierd error occurred.");
                        sender.sendMessage("§4Please contact the developer KayDeeSea.");
                        return true;
                    }
                    Object parsedValue;

                    try {
                        if (v.isBoolean()) {
                            parsedValue = Boolean.parseBoolean(args[3]);
                        } else if (v.isDouble()) {
                            parsedValue = Double.parseDouble(args[3]);
                        } else if (v.isInteger()) {
                            parsedValue = Integer.parseInt(args[3]);
                        } else {
                            sender.sendMessage("§cUnsupported value type for " + v.getKey());
                            return true;
                        }
                    } catch (Exception ex) {
                        sender.sendMessage("§4" + args[3] + " §c is not a valid " + v.getType().getSimpleName() + ".");
                        return true;
                    }

                    modifyDetailedTypeProfile(dkb, v, parsedValue);
                    sender.sendMessage("§aChanged §b" + profile.getName() + "§a's §b" + s + "§a setting to §b" + parsedValue.toString() + "§a.");
                }
                else if (profile instanceof NormalTypeKnockbackProfile) {
                    NormalTypeKnockbackProfile nkb = (NormalTypeKnockbackProfile) profile;

                    NormalTypeKnockbackProfile.NormalValues v = NormalTypeKnockbackProfile.NormalValues.getValueByKey(s);
                    if(v == null) {
                        sender.sendMessage("§4A wierd error occurred.");
                        sender.sendMessage("§4Please contact the developer KayDeeSea.");
                        return true;
                    }
                    Object parsedValue;

                    try {
                        if (v.isDouble()) {
                            parsedValue = Double.parseDouble(args[3]);
                        } else if (v.isInteger()) {
                            parsedValue = Integer.parseInt(args[3]);
                        } else {
                            sender.sendMessage("§cUnsupported value type for " + v.getKey());
                            return true;
                        }
                    } catch (Exception ex) {
                        sender.sendMessage("§4" + args[3] + " §c is not a valid " + v.getType().getSimpleName() + ".");
                        return true;
                    }

                    modifyNormalTypeProfile(nkb, v, parsedValue);
                    sender.sendMessage("§aChanged §b" + profile.getName() + "§a's §b" + s + "§a setting to §b" + parsedValue.toString() + "§a.");
                }
                else if (profile instanceof BedWarsTypeKnockbackProfile) {
                    BedWarsTypeKnockbackProfile bkb = (BedWarsTypeKnockbackProfile) profile;

                    BedWarsTypeKnockbackProfile.BedWarsValues v = BedWarsTypeKnockbackProfile.BedWarsValues.getValueByKey(s);
                    if(v == null) {
                        sender.sendMessage("§4A wierd error occurred.");
                        sender.sendMessage("§4Please contact the developer KayDeeSea.");
                        return true;
                    }
                    Object parsedValue;

                    try {
                        if (v.isBoolean()) {
                            parsedValue = Boolean.parseBoolean(args[3]);
                        } else if (v.isDouble()) {
                            parsedValue = Double.parseDouble(args[3]);
                        } else if (v.isInteger()) {
                            parsedValue = Integer.parseInt(args[3]);
                        } else {
                            sender.sendMessage("§cUnsupported value type for " + v.getKey());
                            return true;
                        }
                    } catch (Exception ex) {
                        sender.sendMessage("§4" + args[3] + " §c is not a valid " + v.getType().getSimpleName() + ".");
                        return true;
                    }

                    modifyBedWarsTypeProfile(bkb, v, parsedValue);
                    sender.sendMessage("§aChanged §b" + profile.getName() + "§a's §b" + s + "§a setting to §b" + parsedValue.toString() + "§a.");
                }
                else if (profile instanceof ComboTypeKnockbackProfile) {
                    ComboTypeKnockbackProfile ckb = (ComboTypeKnockbackProfile) profile;

                    ComboTypeKnockbackProfile.ComboValues v = ComboTypeKnockbackProfile.ComboValues.getValueByKey(s);
                    if(v == null) {
                        sender.sendMessage("§4A wierd error occurred.");
                        sender.sendMessage("§4Please contact the developer KayDeeSea.");
                        return true;
                    }
                    Object parsedValue;

                    try {
                        if (v.isDouble()) {
                            parsedValue = Double.parseDouble(args[3]);
                        } else if (v.isInteger()) {
                            parsedValue = Integer.parseInt(args[3]);
                        } else {
                            sender.sendMessage("§cUnsupported value type for " + v.getKey());
                            return true;
                        }
                    } catch (Exception ex) {
                        sender.sendMessage("§4" + args[3] + " §c is not a valid " + v.getType().getSimpleName() + ".");
                        return true;
                    }

                    modifyComboTypeProfile(ckb, v, parsedValue);
                    sender.sendMessage("§aChanged §b" + profile.getName() + "§a's §b" + s + "§a setting to §b" + parsedValue.toString() + "§a.");

                }
            } else {
                sender.sendMessage("§cCouldn't find a §4" + args[2] + "§c property in knockback profile " + profile.getName() + ".");
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

    private static void modifyDetailedTypeProfile(DetailedTypeKnockbackProfile profile, DetailedTypeKnockbackProfile.DetailedValues v, Object value) {
        profile.setValueByKey(v, value);
        profile.save();
        if (v == DetailedTypeKnockbackProfile.DetailedValues.HIT_DELAY) {
            for (Player onlinePlayer : Bukkit.getServer().getOnlinePlayers()) {
                if (onlinePlayer.getKnockbackProfile() == profile) {
                    ((CraftPlayer) onlinePlayer).getHandle().maxNoDamageTicks = profile.getHitDelay();
                }
            }
        }
    }
    private static void modifyBedWarsTypeProfile(BedWarsTypeKnockbackProfile profile, BedWarsTypeKnockbackProfile.BedWarsValues v, Object value) {
        profile.setValueByKey(v, value);
        profile.save();
        if (v == BedWarsTypeKnockbackProfile.BedWarsValues.HIT_DELAY) {
            for (Player onlinePlayer : Bukkit.getServer().getOnlinePlayers()) {
                if (onlinePlayer.getKnockbackProfile() == profile) {
                    ((CraftPlayer) onlinePlayer).getHandle().maxNoDamageTicks = profile.getHitDelay();
                }
            }
        }
    }
    private static void modifyNormalTypeProfile(NormalTypeKnockbackProfile profile, NormalTypeKnockbackProfile.NormalValues v, Object value) {
        profile.setValueByKey(v, value);
        profile.save();
        if (v == NormalTypeKnockbackProfile.NormalValues.HIT_DELAY) {
            for (Player onlinePlayer : Bukkit.getServer().getOnlinePlayers()) {
                if (onlinePlayer.getKnockbackProfile() == profile) {
                    ((CraftPlayer) onlinePlayer).getHandle().maxNoDamageTicks = profile.getHitDelay();
                }
            }
        }
    }
    private static void modifyComboTypeProfile(ComboTypeKnockbackProfile profile, ComboTypeKnockbackProfile.ComboValues v, Object value) {
        profile.setValueByKey(v, value);
        profile.save();
        if (v == ComboTypeKnockbackProfile.ComboValues.HIT_DELAY) {
            for (Player onlinePlayer : Bukkit.getServer().getOnlinePlayers()) {
                if (onlinePlayer.getKnockbackProfile() == profile) {
                    ((CraftPlayer) onlinePlayer).getHandle().maxNoDamageTicks = profile.getHitDelay();
                }
            }
        }
    }

    private void sendKnockbackInfo(CommandSender sender, KnockBackProfile profile) {
        sender.sendMessage(separator);
        String name = "§bName: §3"+profile.getName();
        sender.sendMessage(name);
        ProfileType typeEnum = ProfileType.getByClass(profile.getClass());
        if(typeEnum != null) {
            String type = "§bType: §3" + typeEnum.name();
            sender.sendMessage(type);
        }
        if(profile instanceof DetailedTypeKnockbackProfile) {
            DetailedTypeKnockbackProfile prf = (DetailedTypeKnockbackProfile) profile;
            for (DetailedTypeKnockbackProfile.DetailedValues value : DetailedTypeKnockbackProfile.DetailedValues.values()) {
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
        else if(profile instanceof NormalTypeKnockbackProfile) {
            NormalTypeKnockbackProfile prf = (NormalTypeKnockbackProfile) profile;
            for (NormalTypeKnockbackProfile.NormalValues value : NormalTypeKnockbackProfile.NormalValues.values()) {
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
        else if(profile instanceof BedWarsTypeKnockbackProfile) {
            BedWarsTypeKnockbackProfile prf = (BedWarsTypeKnockbackProfile) profile;
            for (BedWarsTypeKnockbackProfile.BedWarsValues value : BedWarsTypeKnockbackProfile.BedWarsValues.values()) {
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
        else if(profile instanceof ComboTypeKnockbackProfile) {
            ComboTypeKnockbackProfile prf = (ComboTypeKnockbackProfile) profile;
            for (ComboTypeKnockbackProfile.ComboValues value : ComboTypeKnockbackProfile.ComboValues.values()) {
                String msg = getComboKnockbackInfo(value, prf);
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

    private static String getNormalKnockbackInfo(NormalTypeKnockbackProfile.NormalValues value, NormalTypeKnockbackProfile prf) {
        return "§b"+ value +": §3"+prf.getValueByKey(value);
    }
    private static String getDetailedKnockbackInfo(DetailedTypeKnockbackProfile.DetailedValues value, DetailedTypeKnockbackProfile prf) {
        return "§b"+ value +": §3"+prf.getValueByKey(value);
    }
    private static String getBedWarsKnockbackInfo(BedWarsTypeKnockbackProfile.BedWarsValues value, BedWarsTypeKnockbackProfile prf) {
        return "§b"+ value +": §3"+prf.getValueByKey(value);
    }
    private static String getComboKnockbackInfo(ComboTypeKnockbackProfile.ComboValues value, ComboTypeKnockbackProfile prf) {
        return "§b"+ value +": §3"+prf.getValueByKey(value);
    }

    private void knockbackCommandMain(CommandSender sender) {
        sender.sendMessage(separator);

        sender.sendMessage("§3KnockBack Profiles:"); // most people make this smaller/simpler but for a lot of people its easier to just see them all

        for (KnockBackProfile profile : CelestialSpigot.INSTANCE.getKnockBack().getKbProfiles()) {
            String s = "§3Name: §b"+profile.getName();
            ProfileType typeEnum = ProfileType.getByClass(profile.getClass());
            if(typeEnum != null) {
                s += " §3Type: §b"+typeEnum.name();
            }
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

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        // Make sure the sender has permission (optional, but consistent with execute())
        if (!testPermissionSilent(sender)) {
            return Collections.emptyList();
        }

        // If typing the first argument, always suggest sub-commands:
        if (args.length == 1) {
            String partial = args[0].toLowerCase();
            return SUB_COMMANDS.stream()
                    .filter(s -> s.startsWith(partial))
                    .collect(Collectors.toList());
        }

        // If typing the second argument, we look at which sub-command was chosen:
        String base = args[0].toLowerCase();
        // Gather all existing profile names:
        List<String> allProfiles = CelestialSpigot.INSTANCE.getKnockBack()
                .getKbProfiles()
                .stream()
                .map(KnockBackProfile::getName)
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .collect(Collectors.toList());

        // Gather all ProfileType raw names (for "create"):
        List<String> allTypes = Arrays.stream(ProfileType.values())
                .map(pt -> pt.raw)
                .collect(Collectors.toList());

        // 2nd argument suggestions:
        if (args.length == 2) {
            String partial = args[1].toLowerCase();

            switch (base) {
                case "delete":
                case "load":
                case "remove":
                case "info":
                case "view":
                    // Suggest profile names:
                    return allProfiles.stream()
                            .filter(p -> p.toLowerCase().startsWith(partial))
                            .collect(Collectors.toList());

                case "set":
                    // For "/knockback set <profile> <player>", suggest profile names here:
                    return allProfiles.stream()
                            .filter(p -> p.toLowerCase().startsWith(partial))
                            .collect(Collectors.toList());

                case "create":
                case "add":
                    // For "/knockback create <profile> <type>", the 2nd arg is the TYPE:
                    return allTypes.stream()
                            .filter(t -> t.startsWith(partial))
                            .collect(Collectors.toList());

                case "edit":
                case "modify":
                    // For "/knockback edit <profile> <variable> <value>", the 2nd arg is the PROFILE:
                    return allProfiles.stream()
                            .filter(p -> p.toLowerCase().startsWith(partial))
                            .collect(Collectors.toList());

                default:
                    return Collections.emptyList();
            }
        }

        // If typing the third argument:
        if (args.length == 3) {
            String profileName = args[1];
            KnockBackProfile profile = CelestialSpigot.INSTANCE.getKnockBack().getKbProfileByName(profileName);
            if (profile == null) {
                return Collections.emptyList();
            }

            switch (base) {
                case "edit":
                case "modify":
                    // Suggest the list of valid keys/variables on that profile:
                    // We call profile.getValues() to get all available keys (case-insensitive).
                    return profile.getValues().stream()
                            .map(String::toLowerCase)
                            .filter(key -> key.startsWith(args[2].toLowerCase()))
                            .collect(Collectors.toList());

                case "set":
                    // After "/knockback set <profile>", suggest online player names:
                    String partialPlayer = args[2].toLowerCase();
                    return Bukkit.getOnlinePlayers().stream()
                            .map(Player::getName)
                            .filter(n -> n.toLowerCase().startsWith(partialPlayer))
                            .collect(Collectors.toList());

                default:
                    return Collections.emptyList();
            }
        }

        // ok not lying ChatGPT cooked here

        return Collections.emptyList();
    }

    private void sendHelp(CommandSender sender) {
        for (String s : CelestialSpigot.INSTANCE.getConfig().getKnockbackCommand()) {
            sender.sendMessage(s);
        }
    }

}
