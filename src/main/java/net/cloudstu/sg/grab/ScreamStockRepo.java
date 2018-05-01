package net.cloudstu.sg.grab;

import lombok.extern.slf4j.Slf4j;
import net.cloudstu.sg.util.ShiPanEUtil;
import net.cloudstu.sg.util.sinastock.StockRealTimeInfoTemplate;
import net.cloudstu.sg.util.sinastock.data.StockData;
import org.springframework.util.CollectionUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 尖叫
 *
 * @author zhiming.li
 * @date 2018/4/26
 */
@Slf4j
public class ScreamStockRepo {

    public static Set<String> codes = new HashSet<>();

    public static ConcurrentHashMap<String, Double> priceMap = new ConcurrentHashMap<>();

    /**
     * 看是否尖叫
     *
     * @param codes
     */
    public static void testScream(Set<String> codes) {
        if (!CollectionUtils.isEmpty(codes)) {
            return;
        }

        codes.stream().forEach(code -> {
            StockRealTimeInfoTemplate.get(code, response -> {
                StockData data = response.getData();
                double range = getRange(code, data.getCurrentPrice());

                if (range > 1.5) {
                    ShiPanEUtil.buy(code, getAmount(data.getCurrentPrice()));
                    log.warn("尖叫交易【{}】", code);
                }
            });
        });
    }

    /**
     * 涨幅
     *
     * @param code
     * @param nowPrice
     * @return
     */
    private static double getRange(String code, double nowPrice) {
        if (!priceMap.containsKey(code)) {
            return 0.0;
        }

        double oldPrice = priceMap.get(code);
        priceMap.put(code, nowPrice);
        return nowPrice - oldPrice;
    }

    /**
     * 购买数量
     *
     * @return
     */
    private static int getAmount(double nowPrice) {
        return (int) Math.round(200/nowPrice) * 100;
    }
}
