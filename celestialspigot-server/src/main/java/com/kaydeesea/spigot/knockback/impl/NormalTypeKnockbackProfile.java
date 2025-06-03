package com.kaydeesea.spigot.knockback.impl;

import com.kaydeesea.spigot.CelestialSpigot;
import com.kaydeesea.spigot.knockback.NormalKnockbackProfile;
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
public class NormalTypeKnockbackProfile implements NormalKnockbackProfile {

    private String name;
    private double friction = 2.0D;
    private double horizontal = 0.35D;
    private double vertical = 0.35D;
    private double verticalLimit = 0.4D;
    private double extraHorizontal = 0.425D;
    private double extraVertical = 0.085D;
    private int hitDelay = 20;

    public NormalTypeKnockbackProfile(String name) {
        this.name = name;
    }

    @Override
    public @NotNull List<String> getValues() {
        return Arrays
                .stream(NormalValues.values())
                .map(NormalValues::getKey)
                .collect(Collectors.toList());
    }

    public void save() {
        final String path = "knockback.profiles." + this.name;
        var config = CelestialSpigot.INSTANCE.getKnockBack().getConfig();

        config.set(path + ".type", ProfileType.NORMAL.name());

        for (NormalValues val : NormalValues.values()) {
            String key = path + "." + val.getKey();
            Object value = getValueByKey(val);
            config.set(key, value);
        }

        CelestialSpigot.INSTANCE.getKnockBack().save();
    }

    public Object getValueByKey(NormalValues val) {
        switch (val) {
            case FRICTION:
                return friction;
            case HORIZONTAL:
                return horizontal;
            case VERTICAL:
                return vertical;
            case VERTICAL_LIMIT:
                return verticalLimit;
            case EXTRA_HORIZONTAL:
                return extraHorizontal;
            case EXTRA_VERTICAL:
                return extraVertical;
            case HIT_DELAY:
                return hitDelay;
            default:
                return 0.0;
        }
    }

    public void setValueByKey(NormalValues val, Object value) {
        switch (val) {
            case FRICTION:
                friction = (Double) value;
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
            case EXTRA_HORIZONTAL:
                extraHorizontal = (Double) value;
                break;
            case EXTRA_VERTICAL:
                extraVertical = (Double) value;
                break;
            case HIT_DELAY:
                hitDelay = (Integer) value;
                break;
        }
    }

    @Getter
    @RequiredArgsConstructor
    public enum NormalValues {
        FRICTION("friction", Double.class),
        HORIZONTAL("horizontal", Double.class),
        VERTICAL("vertical", Double.class),
        VERTICAL_LIMIT("vertical-limit", Double.class),
        EXTRA_HORIZONTAL("extra-horizontal", Double.class),
        EXTRA_VERTICAL("extra-vertical", Double.class),
        HIT_DELAY("hit-delay", Integer.class);

        private final String key;
        private final Class<?> type;

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
        public static NormalValues getValueByKey(String key) {
            for (NormalValues value : values()) {
                if(value.key.equalsIgnoreCase(key)) return value;
            }
            return null;
        }
    }

}
