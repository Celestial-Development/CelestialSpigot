package com.kaydeesea.spigot.threads.impl;


import com.kaydeesea.spigot.threads.AbstractThread;



public class KnockbackThread extends AbstractThread {

    @Override
    public void execute() {
        while (!this.packets.isEmpty()) {
            this.packets.poll().run();
        }
    }
}
