package net.cloudstu.sg.web.listener;


import net.cloudstu.sg.grab.TransactionTrackerRepo;
import net.cloudstu.sg.util.SimpleTimer;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.TimerTask;

/**
 * 初始化模拟交易跟踪器
 *
 * @author zhiming.li
 * @date 2018/4/17
 */
public class TransactionTrackerListener implements ServletContextListener {


    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {

        long[] userIds = {353465,417634,410360,120909,335243,352518};

        //按一定频率抓取 实验1号 278542 当月72 的交易记录 3秒一次
        SimpleTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                TransactionTrackerRepo.grab(72, userIds);
            }
        }, 2000L, 3000L);
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
    }
}
