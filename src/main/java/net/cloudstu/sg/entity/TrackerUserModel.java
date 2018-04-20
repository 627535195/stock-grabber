package net.cloudstu.sg.entity;

import lombok.Data;

/**
 * 被追踪的用户
 *
 * @author zhiming.li
 * @date 2018/4/20
 */
@Data
public class TrackerUserModel {
    private long userId;
    private long userName;
    private int type;
}
