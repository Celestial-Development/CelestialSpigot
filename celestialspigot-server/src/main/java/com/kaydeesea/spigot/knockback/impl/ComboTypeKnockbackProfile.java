package com.kaydeesea.spigot.knockback.impl;

import com.kaydeesea.spigot.CelestialSpigot;
import com.kaydeesea.spigot.knockback.ComboKnockbackProfile;
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
public class ComboTypeKnockbackProfile implements ComboKnockbackProfile {
    private String name;


    private double baseHorizontal ;
    private double horizontalScalePerHit;
    private double baseVertical;
    private double verticalScalePerHit;
    private double maxHorizontal;
    private double maxVertical;
    private long comboResetMS;

    private int hitDelay;

    public ComboTypeKnockbackProfile(String name) {
        this.name = name;
    }

    @Override
    public @NotNull List<String> getValues() {
        return Arrays
                .stream(ComboValues.values())
                .map(ComboValues::getKey)
                .collect(Collectors.toList());
    }

    public void save() {
        final String path = "knockback.profiles." + this.name;
        var config = CelestialSpigot.INSTANCE.getKnockBack().getConfig();

        config.set(path + ".type", ProfileType.COMBO.name());

        for (ComboValues val : ComboValues.values()) {
            String key = path + "." + val.getKey();
            Object value = getValueByKey(val);
            config.set(key, value);
        }

        CelestialSpigot.INSTANCE.getKnockBack().save();
    }

    @Getter
    @RequiredArgsConstructor
    public enum ComboValues {
        BASE_HORIZONTAL("base-horizontal", Double.class),
        HORIZONTAL_SCALE_PER_HIT("horizontal-scale-per-hit", Double.class),
        BASE_VERTICAL("base-vertical", Double.class),
        VERTICAL_SCALE_PER_HIT("vertical-scale-per-hit", Double.class),
        MAX_HORIZONTAL("max-horizontal", Double.class),
        MAX_VERTICAL("max-vertical", Double.class),
        COMBO_RESET_MS("combo-reset-ms", Long.class),
        HIT_DELAY("hit-delay", Integer.class);

        private final String key;
        private final Class<?> type;



        public boolean isLong() {
            return type == Long.class;
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
        public static ComboValues getValueByKey(String key) {
            for (ComboValues value : values()) {
                if(value.key.equalsIgnoreCase(key)) return value;
            }
            return null;
        }
    }

    public Object getValueByKey(ComboValues val) {
        switch (val) {
            case BASE_HORIZONTAL:
                return baseHorizontal;
            case HORIZONTAL_SCALE_PER_HIT:
                return horizontalScalePerHit;
            case BASE_VERTICAL:
                return baseVertical;
            case VERTICAL_SCALE_PER_HIT:
                return verticalScalePerHit;
            case MAX_HORIZONTAL:
                return maxHorizontal;
            case MAX_VERTICAL:
                return maxVertical;
            case COMBO_RESET_MS:
                return comboResetMS;
            case HIT_DELAY:
                return hitDelay;
            default:
                return 0.0;  // or some default value
        }
    }

    public void setValueByKey(ComboValues val, Object value) {
        switch (val) {
            case BASE_HORIZONTAL:
                baseHorizontal = (Double) value;
                break;
            case HORIZONTAL_SCALE_PER_HIT:
                horizontalScalePerHit = (Double) value;
                break;
            case BASE_VERTICAL:
                baseVertical = (Double) value;
                break;
            case VERTICAL_SCALE_PER_HIT:
                verticalScalePerHit = (Double) value;
                break;
            case MAX_HORIZONTAL:
                maxHorizontal = (Double) value;
                break;
            case MAX_VERTICAL:
                maxVertical = (Double) value;
                break;
            case HIT_DELAY:
                hitDelay = (Integer) value;
            case COMBO_RESET_MS:
                comboResetMS = (Long) value;
                break;
        }
    }
}
