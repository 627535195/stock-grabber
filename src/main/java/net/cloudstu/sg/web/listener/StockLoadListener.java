package net.cloudstu.sg.web.listener;

import lombok.extern.slf4j.Slf4j;
import net.cloudstu.sg.grab.MonitoredStockLoader;
import net.cloudstu.sg.grab.ScreamStockRepo;
import net.cloudstu.sg.util.ShiPanEUtil;
import net.cloudstu.sg.util.SimpleTimer;
import net.cloudstu.sg.util.TransactionTimeUtil;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.Calendar;
import java.util.Date;
import java.util.TimerTask;

/**
 * 加载预测股
 *
 * @author zhiming.li
 * @date 2018/5/1
 */
@Slf4j
public class StockLoadListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {

        //每天的09：25：00执行load任务
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 9); // 控制时
        calendar.set(Calendar.MINUTE, 25);    // 控制分
        calendar.set(Calendar.SECOND, 0);    // 控制秒
        Date time = calendar.getTime();     // 得出执行任务的时间,此处为今天的09：25：00

        SimpleTimer.scheduleAtTime(new TimerTask() {
            @Override
            public void run() {
                //测试交易接口
                ShiPanEUtil.buy("000001", 100);

                MonitoredStockLoader.load();
                log.warn("初始化涨停预测完成！【{}】", ScreamStockRepo.codes);
            }
        }, time);

        //开盘后的补充预测
        SimpleTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (TransactionTimeUtil.isTransactionTime()) {
                    MonitoredStockLoader.append();
                    log.warn("新增涨停预测完成！【{}】", ScreamStockRepo.codes);
                }
            }
        },2000, 5*60*1000);
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
    }
}
