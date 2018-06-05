package net.cloudstu.sg.grab;

import lombok.extern.slf4j.Slf4j;
import net.cloudstu.sg.app.RootConfig;
import net.cloudstu.sg.dao.StockHoldDao;
import net.cloudstu.sg.entity.StockHoldModel;
import net.cloudstu.sg.util.ShiPanEUtil;
import net.cloudstu.sg.util.SpringUtil;
import net.cloudstu.sg.util.ThreadUtil;
import net.cloudstu.sg.util.WxmpSender;
import net.cloudstu.sg.util.sinastock.StockRealTimeInfoTemplate;
import net.cloudstu.sg.util.sinastock.data.StockData;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 持仓处理
 *
 * @author zhiming.li
 * @date 2018/4/19
 */
@Slf4j
public class StockHoldRepo {

    public static Set<String> holdNoticedSet = new HashSet<>(4);

    /**
     * 持仓监控
     */
    public static void monitor() {
        StockHoldDao stockHoldDao = SpringUtil.getBean(StockHoldDao.class);
        List<StockHoldModel> stockHolds = stockHoldDao.selectAll();
        if (CollectionUtils.isEmpty(stockHolds)) {
            return;
        }

        for (StockHoldModel stockHold : stockHolds) {
            if (StringUtils.isEmpty(stockHold.getCode())) {
                continue;
            }
            StockRealTimeInfoTemplate.get(stockHold.getCode(), response -> {
                StockData data = response.getData();

                //达到预期提示
                if (data.getCurrentPrice() > stockHold.getExpectPrice() && holdNoticedSet.add(stockHold.getCode())) {
                    WxmpSender.messageSendToAdmin(getReachExpectedInfo(stockHold));
                }

                //大幅拉升提示
                if (data.getSwing() > 5 && holdNoticedSet.add(stockHold.getCode())) {
                    WxmpSender.messageSendToAdmin(getLargeUp(stockHold));
                }

            });
        }

    }

    /**
     * 从实盘易获取持仓股并入库
     */
    public static void refreshHoldStocks() {
        List<StockHoldModel> stockHolds = ShiPanEUtil.getHoldStocks();

        StockHoldDao stockHoldDao = SpringUtil.getBean(StockHoldDao.class);
        stockHoldDao.clear();

        for (StockHoldModel stockHold : stockHolds) {
            stockHoldDao.insert(stockHold);
        }
    }

    /**
     * 卖出标记为可售出的股票
     */
    public static void sell () {
        StockHoldDao stockHoldDao = SpringUtil.getBean(StockHoldDao.class);
        List<StockHoldModel> stockHolds = stockHoldDao.selectAll();

        for (StockHoldModel stockHold : stockHolds) {
            if(stockHold.isSell() && stockHold.getAvailableHold()>0) {
                ShiPanEUtil.sell(stockHold.getCode(), stockHold.getAvailableHold());
            }
        }

    }

    /**
     * 卖出没有涨停的股票
     */
    public static void sellAnyway() {
        StockHoldDao stockHoldDao = SpringUtil.getBean(StockHoldDao.class);
        List<StockHoldModel> stockHolds = stockHoldDao.selectAll();

        for (StockHoldModel stockHold : stockHolds) {
            if(stockHold.getAvailableHold()>0 ) {
                StockRealTimeInfoTemplate.get(stockHold.getCode(), response -> {
                    StockData data = response.getData();

                    //没有涨停
                    if (data.getSwing() < 9.85) {
                        ShiPanEUtil.sell(stockHold.getCode(), stockHold.getAvailableHold());
                    }

                });

            }
            ThreadUtil.sleep(2, TimeUnit.SECONDS);
        }
    }


    private static String getLargeUp(StockHoldModel stockHold) {
        return String.format("【{}】大幅拉升！", stockHold.getName());
    }

    private static String getReachExpectedInfo(StockHoldModel stockHold) {
        return String.format("【{}】达到预期，恭喜！", stockHold.getName());
    }

    public static void main(String[] args) {
        new AnnotationConfigApplicationContext(RootConfig.class);

        sell();
    }
}
