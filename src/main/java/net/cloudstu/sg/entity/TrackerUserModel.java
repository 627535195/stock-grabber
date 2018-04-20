package net.cloudstu.sg.entity;

import lombok.Data;
import lombok.ToString;

/**
 * 被追踪的用户
 *
 * @author zhiming.li
 * @date 2018/4/20
 */
@Data
@ToString
public class TrackerUserModel {
    private long userId;
    private String userName;
    private int type;
    private int cnt;
}
