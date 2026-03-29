package org.voximir.sky_torch.utility;

import java.util.ArrayList;
import java.util.List;

public class TickScheduler {
    private static final List<ScheduledTask> TASKS = new ArrayList<>();

    public static void schedule(int delay, Runnable action) {
        TASKS.add(new ScheduledTask(delay, action));
    }

    public static void tick() {
        // Snapshot the list so that actions calling schedule() during execution
        // append to TASKS without causing ConcurrentModificationException.
        List<ScheduledTask> snapshot = new ArrayList<>(TASKS);
        TASKS.clear();
        for (ScheduledTask task : snapshot) {
            task.ticks--;
            if (task.ticks > 0) {
                TASKS.add(task);
            } else {
                task.action.run();
            }
        }
    }

    private static class ScheduledTask {
        int ticks;
        final Runnable action;

        ScheduledTask(int ticks, Runnable action) {
            this.ticks = ticks;
            this.action = action;
        }
    }
}
