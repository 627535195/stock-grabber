package net.cloudstu.sg.grab;

import lombok.extern.slf4j.Slf4j;
import net.cloudstu.sg.factor.FactorValidator;
import net.cloudstu.sg.util.LimitQueue;
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

    public final static Logger transactionLog = LoggerFactory.getLogger("transactionLog");

    public static Set<String> codes = new HashSet<>();

    public static Set<String> existedTransactionCodes = new HashSet<>();

    public static ConcurrentHashMap<String, Double> priceMap = new ConcurrentHashMap<>();

    public static ConcurrentHashMap<String, LimitQueue[]> rangeMap = new ConcurrentHashMap<>();

    /**
     * 看是否尖叫
     *
     * @param codes
     * @param seconds 频率
     */
    public synchronized static void testScream(Set<String> codes, int seconds) {
        if (CollectionUtils.isEmpty(codes)) {
            return;
        }

        codes.stream().forEach(code -> {
            StockRealTimeInfoTemplate.get(code, response -> {
                StockData data = response.getData();
                double range = getRange(code, data.getSwing());
                setRange(code, data.getSwing(), seconds);

                if (FactorValidator.validate(code, data)
                        && (range > 1.2 || testRange(code))
                        && existedTransactionCodes.add(code)) {
                    ShiPanEUtil.buy(code, getAmount(data.getCurrentPrice()));
                    transactionLog.warn("尖叫交易【{}】", code);
                }

            });
        });
    }

    /**
     * 将股票浮动放入队列，以获得特定时间比如30s 60s的浮动
     *
     * @param code     股票代码
     * @param nowSwing 当前价格
     * @param seconds  获取股票浮动的频率
     */
    private static void setRange(String code, double nowSwing, int seconds) {
        if (!rangeMap.containsKey(code)) {
            LimitQueue<Double>[] lqArray = new LimitQueue[]{new LimitQueue<>(30 / seconds), new LimitQueue<>(60 / seconds), new LimitQueue<>(90 / seconds)};
            lqArray[0].offer(nowSwing);
            lqArray[1].offer(nowSwing);
            lqArray[2].offer(nowSwing);
            rangeMap.put(code, lqArray);
            return;
        }

        LimitQueue<Double>[] lqArray = rangeMap.get(code);
        lqArray[0].offer(nowSwing);
        lqArray[1].offer(nowSwing);
        lqArray[2].offer(nowSwing);
    }

    /**
     * 测试特定区间内的涨幅是否达标
     *
     * @param code 股票代码
     * @return
     */
    private static boolean testRange(String code) {
        LimitQueue<Double>[] lqArray = rangeMap.get(code);
        if (lqArray == null || lqArray.length != 3) {
            return false;
        }

        if (lqArray[0].isFull()) {
            double range = lqArray[0].getLast() - lqArray[0].getFirst();
            swingLog.info("{},{},{}", code, range, 30);
            if (range > 1.5) {
                return true;
            }
        }

        if (lqArray[1].isFull()) {
            double range = lqArray[1].getLast() - lqArray[1].getFirst();
            swingLog.info("{},{},{}", code, range, 60);
            if (range > 1.8) {
                return true;
            }
        }

        if (lqArray[2].isFull()) {
            double range = lqArray[2].getLast() - lqArray[2].getFirst();
            swingLog.info("{},{},{}", code, range, 90);
            if (range > 2) {
                return true;
            }
        }

        return false;

    }

    /**
     * 按特定频率的每次涨幅
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
        return (int) Math.round(200 / nowPrice) * 100;
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
