package net.cloudstu.sg.web.listener;

import net.cloudstu.sg.grab.ScreamStockRepo;
import net.cloudstu.sg.util.SimpleTimer;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
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
        SimpleTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                ScreamStockRepo.testScream(getCodes());
            }
        }, 2000L, 10000L);

    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
    }


    private Set<String> getCodes() {
        return ScreamStockRepo.codes;
    }
}
