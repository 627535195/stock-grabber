package net.cloudstu.sg.factor;

import net.cloudstu.sg.util.sinastock.data.StockData;

/**
 * @author zhiming.li
 * @date 2018/5/22
 */
public class HighOpenFactor implements Factor{

    @Override
    public boolean isSatisfied(StockData data) {
        if(data.getSwing()>1) {
            return true;
        }

        return false;
    }
}
