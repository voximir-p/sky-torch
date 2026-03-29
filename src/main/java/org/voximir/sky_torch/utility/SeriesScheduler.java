package org.voximir.sky_torch.utility;

public class SeriesScheduler {
    private int delay = 0;

    public SeriesScheduler run(Runnable action) {
        TickScheduler.schedule(delay, action);
        return this;
    }

    public SeriesScheduler sleep(int ticks) {
        delay += ticks;
        return this;
    }
}
