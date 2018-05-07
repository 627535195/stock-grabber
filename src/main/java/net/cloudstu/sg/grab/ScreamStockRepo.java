package net.cloudstu.sg.grab;

import lombok.extern.slf4j.Slf4j;
import net.cloudstu.sg.util.ShiPanEUtil;
import net.cloudstu.sg.util.sinastock.StockRealTimeInfoTemplate;
import net.cloudstu.sg.util.sinastock.data.StockData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.util.HashSet;
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

    public final static Logger swingLog = LoggerFactory.getLogger("swingLog");

    public static Set<String> codes = new HashSet<>();

    public static Set<String> existedTransactionCodes = new HashSet<>();

    public static ConcurrentHashMap<String, Double> priceMap = new ConcurrentHashMap<>();

    /**
     * 看是否尖叫
     *
     * @param codes
     * @param referenceRange 参考量
     * @param seconds 频率
     */
    public static void testScream(Set<String> codes, double referenceRange, int seconds) {
        if (CollectionUtils.isEmpty(codes)) {
            return;
        }

        codes.stream().forEach(code -> {
            StockRealTimeInfoTemplate.get(code, response -> {
                StockData data = response.getData();
                double range = getRange(code, data.getSwing());

                if (range > referenceRange && existedTransactionCodes.add(code)) {
                    ShiPanEUtil.buy(code, getAmount(data.getCurrentPrice()));
                    log.warn("尖叫交易【{}】", code);
                }

                swingLog.info("{},{},{}", code, range, seconds);


//                if(seconds>10) {
//                    SwingModel swing = new SwingModel();
//                    swing.setCode(code);
//                    swing.setSeconds(seconds);
//                    swing.setSwing(range);
//                    SpringUtil.getBean(SwingDao.class).insert(swing);
//                }
            });
        });
    }

    /**
     * 涨幅
     *
     * @param code
     * @param nowSwing
     * @return
     */
    private static double getRange(String code, double nowSwing) {
        if (!priceMap.containsKey(code)) {
            priceMap.put(code, nowSwing);
            return 0.0;
        }

        double oldSwing = priceMap.get(code);
        priceMap.put(code, nowSwing);
        return nowSwing - oldSwing;
    }

    /**
     * 购买数量
     *
     * @return
     */
    private static int getAmount(double nowPrice) {
        return (int) Math.round(200/nowPrice) * 100;
    }

    public static void main(String[] args) {
        Set<String> codes = new HashSet<>();
        codes.add("300380");
        codes.add("600604");
        codes.stream().forEach(code -> {
            StockRealTimeInfoTemplate.get(code, response -> {
                System.out.println(response.getData().getCurrentPrice());
            });
        });
    }
}
