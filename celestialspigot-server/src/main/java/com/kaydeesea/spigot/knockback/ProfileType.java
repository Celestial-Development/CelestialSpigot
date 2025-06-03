package com.kaydeesea.spigot.knockback;

import com.kaydeesea.spigot.knockback.impl.BedWarsTypeKnockbackProfile;
import com.kaydeesea.spigot.knockback.impl.ComboTypeKnockbackProfile;
import com.kaydeesea.spigot.knockback.impl.DetailedTypeKnockbackProfile;
import com.kaydeesea.spigot.knockback.impl.NormalTypeKnockbackProfile;

public enum ProfileType {
    NORMAL("normal", NormalTypeKnockbackProfile.class),
    DETAILED("detailed", DetailedTypeKnockbackProfile.class),
    COMBO("combo", ComboTypeKnockbackProfile.class),
    BEDWARS("bedwars", BedWarsTypeKnockbackProfile.class);

    public final String raw;
    public final Class<? extends KnockBackProfile> profileClass;

    ProfileType(String raw, Class<? extends KnockBackProfile> profileClass) {
        this.raw = raw;
        this.profileClass = profileClass;
    }

    public static ProfileType getByClass(Class<? extends KnockBackProfile> profileClass) {
        for (ProfileType value : values()) {
            if (value.profileClass == profileClass) {
                return value;
            }
        }
        return null;
    }

    public static ProfileType fromRaw(String raw) {
        for (ProfileType value : values()) {
            if (value.raw.equalsIgnoreCase(raw)) {
                return value;
            }
        }
        return null;
    }

    public KnockBackProfile newInstance(String name) {
        try {
            return profileClass.getConstructor(String.class).newInstance(name);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create KnockBackProfile instance for type: " + this.name(), e);
        }
    }
}
