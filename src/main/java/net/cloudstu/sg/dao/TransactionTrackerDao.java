package net.cloudstu.sg.dao;

import net.cloudstu.sg.entity.TransactionTrackerModel;
import net.cloudstu.sg.entity.TransactionTrackerQueryModel;
import org.springframework.stereotype.Repository;

/**
 * 交易跟踪
 *
 * @author zhiming.li
 * @date 2018/4/10
 */
@Repository
public interface TransactionTrackerDao extends BaseDao<TransactionTrackerModel,
        TransactionTrackerQueryModel>{

}
