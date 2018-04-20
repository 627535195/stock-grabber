package net.cloudstu.sg.dao;

import net.cloudstu.sg.entity.StockHoldModel;

import java.util.List;

/**
 * 持仓dao
 *
 * @author zhiming.li
 * @date 2018/4/19
 */
public interface StockHoldDao{

    /**
     * 获取所有持仓股票
     *
     * @return
     */
    List<StockHoldModel> selectAll();
}
