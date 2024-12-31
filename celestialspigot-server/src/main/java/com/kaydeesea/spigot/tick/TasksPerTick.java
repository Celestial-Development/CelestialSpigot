package com.kaydeesea.spigot.tick;

import lombok.Getter;

public class TasksPerTick implements Runnable {
    @Getter
    private final int tick;
    private final Runnable task;

    public TasksPerTick(int creationTicks, Runnable task) {
        this.tick = creationTicks;
        this.task = task;
    }

    @Override
    public void run() {
        task.run();
    }
}
