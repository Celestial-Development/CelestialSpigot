package com.kaydeesea.spigot.knockback.impl;

import com.kaydeesea.spigot.CelestialSpigot;
import com.kaydeesea.spigot.knockback.DetailedKnockbackProfile;
import com.kaydeesea.spigot.knockback.ProfileType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class DetailedTypeKnockbackProfile implements DetailedKnockbackProfile {
    private String name;

    private double horizontal = 0.35;
    private double vertical = 0.35;

    private boolean inheritH = true;
    private double inheritHValue = 1.0;

    private boolean inheritY = false;
    private double inheritYValue = 0.5;

    private double frictionH = 2.0;
    private double frictionY = 2.0;

    private double groundH = 1.06;
    private double groundV = 1.0;

    private double sprintH = 1.23;
    private double sprintV = 1.0;

    private boolean enableVerticalLimit = false;
    private double verticalLimit = 0.4;

    private boolean stopSprint = true;
    private double slowdown = 0.5;

    private boolean combo = false;
    private int comboTicks = 10;
    private double comboVelocity = -0.05D;
    private double comboHeight = 2.5D;

    private int hitDelay = 20;

    public DetailedTypeKnockbackProfile(String name, String path, YamlConfiguration config) {
        this.name = name;
        for (DetailedTypeKnockbackProfile.DetailedValues value : DetailedTypeKnockbackProfile.DetailedValues.values()) {
            String a = path + "." + value.getKey();
            if (value.isBoolean()) {
                setValueByKey(value, config.getBoolean(a, (Boolean) getValueByKey(value)));
            } else if (value.isDouble()) {
                setValueByKey(value, config.getDouble(a, (Double) getValueByKey(value)));
            } else if (value.isInteger()) {
                setValueByKey(value, config.getInt(a, (Integer) getValueByKey(value)));
            }
        }
    }

    public DetailedTypeKnockbackProfile(String name) {
        this.name = name;
    }


    @Override
    public @NotNull List<String> getValues() {
        return Arrays
                .stream(DetailedValues.values())
                .map(DetailedValues::getKey)
                .collect(Collectors.toList());
    }

    public void save() {
        final String path = "knockback.profiles." + this.name;
        var config = CelestialSpigot.INSTANCE.getKnockBack().getConfig();

        config.set(path + ".type", ProfileType.DETAILED.name());

        for (DetailedValues val : DetailedValues.values()) {
            String key = path + "." + val.getKey();
            Object value = getValueByKey(val);
            config.set(key, value);
        }

        CelestialSpigot.INSTANCE.getKnockBack().save();
    }
    @Getter
    @RequiredArgsConstructor
    public enum DetailedValues {

        HORIZONTAL("horizontal", Double.class),
        VERTICAL("vertical", Double.class),
        INHERIT_HORIZONTAL("inherit-horizontal", Boolean.class),
        INHERIT_HORIZONTAL_VALUE("inherit-horizontal-value", Double.class),
        INHERIT_VERTICAL_VALUE("inherit-vertical-value", Double.class),
        INHERIT_VERTICAL("inherit-vertical", Boolean.class),
        FRICTION_HORIZONTAL("friction-horizontal", Double.class),
        FRICTION_VERTICAL("friction-vertical", Double.class),
        GROUND_HORIZONTAL("ground-horizontal", Double.class),
        GROUND_VERTICAL("ground-vertical", Double.class),
        SPRINT_HORIZONTAL("sprint-horizontal", Double.class),
        SPRINT_VERTICAL("sprint-vertical", Double.class),
        ENABLE_VERTICAL_LIMIT("enable-vertical-limit", Boolean.class),
        VERTICAL_LIMIT("vertical-limit", Double.class),
        STOP_SPRINT("stop-sprint", Boolean.class),
        SLOWDOWN("slowdown", Double.class),
        COMBO("combo", Boolean.class),
        COMBO_TICKS("combo-ticks", Integer.class),
        COMBO_VELOCITY("combo-velocity", Double.class),
        COMBO_HEIGHT("combo-height", Double.class),

        HIT_DELAY("hit-delay", Integer.class);
        private final String key;
        private final Class<?> type;

 

        public boolean isBoolean() {
            return type == Boolean.class;
        }

        public boolean isDouble() {
            return type == Double.class;
        }

        public boolean isInteger() {
            return type == Integer.class;
        }

        @Override
        public String toString() {
            return key;
        }
        public static DetailedValues getValueByKey(String key) {
            for (DetailedValues value : values()) {
                if(value.key.equalsIgnoreCase(key)) return value;
            }
            return null;
        }
    }
    public Object getValueByKey(DetailedValues val) {
        switch (val) {
            case HORIZONTAL:
                return horizontal;
            case VERTICAL:
                return vertical;
            case INHERIT_HORIZONTAL:
                return inheritH;
            case INHERIT_HORIZONTAL_VALUE:
                return inheritHValue;
            case INHERIT_VERTICAL:
                return inheritY;
            case INHERIT_VERTICAL_VALUE:
                return inheritYValue;
            case FRICTION_HORIZONTAL:
                return frictionH;
            case FRICTION_VERTICAL:
                return frictionY;
            case GROUND_HORIZONTAL:
                return groundH;
            case GROUND_VERTICAL:
                return groundV;
            case SPRINT_HORIZONTAL:
                return sprintH;
            case SPRINT_VERTICAL:
                return sprintV;
            case ENABLE_VERTICAL_LIMIT:
                return enableVerticalLimit;
            case VERTICAL_LIMIT:
                return verticalLimit;
            case STOP_SPRINT:
                return stopSprint;
            case SLOWDOWN:
                return slowdown;
            case COMBO:
                return combo;
            case COMBO_TICKS:
                return comboTicks;
            case COMBO_VELOCITY:
                return comboVelocity;
            case COMBO_HEIGHT:
                return comboHeight;
            case HIT_DELAY:
                return hitDelay;
            default:
                return null; // or throw an exception if preferred
        }
    }

    public void setValueByKey(DetailedValues val, Object value) {
        switch (val) {
            case HORIZONTAL:
                horizontal = (Double) value;
                break;
            case VERTICAL:
                vertical = (Double) value;
                break;
            case INHERIT_HORIZONTAL:
                inheritH = (Boolean) value;
                break;
            case INHERIT_HORIZONTAL_VALUE:
                inheritHValue = (Double) value;
                break;
            case INHERIT_VERTICAL:
                inheritY = (Boolean) value;
                break;
            case INHERIT_VERTICAL_VALUE:
                inheritYValue = (Double) value;
                break;
            case FRICTION_HORIZONTAL:
                frictionH = (Double) value;
                break;
            case FRICTION_VERTICAL:
                frictionY = (Double) value;
                break;
            case GROUND_HORIZONTAL:
                groundH = (Double) value;
                break;
            case GROUND_VERTICAL:
                groundV = (Double) value;
                break;
            case SPRINT_HORIZONTAL:
                sprintH = (Double) value;
                break;
            case SPRINT_VERTICAL:
                sprintV = (Double) value;
                break;
            case ENABLE_VERTICAL_LIMIT:
                enableVerticalLimit = (Boolean) value;
                break;
            case VERTICAL_LIMIT:
                verticalLimit = (Double) value;
                break;
            case STOP_SPRINT:
                stopSprint = (Boolean) value;
                break;
            case SLOWDOWN:
                slowdown = (Double) value;
                break;
            case COMBO:
                combo = (Boolean) value;
                break;
            case COMBO_TICKS:
                comboTicks = (Integer) value;
                break;
            case COMBO_VELOCITY:
                comboVelocity = (Double) value;
                break;
            case COMBO_HEIGHT:
                comboHeight = (Double) value;
                break;
            case HIT_DELAY:
                hitDelay = (Integer) value;
                break;
        }
    }


}
