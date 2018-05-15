package net.cloudstu.sg.util;

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 简单的定时任务
 *
 * @author zhiming.li
 */
public class SimpleTimer {

    private static final long PERIOD_DAY = 24 * 60 * 60 * 1000;

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
     * 按指定时间每天执行任务，控制到分钟
     *
     * @param timerTask
     * @param hour      执行所在小时
     * @param minute    执行所在分钟
     */
    public static void scheduleAtTime(TimerTask timerTask, int hour, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0); //不控制到秒
        Date firstExecuteTime  = calendar.getTime(); //第一次执行定时任务的时间

        //如果第一次执行定时任务的时间 小于当前的时间
        //此时要在 第一次执行定时任务的时间加一天，以便此任务在下个时间点执行。如果不加一天，任务会立即执行。
        if (firstExecuteTime.before(new Date())) {
            firstExecuteTime = adjustDay(firstExecuteTime, 1);
        }

        Timer timer = new Timer();

        //安排指定的任务在指定的时间开始进行重复的固定延迟执行。
        timer.schedule(timerTask, firstExecuteTime, PERIOD_DAY);
    }

    // 增加或减少天数
    private static Date adjustDay(Date date, int num) {
        Calendar startDT = Calendar.getInstance();
        startDT.setTime(date);
        startDT.add(Calendar.DAY_OF_MONTH, num);
        return startDT.getTime();
    }

}



