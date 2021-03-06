package net.cloudstu.sg.web.listener;

import net.cloudstu.sg.grab.MonitoredStockLoader;
import net.cloudstu.sg.grab.ScreamStockRepo;
import net.cloudstu.sg.grab.StockHoldRepo;
import net.cloudstu.sg.util.ShiPanEUtil;
import net.cloudstu.sg.util.SimpleTimer;
import net.cloudstu.sg.util.TransactionTimeUtil;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.TimerTask;

/**
 * 添加持仓监控
 *
 * @author zhiming.li
 * @date 2018/4/23
 */
public class StockHoldListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {

        // 10秒启动延迟，交易期间每10分钟刷新一次持仓
        SimpleTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (TransactionTimeUtil.isTransactionTime()) {
                    StockHoldRepo.refreshHoldStocks();
                }
            }
        }, 10000L, 10 * 60 * 1000L);


        //每天的09：45：00卖掉没有涨停的股票
        SimpleTimer.scheduleAtTime(new TimerTask() {
            @Override
            public void run() {
                StockHoldRepo.sellAnyway();
            }
        }, 9, 45);

//        SimpleTimer.scheduleAtFixedRate(new TimerTask() {
//            @Override
//            public void run() {
//                StockHoldRepo.monitor();
//            }
//        }, 2000L, 10000L);
//
//        // 10分钟一次清理报警集合， 也就是10分钟依然如此会再报警
//        SimpleTimer.scheduleAtFixedRate(new TimerTask() {
//            @Override
//            public void run() {
//                StockHoldRepo.holdNoticedSet.clear();
//            }
//        }, 2000L, 10*60*1000L);

    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
    }
}
