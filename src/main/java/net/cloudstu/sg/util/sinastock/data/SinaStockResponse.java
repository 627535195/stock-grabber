package net.cloudstu.sg.util.sinastock.data;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor()
@Data
public class SinaStockResponse {
	public static final int SUCCESS = 1;
	public static final int ERROR = -1;
	private int code;
	private String originalData;
	private StockData data;
}
