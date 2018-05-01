package net.cloudstu.sg.web.listener;

import net.cloudstu.sg.grab.MonitoredStockLoader;
import net.cloudstu.sg.grab.ScreamStockRepo;
import net.cloudstu.sg.util.SimpleTimer;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;
import java.util.TimerTask;

/**
 * 尖叫自动买入
 *
 * @author zhiming.li
 * @date 2018/4/23
 */
public class StockScreamListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {

        //每天的09：25：00执行清理交易队列
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 9); // 控制时
        calendar.set(Calendar.MINUTE, 25);    // 控制分
        calendar.set(Calendar.SECOND, 0);    // 控制秒
        Date time = calendar.getTime();     // 得出执行任务的时间,此处为今天的09：25：00

        SimpleTimer.scheduleAtTime(new TimerTask() {
            @Override
            public void run() {
                ScreamStockRepo.existedTransactionCodes.clear();
                ScreamStockRepo.priceMap.clear();
            }
        }, time);

        SimpleTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                ScreamStockRepo.testScream(getCodes(), 1.5);
            }
        }, 2000L, 5000L);

        SimpleTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                ScreamStockRepo.testScream(getCodes(), 2);
            }
        }, 2000L, 30000L);
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
    }


    private Set<String> getCodes() {
        return ScreamStockRepo.codes;
    }
}
