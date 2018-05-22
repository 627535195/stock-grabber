package net.cloudstu.sg.factor;

import net.cloudstu.sg.util.sinastock.data.StockData;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zhiming.li
 * @date 2018/5/22
 */
public class LowOpenFactor implements Factor {
    public ConcurrentHashMap<String, Double> minMap = new ConcurrentHashMap<>();
    private static final double OPEN_FACTOR = -4.0;
    private static final double OPEN_FACTOR2 = -2.3;
    private static final double FACTOR = -1.5;

    @Override
    public boolean isSatisfied(String code, StockData data) {
        double min = getMinMap(code, data);

        if(data.getOpenSwing()<OPEN_FACTOR) {
            return false;
        }

        if(data.getOpenSwing()<OPEN_FACTOR2
                && data.getOpenPrice() - min  < FACTOR) {
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
