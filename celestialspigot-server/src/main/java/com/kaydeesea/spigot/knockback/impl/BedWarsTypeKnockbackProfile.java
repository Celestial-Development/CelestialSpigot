package com.kaydeesea.spigot.knockback.impl;

import com.kaydeesea.spigot.CelestialSpigot;
import com.kaydeesea.spigot.knockback.BedWarsKnockbackProfile;
import com.kaydeesea.spigot.knockback.ProfileType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class BedWarsTypeKnockbackProfile implements BedWarsKnockbackProfile {
    private String name;
    private double frictionValue = 2.0D;
    private double horizontal = 0.9055D;
    private double vertical = 0.8835D;
    private double verticalLimit = 0.3534D;
    private double maxRangeReduction = 0.4D;
    private double rangeFactor = 0.2D;
    private double startRangeReduction = 3.0D;
    private int hitDelay = 20;
    private double slowdownValue = 0.6;
    private boolean wTap = false;
    private boolean slowdownBoolean = false;
    private boolean friction = false;

    public BedWarsTypeKnockbackProfile(String name) {
        this.name = name;
    }


    @Override
    public @NotNull List<String> getValues() {
        return Arrays
                .stream(BedWarsValues.values())
                .map(BedWarsValues::getKey)
                .collect(Collectors.toList());
    }

    public boolean isValueBoolean(String key) {
        return Arrays.stream(BedWarsValues.values())
                .filter(BedWarsValues::isBoolean)
                .anyMatch(v -> v.getKey().equalsIgnoreCase(key));
    }

    public void save() {
        final String path = "knockback.profiles." + this.name;
        var config = CelestialSpigot.INSTANCE.getKnockBack().getConfig();

        config.set(path + ".type", ProfileType.BEDWARS.name());

        for (BedWarsValues val : BedWarsValues.values()) {
            String key = path + "." + val.getKey();
            Object value = getValueByKey(val);
            config.set(key, value);
        }

        CelestialSpigot.INSTANCE.getKnockBack().save();
    }

    public Object getValueByKey(BedWarsValues val) {
        switch (val) {
            case FRICTION:
                return frictionValue;
            case HORIZONTAL:
                return horizontal;
            case VERTICAL:
                return vertical;
            case VERTICAL_LIMIT:
                return verticalLimit;
            case MAX_RANGE_REDUCTION:
                return maxRangeReduction;
            case RANGE_FACTOR:
                return rangeFactor;
            case START_RANGE_REDUCTION:
                return startRangeReduction;
            case HIT_DELAY:
                return hitDelay;
            case SLOWDOWN_VALUE:
                return slowdownValue;
            case W_TAP:
                return wTap;
            case SLOWDOWN_BOOLEAN:
                return slowdownBoolean;
            case FRICTION_BOOLEAN:
                return friction;
            default:
                return 0.0;
        }
    }

    public void setValueByKey(BedWarsValues val, Object value) {
        switch (val) {
            case FRICTION:
                frictionValue = (Double) value;
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
            case MAX_RANGE_REDUCTION:
                maxRangeReduction = (Double) value;
                break;
            case RANGE_FACTOR:
                rangeFactor = (Double) value;
                break;
            case START_RANGE_REDUCTION:
                startRangeReduction = (Double) value;
                break;
            case HIT_DELAY:
                hitDelay = (Integer) value;
                break;
            case SLOWDOWN_VALUE:
                slowdownValue = (Double) value;
                break;
            case W_TAP:
                wTap = (Boolean) value;
                break;
            case SLOWDOWN_BOOLEAN:
                slowdownBoolean = (Boolean) value;
                break;
            case FRICTION_BOOLEAN:
                friction = (Boolean) value;
                break;
        }
    }


    @Getter
    @RequiredArgsConstructor
    public enum BedWarsValues {
        FRICTION("friction", Double.class),
        HORIZONTAL("horizontal", Double.class),
        VERTICAL("vertical", Double.class),
        VERTICAL_LIMIT("vertical-limit", Double.class),
        MAX_RANGE_REDUCTION("max-range-reduction", Double.class),
        RANGE_FACTOR("range-factor", Double.class),
        START_RANGE_REDUCTION("start-range-reduction", Double.class),
        HIT_DELAY("hit-delay", Integer.class),
        SLOWDOWN_VALUE("slowdown-value", Double.class),
        W_TAP("w-tap", Boolean.class),
        SLOWDOWN_BOOLEAN("slowdown-boolean", Boolean.class),
        FRICTION_BOOLEAN("friction-boolean", Boolean.class);

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
        public static BedWarsValues getValueByKey(String key) {
            for (BedWarsValues value : values()) {
                if(value.key.equalsIgnoreCase(key)) return value;
            }
            return null;
        }
    }
}
