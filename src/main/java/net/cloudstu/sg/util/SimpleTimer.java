package net.cloudstu.sg.util;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 简单的定时任务
 *
 * @author zhiming.li
 */
public class SimpleTimer {

    /**
     * 按固定时间间隔执行任务
     *
     * @see Timer#scheduleAtFixedRate(TimerTask, long, long)
     * @param timerTask
     * @param delay 毫秒
     * @param period 毫秒
     */
    public static void scheduleAtFixedRate(TimerTask timerTask, long delay, long period) {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(timerTask, delay, period);
    }
}
