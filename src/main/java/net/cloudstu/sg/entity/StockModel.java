package net.cloudstu.sg.entity;

import lombok.Data;

/**
 * 股票
 *
 * @author zhiming.li
 * @date 2018/5/1
 */
@Data
public class StockModel {
    private String code;
    private String name;
    private boolean selected;
}
