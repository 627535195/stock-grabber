package net.cloudstu.sg.dao;

import net.cloudstu.sg.entity.StockHoldModel;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 持仓dao
 *
 * @author zhiming.li
 * @date 2018/4/19
 */
@Repository
public interface StockHoldDao {

    /**
     * 获取所有持仓股票
     *
     * @return
     */
    List<StockHoldModel> selectAll();

    /**
     * 插入持仓股票
     *
     * @param stockHold
     */
    int insert(StockHoldModel stockHold);

    /**
     * 清空持仓股票
     *
     * @return
     */
    int clear();
}
