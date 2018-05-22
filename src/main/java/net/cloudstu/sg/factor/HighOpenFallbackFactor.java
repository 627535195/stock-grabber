package net.cloudstu.sg.factor;

import net.cloudstu.sg.util.LimitQueue;
import net.cloudstu.sg.util.sinastock.data.StockData;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 从最高点回落一定数值的股票不能买
 *
 * @author zhiming.li
 * @date 2018/5/22
 */
public class HighOpenFallbackFactor implements Factor{

    public ConcurrentHashMap<String, Double> maxMap = new ConcurrentHashMap<>();
    private static final double FACTOR = 3.0;


    @Override
    public boolean isSatisfied(String code, StockData data) {
        double max = getMaxMap(code, data);

        double fallback = max - data.getSwing();

        if(fallback > FACTOR) {
            return false;
        }

        return true;
    }

    private double getMaxMap(String code, StockData data) {
        if(!maxMap.containsKey(code)) {
            maxMap.put(code, Math.max(data.getSwing(), data.getOpenSwing()));
        }else {
            double max = maxMap.get(code);
            maxMap.put(code, Math.max(data.getSwing(), max));
        }

        return maxMap.get(code);
    }

}
