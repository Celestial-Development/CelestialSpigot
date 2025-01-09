package com.kaydeesea.spigot.threads.utils;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.Getter;
import net.minecraft.server.Packet;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * This Project is property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created: 9/4/2021
 * Project: CarbonSpigot
 */
public class SpigotChannelWriter {

    private static  final Queue<PacketQueue> queue = new ConcurrentLinkedQueue<>();
    private static final Tasks tasks = new Tasks();
    private final Channel channel;

    public SpigotChannelWriter(Channel channel) {
        this.channel = channel;
    }

    public static void writeThenFlush(Channel channel, Packet value, GenericFutureListener<? extends Future<? super Void>>[] listener) {
        SpigotChannelWriter writer = new SpigotChannelWriter(channel);
        queue.add(new PacketQueue(value, listener));

        if (tasks.addTask()) {
            channel.pipeline().lastContext().executor().execute(writer::writeQueueAndFlush);
        }
    }

    public void writeQueueAndFlush() {
        while (tasks.fetchTask()) {
            while (!queue.isEmpty()) {
                PacketQueue messages = queue.poll();
                if (messages == null) continue;
                ChannelFuture future = this.channel.write(messages.getPacket());
                if (messages.getListener() != null) {
                    future.addListeners(messages.getListener());
                }
                future.addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
            }
        }
        this.channel.flush();
    }

    @Getter
    private static class PacketQueue {

        private final Packet packet;
        private final GenericFutureListener<? extends Future<? super Void>>[] listener;

        public PacketQueue(Packet packet, GenericFutureListener<? extends Future<? super Void>>[] listener) {
            this.packet=packet;
            this.listener=listener;
        }

    }
}
