package net.cloudstu.sg.util.sinastock.data;

import lombok.Data;

@Data
public class StockData {
    private String name;
    private double currentPrice;
    private double swing;
    private double openPrice;
    private long amount;
}
