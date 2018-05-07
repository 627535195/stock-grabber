package net.cloudstu.sg.dao;

import net.cloudstu.sg.entity.StockModel;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 股票dao
 *
 * @author zhiming.li
 * @date 2018/5/1
 */
@Repository
public interface StockDao {

    StockModel selectByName(@Param(value = "name") String name);

    StockModel selectLikeName(@Param(value = "name") String name);

    int update(@Param(value = "code") String code);

    List<StockModel> selectMonitored();

    int resetSelectedStocks();
}
