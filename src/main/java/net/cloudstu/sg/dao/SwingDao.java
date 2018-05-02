package net.cloudstu.sg.dao;

import net.cloudstu.sg.entity.SwingModel;
import org.springframework.stereotype.Repository;

/**
 * 振幅记录dao
 *
 * @author zhiming.li
 * @date 2018/5/2
 */
@Repository
public interface SwingDao {
    void insert(SwingModel swingModel);
}
