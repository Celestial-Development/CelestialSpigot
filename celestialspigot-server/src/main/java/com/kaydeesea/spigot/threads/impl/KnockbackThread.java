package com.kaydeesea.spigot.threads.impl;


import com.kaydeesea.spigot.threads.AbstractThread;

/**
 * This Project is property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created: 9/4/2021
 * Project: CarbonSpigot
 */

public class KnockbackThread extends AbstractThread {

    @Override
    public void execute() {
        while (!this.packets.isEmpty()) {
            this.packets.poll().run();
        }
    }
}
