package org.voximir.sky_torch.util;

import java.util.LinkedList;
import java.util.List;

public class TickScheduler {
    private static final List<ScheduledTask> TASKS = new LinkedList<>();

    public static void schedule(int delay, Runnable action) {
        TASKS.add(new ScheduledTask(delay, action));
    }

    public static void tick() {
        var iterator = TASKS.iterator();

        while (iterator.hasNext()) {
            ScheduledTask task = iterator.next();
            task.ticks--;

            if (task.ticks <= 0) {
                task.action.run();
                iterator.remove();
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
