package net.cloudstu.sg.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 交易跟踪实体
 *
 * @author zhiming.li
 * @date 2018/4/10
 */
@EqualsAndHashCode(exclude = {"id", "applyPrice", "price", "time", "state"})
@ToString
@Data
public class TransactionTrackerModel{
    private long id;
    private long userId;
    private String name;
    private String action;
    private double applyPrice;
    private double price;
    private long applyTime;
    private long time;
    private String state;
}