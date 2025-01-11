package com.kaydeesea.spigot.threads;

import com.kaydeesea.spigot.CelestialSpigot;
import com.kaydeesea.spigot.threads.utils.SpigotChannelWriter;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.Getter;
import net.minecraft.server.NetworkManager;
import net.minecraft.server.Packet;
import net.openhft.affinity.AffinityLock;
import net.openhft.affinity.AffinityStrategies;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;



public abstract class AbstractThread {

    @Getter
    protected Queue<Runnable> packets = new ConcurrentLinkedQueue<>();
    private final int TICK_TIME = 1000000000 / CelestialSpigot.INSTANCE.getConfig().getAsyncThreadTPS();
    private final boolean running;
    private Thread thread;

    public AbstractThread() {
        this.running = true;
        try {
            try (AffinityLock al = AffinityLock.acquireLock()) {
                this.thread = new Thread(() -> {
                    try (AffinityLock al2 = al.acquireLock(AffinityStrategies.SAME_SOCKET, AffinityStrategies.ANY)) {
                        loop();
                    }
                });
                this.thread.start();
            }
        } catch (Exception e) {
            System.out.println("ERROR: An Error occurred while trying to load affinity thread, using normal thread");
            System.out.println("NOTE: THIS MIGHT CAUSE LAG BECAUSE IT IS NOT BEING USED THROUGH AN OPTIMIZED API!");
            this.thread = new Thread(this::loop);
            this.thread.start();
        }
    }

    public void loop() {
        long lastTick = System.nanoTime();
        long catchupTime = 0L;

        while (running) {
            long curTime = System.nanoTime();
            long wait = (long)this.TICK_TIME - (curTime - lastTick) - catchupTime;
            if (wait > 0L) {
                try {
                    Thread.sleep(wait / 1000000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                catchupTime = 0L;
            } else {
                catchupTime = Math.min(1000000000L, Math.abs(wait));
                execute();
                lastTick = curTime;
            }
        }
    }

    public abstract void execute();

    public void addPacket(Packet packet, NetworkManager manager, GenericFutureListener<? extends Future<? super Void>>[] genericFutureListener) {
        this.packets.add(() -> SpigotChannelWriter.writeThenFlush(manager.channel, packet, genericFutureListener));
    }

}
