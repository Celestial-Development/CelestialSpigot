package com.kaydeesea.spigot.threads.impl;


import com.kaydeesea.spigot.threads.AbstractThread;



public class HitDetectionThread extends AbstractThread {

    @Override
    public void execute() {
        while (!this.packets.isEmpty()) {
            this.packets.poll().run();
        }
    }
}
