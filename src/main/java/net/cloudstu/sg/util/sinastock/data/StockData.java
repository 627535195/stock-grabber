package net.cloudstu.sg.util.sinastock.data;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class StockData {
    private String name;
    private double currentPrice;
    private double swing;
    private double openPrice;
    private double openSwing;
    private long amount;
}
