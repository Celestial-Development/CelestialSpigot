package com.kaydeesea.spigot.knockback.impl;

import com.kaydeesea.spigot.CelestialSpigot;
import com.kaydeesea.spigot.knockback.DetailedKnockbackProfile;
import com.kaydeesea.spigot.knockback.ProfileType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class DetailedTypeKnockbackProfile implements DetailedKnockbackProfile {
    private String name;
    private double frictionH = 2.0;
    private double frictionY = 2.0;
    private boolean inheritH = true;
    private boolean inheritY = false;
    private double inheritHValue = 1.0;
    private double inheritYValue = 0.5;
    private double horizontal = 0.35;
    private double vertical = 0.35;
    private boolean enableVerticalLimit = false;
    private double verticalLimit = 0.4;
    private double groundH = 1.06;
    private double groundV = 1.0;
    private double sprintH = 1.23;
    private double sprintV = 1.0;
    private boolean stopSprint = true;
    private int hitDelay = 20;
    private double slowdown = 0.5;

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

    public boolean isValueBoolean(String key) {
        return Arrays.stream(DetailedValues.values())
                .filter(DetailedValues::isBoolean)
                .anyMatch(v -> v.getKey().equalsIgnoreCase(key));
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
        FRICTION_HORIZONTAL("friction-horizontal", Double.class),
        FRICTION_VERTICAL("friction-vertical", Double.class),
        HORIZONTAL("horizontal", Double.class),
        VERTICAL("vertical", Double.class),
        VERTICAL_LIMIT("vertical-limit", Double.class),
        GROUND_HORIZONTAL("ground-horizontal", Double.class),
        GROUND_VERTICAL("ground-vertical", Double.class),
        SPRINT_HORIZONTAL("sprint-horizontal", Double.class),
        SPRINT_VERTICAL("sprint-vertical", Double.class),
        SLOWDOWN("slowdown", Double.class),
        HIT_DELAY("hit-delay", Integer.class),
        ENABLE_VERTICAL_LIMIT("enable-vertical-limit", Boolean.class),
        STOP_SPRINT("stop-sprint", Boolean.class),
        INHERIT_HORIZONTAL("inherit-horizontal", Boolean.class),
        INHERIT_VERTICAL("inherit-vertical", Boolean.class),
        INHERIT_HORIZONTAL_VALUE("inherit-horizontal-value", Double.class),
        INHERIT_VERTICAL_VALUE("inherit-vertical-value", Double.class);

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
            case FRICTION_HORIZONTAL:
                return frictionH;
            case FRICTION_VERTICAL:
                return frictionY;
            case HORIZONTAL:
                return horizontal;
            case VERTICAL:
                return vertical;
            case VERTICAL_LIMIT:
                return verticalLimit;
            case GROUND_HORIZONTAL:
                return groundH;
            case GROUND_VERTICAL:
                return groundV;
            case SPRINT_HORIZONTAL:
                return sprintH;
            case SPRINT_VERTICAL:
                return sprintV;
            case SLOWDOWN:
                return slowdown;
            case HIT_DELAY:
                return hitDelay;
            case ENABLE_VERTICAL_LIMIT:
                return enableVerticalLimit;
            case STOP_SPRINT:
                return stopSprint;
            case INHERIT_HORIZONTAL:
                return inheritH;
            case INHERIT_VERTICAL:
                return inheritY;
            case INHERIT_HORIZONTAL_VALUE:
                return inheritHValue;
            case INHERIT_VERTICAL_VALUE:
                return inheritYValue;
            default:
                return 0.0;  // or some default value
        }
    }

    public void setValueByKey(DetailedValues val, Object value) {
        switch (val) {
            case FRICTION_HORIZONTAL:
                frictionH = (Double) value;
                break;
            case FRICTION_VERTICAL:
                frictionY = (Double) value;
                break;
            case HORIZONTAL:
                horizontal = (Double) value;
                break;
            case VERTICAL:
                vertical = (Double) value;
                break;
            case VERTICAL_LIMIT:
                verticalLimit = (Double) value;
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
            case SLOWDOWN:
                slowdown = (Double) value;
                break;
            case HIT_DELAY:
                hitDelay = (Integer) value;
                break;
            case ENABLE_VERTICAL_LIMIT:
                enableVerticalLimit = (Boolean) value;
                break;
            case STOP_SPRINT:
                stopSprint = (Boolean) value;
                break;
            case INHERIT_HORIZONTAL:
                inheritH = (Boolean) value;
                break;
            case INHERIT_VERTICAL:
                inheritY = (Boolean) value;
                break;
            case INHERIT_HORIZONTAL_VALUE:
                inheritHValue = (Double) value;
                break;
            case INHERIT_VERTICAL_VALUE:
                inheritYValue = (Double) value;
                break;
        }
    }


}
