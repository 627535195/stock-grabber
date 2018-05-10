package net.cloudstu.sg;

import net.cloudstu.sg.grab.MonitoredStockLoader;
import net.cloudstu.sg.grab.ScreamStockRepo;
import net.cloudstu.sg.util.ShiPanEUtil;
import net.cloudstu.sg.util.SimpleTimer;

import java.util.Calendar;
import java.util.Date;
import java.util.TimerTask;

/**
 * @author zhiming.li
 * @date 2018/5/9
 */
public class Test {
    public static void main(String[] args) throws InterruptedException {
        //每天的09：25：00执行load任务
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 18); // 控制时
        calendar.set(Calendar.MINUTE, 50);    // 控制分
        calendar.set(Calendar.SECOND, 0);    // 控制秒
        Date time = calendar.getTime();     // 得出执行任务的时间,此处为今天的09：25：00

        SimpleTimer.scheduleAtTime(new TimerTask() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000*5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("ok1!");
            }
        }, time);

        SimpleTimer.scheduleAtTime(new TimerTask() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000*5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("ok2!");
            }
        }, time);


        Thread.sleep(1000 * 60 * 5);
    }
}
