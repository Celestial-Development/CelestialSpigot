package com.kaydeesea.spigot.threads.utils;

import java.util.concurrent.atomic.AtomicInteger;



public class Tasks {

    private final AtomicInteger state = new AtomicInteger();

    public boolean fetchTask() {
        int old = this.state.getAndDecrement();
        if (old == State.RUNNING_GOT_TASKS.ordinal()) {
            return true;
        } else if (old == State.RUNNING_NO_TASKS.ordinal()) {
            return false;
        } else {
            throw new AssertionError();
        }
    }

    public boolean addTask() {
        if (this.state.get() == State.RUNNING_GOT_TASKS.ordinal()) {
            return false;
        } else {
            int old = this.state.getAndSet(State.RUNNING_GOT_TASKS.ordinal());
            return old == State.WAITING.ordinal();
        }
    }

    private static enum State {
        WAITING,
        RUNNING_NO_TASKS,
        RUNNING_GOT_TASKS;
    }
}
