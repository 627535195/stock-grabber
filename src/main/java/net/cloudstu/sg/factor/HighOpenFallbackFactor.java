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

    public ConcurrentHashMap<String, Double> minMap = new ConcurrentHashMap<>();

    private static final double FACTOR = 3.0;


    @Override
    public boolean isSatisfied(String code, StockData data) {
        double min = getMinMap(code, data);

        double fallback = data.getOpenSwing() - min;

        if(fallback > FACTOR) {
            return false;
        }

        return true;
    }

    public double getMinMap(String code, StockData data) {
        if(!minMap.containsKey(code)) {
            minMap.put(code, data.getSwing());
        }else {
            double min = minMap.get(code);
            minMap.put(code, Math.min(data.getSwing(), min));
        }

        return minMap.get(code);
    }

}
