package net.cloudstu.sg.web.listener;

import lombok.extern.slf4j.Slf4j;
import net.cloudstu.sg.grab.MonitoredStockLoader;
import net.cloudstu.sg.grab.ScreamStockRepo;
import net.cloudstu.sg.util.SimpleTimer;
import net.cloudstu.sg.util.TransactionTimeUtil;

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
@Slf4j
public class StockScreamListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {

        //每天的09：25：00执行清理交易队列
        Calendar calendar = Calendar.getInstance();

        SimpleTimer.scheduleAtTime(new TimerTask() {
            @Override
            public void run() {
                ScreamStockRepo.existedTransactionCodes.clear();
                log.warn("清除交易过的代码集合。");
                ScreamStockRepo.priceMap.clear();
                ScreamStockRepo.rangeMap.clear();
                log.warn("清除股票振幅数据集合。");

            }
        }, 9, 25);

        SimpleTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if(TransactionTimeUtil.isTransactionTime()) {
                    try {
                        long beginTime = System.currentTimeMillis();
                        ScreamStockRepo.testScream(getCodes(), 10);
                        log.warn("cost {} ms", (System.currentTimeMillis()-beginTime));
                    }catch (Exception e) {
                        log.error("监控失败！", e);
                    }
                }
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
