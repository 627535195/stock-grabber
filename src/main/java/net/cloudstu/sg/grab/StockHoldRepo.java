package net.cloudstu.sg.grab;

import net.cloudstu.sg.dao.StockHoldDao;
import net.cloudstu.sg.entity.StockHoldModel;
import net.cloudstu.sg.util.SpringUtil;
import net.cloudstu.sg.util.WxmpSender;
import net.cloudstu.sg.util.sinastock.StockRealTimeInfoTemplate;
import net.cloudstu.sg.util.sinastock.data.SinaStockResponse;
import net.cloudstu.sg.util.sinastock.data.StockData;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 持仓处理
 *
 * @author zhiming.li
 * @date 2018/4/19
 */
public class StockHoldRepo {

    public static Set<String> holdNoticedSet = new HashSet<>(4);

    public static void monitor() {
        StockHoldDao stockHoldDao = SpringUtil.getBean(StockHoldDao.class);
        List<StockHoldModel> stockHolds = stockHoldDao.selectAll();
        if (CollectionUtils.isEmpty(stockHolds)) {
            return;
        }

        for (StockHoldModel stockHold : stockHolds) {
            if(StringUtils.isEmpty(stockHold.getCode())) {
               continue;
            }
            StockRealTimeInfoTemplate.get(stockHold.getCode(), response -> {
                StockData data = response.getData();

                //达到预期提示
                if(data.getCurrentPrice()> stockHold.getExpectPrice() && holdNoticedSet.add(stockHold.getCode())) {
                    WxmpSender.messageSendToAdmin(getReachExpectedInfo(stockHold));
                }

                //大幅拉升提示
                if(data.getSwing()>5 && holdNoticedSet.add(stockHold.getCode())) {
                    WxmpSender.messageSendToAdmin(getLargeUp(stockHold));
                }

            });
        }

    }

    private static String getLargeUp(StockHoldModel stockHold) {
        return String.format("【{}】大幅拉升！", stockHold.getName());
    }

    private static String getReachExpectedInfo(StockHoldModel stockHold) {
        return String.format("【{}】达到预期，恭喜！", stockHold.getName());
    }
}
