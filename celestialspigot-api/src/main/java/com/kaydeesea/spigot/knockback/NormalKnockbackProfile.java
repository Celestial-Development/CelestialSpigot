package com.kaydeesea.spigot.knockback;

import net.minecraft.server.EntityHuman;
import net.minecraft.server.EntityLiving;
import net.minecraft.server.MathHelper;

public interface NormalKnockbackProfile extends KnockBackProfile {
    double getFriction();

    void setFriction(double friction);

    double getHorizontal();

    void setHorizontal(double horizontal);

    double getVertical();

    void setVertical(double vertical);

    double getVerticalLimit();

    void setVerticalLimit(double verticalLimit);

    double getExtraHorizontal();

    void setExtraHorizontal(double extraHorizontal);

    double getExtraVertical();

    default void handleEntityLiving(EntityLiving entityLiving, float f, double d0, double d1) {
        // cSpigot start
        double magnitude = MathHelper.sqrt(d0 * d0 + d1 * d1);

        entityLiving.motX /= getFriction();
        entityLiving.motY /= getFriction();
        entityLiving.motZ /= getFriction();

        entityLiving.motX -= d0 / magnitude * getHorizontal();
        entityLiving.motY += getVertical();
        entityLiving.motZ -= d1 / magnitude * getHorizontal();

        if (entityLiving.motY > getVerticalLimit()) {
            entityLiving.motY = getVerticalLimit();
        }
        // cSpigot end
    };

    default void handleEntityHuman(EntityHuman entityHuman, int i) {
        entityHuman.g(
                (-MathHelper.sin(entityHuman.yaw * 3.1415927F / 180.0F) * (float) i * getExtraHorizontal()), getExtraVertical(),
                (MathHelper.cos(entityHuman.yaw * 3.1415927F / 180.0F) * (float) i * getExtraHorizontal())
        );
    }

    void setExtraVertical(double extraVertical);
    default ProfileType getType() {
        return ProfileType.NORMAL;
    }
}
