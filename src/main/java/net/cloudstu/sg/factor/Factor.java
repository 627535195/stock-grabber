package net.cloudstu.sg.factor;

import net.cloudstu.sg.util.sinastock.data.StockData;

/**
 *
 * @author zhiming.li
 * @date 2018/5/22
 */
public interface Factor {

    /**
     * 是否满足相应条件
     *
     * @return
     */
    boolean isSatisfied(StockData data);

}
