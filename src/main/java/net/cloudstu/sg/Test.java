package net.cloudstu.sg;

import net.cloudstu.sg.app.RootConfig;
import net.cloudstu.sg.dao.StockDao;
import net.cloudstu.sg.dao.StockHoldDao;
import net.cloudstu.sg.entity.StockHoldModel;
import net.cloudstu.sg.grab.MonitoredStockLoader;
import net.cloudstu.sg.grab.ScreamStockRepo;
import net.cloudstu.sg.util.ShiPanEUtil;
import net.cloudstu.sg.util.SimpleTimer;
import net.cloudstu.sg.util.SpringUtil;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.Calendar;
import java.util.Date;
import java.util.TimerTask;

/**
 * @author zhiming.li
 * @date 2018/5/9
 */
public class Test {
    public static void main(String[] args) throws InterruptedException {
        new AnnotationConfigApplicationContext(RootConfig.class);

        StockHoldDao stockHoldDao = SpringUtil.getBean(StockHoldDao.class);

        StockHoldModel stockHold = new StockHoldModel();
        stockHold.setCode("000001");
        stockHold.setName("平安银行");
        stockHold.setNowPrice(1);
        stockHold.setExpectPrice(1);
        stockHold.setUpdateTime(new Date());
        stockHoldDao.insert(stockHold);
    }
}
