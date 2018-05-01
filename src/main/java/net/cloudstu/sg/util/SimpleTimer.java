package net.cloudstu.sg.util;

import java.util.Date;
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
     * @param timerTask
     * @param delay     毫秒
     * @param period    毫秒
     * @see Timer#scheduleAtFixedRate(TimerTask, long, long)
     */
    public static void scheduleAtFixedRate(TimerTask timerTask, long delay, long period) {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(timerTask, delay, period);
    }

    /**
     * 按指定时间每天执行任务
     *
     * @param timerTask
     * @param time      时间
     */
    public static void scheduleAtTime(TimerTask timerTask, Date time) {
        Timer timer = new Timer();
        timer.schedule(timerTask, time, 1000 * 60 * 60 * 24);
    }
}
