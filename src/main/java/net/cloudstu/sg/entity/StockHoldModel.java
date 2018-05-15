package net.cloudstu.sg.entity;

import lombok.Data;

import java.util.Date;

/**
 * 持仓
 *
 * @author zhiming.li
 * @date 2018/4/19
 */
@Data
public class StockHoldModel {
    private long id;
    private String code;
    private String name;
    private double nowPrice;
    private double expectPrice;
    private int hold;
    private int availableHold;
    private Date updateTime;
    private boolean sell;
}
