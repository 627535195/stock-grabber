package net.cloudstu.sg.dao;

import net.cloudstu.sg.entity.TrackerUserModel;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 被追踪的用户dao
 *
 * @author zhiming.li
 * @date 2018/4/20
 */
public interface TrackerUserDao {

    /**
     * 获取所有的被追踪者
     *
     * @return
     */
    List<TrackerUserModel> selectAll();

    /**
     * 按用户id获取被追踪者
     *
     * @param userId
     * @return
     */
    TrackerUserModel selectByUserId(@Param(value = "userId") long userId);
}
