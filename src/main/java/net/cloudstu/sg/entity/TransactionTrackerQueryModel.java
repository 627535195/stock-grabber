package net.cloudstu.sg.entity;

import lombok.Builder;

/**
 * @author zhiming.li
 * @date 2018/4/10
 */
@Builder
public class TransactionTrackerQueryModel {
    private long userId;
    private String name;
    private String action;
    private long applyTime;
}
